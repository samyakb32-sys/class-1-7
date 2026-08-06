package com.gumthala.learningapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.data.repo.AuthRepository
import com.gumthala.learningapp.data.repo.QuizRepository
import com.gumthala.learningapp.data.repo.RegisterResult
import com.gumthala.learningapp.data.repo.assignedClassLevels
import com.gumthala.learningapp.ui.screens.admin.ManagedStudentRow
import com.gumthala.learningapp.ui.screens.roster.RegisterStudentInput
import com.gumthala.learningapp.ui.screens.teacher.TeacherOverview
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class RegisterStudentUiState(
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

/**
 * Scoped to one signed-in teacher (assisted-injected by [teacherUserId]).
 * Reads their own [assignedClassLevels] from Room rather than trusting a passed-in
 * list, so the class chips on Register Student can never offer a class the
 * teacher isn't actually assigned to.
 */
@HiltViewModel(assistedFactory = TeacherViewModel.Factory::class)
class TeacherViewModel @AssistedInject constructor(
    @Assisted private val teacherUserId: String,
    private val authRepository: AuthRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(teacherUserId: String): TeacherViewModel
    }

    private val teacherFlow = kotlinx.coroutines.flow.flow {
        emit(authRepository.user(teacherUserId))
    }

    val assignedClasses: StateFlow<List<Int>> = teacherFlow
        .map { it?.assignedClassLevels().orEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Rough progress percent per student: stars earned vs. max possible (3 per
     * completed chapter). There's no cleaner "percent of syllabus done" metric
     * without a denominator of total chapters per class, which isn't tracked
     * per-student yet — this is an approximation, not the mockup's precise %.
     */
    private fun progressPercent(chaptersCompleted: Int, totalStars: Int): Int =
        if (chaptersCompleted == 0) 0 else (totalStars * 100 / (chaptersCompleted * 3)).coerceIn(0, 100)

    private val studentRows = assignedClasses.flatMapLatest { classes ->
        quizRepository.observeStudentProgress(classes)
    }

    val overview: StateFlow<TeacherOverview> = studentRows
        .map { rows ->
            val avg = if (rows.isEmpty()) 0 else rows
                .map { progressPercent(it.chaptersCompleted, it.totalStars) }
                .average()
                .toInt()
            TeacherOverview(
                teacherName = "", // filled in by the caller from AuthViewModel's session name
                myStudentCount = rows.size,
                avgProgressPct = avg
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TeacherOverview("", 0, 0))

    val students: StateFlow<List<ManagedStudentRow>> = studentRows
        .map { rows ->
            rows.map { row ->
                ManagedStudentRow(
                    id = row.userId,
                    avatarEmoji = "🧒",
                    fullName = row.fullName,
                    classLabel = "Class ${row.classLevel}",
                    progressPct = progressPercent(row.chaptersCompleted, row.totalStars)
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _registerState = MutableStateFlow(RegisterStudentUiState())
    val registerState: StateFlow<RegisterStudentUiState> = _registerState.asStateFlow()

    fun registerStudent(input: RegisterStudentInput) {
        if (_registerState.value.isSubmitting) return
        _registerState.value = RegisterStudentUiState(isSubmitting = true)
        viewModelScope.launch {
            val result = authRepository.registerStudent(
                actorId = teacherUserId,
                fullName = input.name,
                classLevel = input.classLevel,
                rollNo = input.rollNo
            )
            _registerState.value = when (result) {
                is RegisterResult.Success -> RegisterStudentUiState(
                    success = "${result.user.fullName} added to Class ${input.classLevel}."
                )
                RegisterResult.StudentAlreadyExists -> RegisterStudentUiState(
                    error = "A student with that name already exists in Class ${input.classLevel}."
                )
                RegisterResult.NotPermitted -> RegisterStudentUiState(
                    error = "You're not assigned to Class ${input.classLevel}."
                )
                else -> RegisterStudentUiState(error = "Something went wrong. Try again.")
            }
        }
    }

    fun consumeRegisterResult() {
        _registerState.value = RegisterStudentUiState()
    }
}
