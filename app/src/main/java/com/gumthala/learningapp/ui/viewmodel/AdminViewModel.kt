package com.gumthala.learningapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.core.ClassLevels
import com.gumthala.learningapp.core.UserRole
import com.gumthala.learningapp.data.repo.AuthRepository
import com.gumthala.learningapp.data.repo.ContentRepository
import com.gumthala.learningapp.data.repo.QuizRepository
import com.gumthala.learningapp.data.repo.RegisterResult
import com.gumthala.learningapp.ui.screens.admin.AdminOverview
import com.gumthala.learningapp.ui.screens.admin.ManagedStudentRow
import com.gumthala.learningapp.ui.screens.roster.RegisterStudentInput
import com.gumthala.learningapp.ui.screens.roster.RegisterTeacherInput
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Scoped to one signed-in admin. Unlike [TeacherViewModel], an admin sees every
 * class (no assignedClassLevels restriction) — matches AuthRepository.registerStudent
 * / registerStaff, which only gate on role == ADMIN, no class check.
 */
@HiltViewModel(assistedFactory = AdminViewModel.Factory::class)
class AdminViewModel @AssistedInject constructor(
    @Assisted private val adminUserId: String,
    private val authRepository: AuthRepository,
    private val quizRepository: QuizRepository,
    private val contentRepository: ContentRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(adminUserId: String): AdminViewModel
    }

    private fun progressPercent(chaptersCompleted: Int, totalStars: Int): Int =
        if (chaptersCompleted == 0) 0 else (totalStars * 100 / (chaptersCompleted * 3)).coerceIn(0, 100)

    private val allStudentRows = quizRepository.observeStudentProgress(ClassLevels.ALL)
    private val teacherCount = authRepository.observeByRole(UserRole.TEACHER).map { it.size }
    private val questionCount = contentRepository.observeTotalQuestionCount()

    val overview: StateFlow<AdminOverview> = combine(
        allStudentRows, teacherCount, questionCount
    ) { rows, teachers, questions ->
        val avg = if (rows.isEmpty()) 0 else rows
            .map { progressPercent(it.chaptersCompleted, it.totalStars) }
            .average()
            .toInt()
        AdminOverview(
            adminName = "", // filled in by the caller from AuthViewModel's session name
            studentCount = rows.size,
            teacherCount = teachers,
            avgProgressPct = avg,
            questionCount = questions
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminOverview("", 0, 0, 0, 0))

    val students: StateFlow<List<ManagedStudentRow>> = allStudentRows
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

    private val _registerStudentState = MutableStateFlow(RegisterStudentUiState())
    val registerStudentState: StateFlow<RegisterStudentUiState> = _registerStudentState.asStateFlow()

    fun registerStudent(input: RegisterStudentInput) {
        if (_registerStudentState.value.isSubmitting) return
        _registerStudentState.value = RegisterStudentUiState(isSubmitting = true)
        viewModelScope.launch {
            runCatching {
                authRepository.registerStudent(
                    actorId = adminUserId,
                    fullName = input.name,
                    classLevel = input.classLevel,
                    rollNo = input.rollNo
                )
            }.fold(
                onSuccess = { result ->
                    _registerStudentState.value = when (result) {
                        is RegisterResult.Success -> RegisterStudentUiState(
                            success = "${result.user.fullName} added to Class ${input.classLevel}."
                        )
                        RegisterResult.StudentAlreadyExists -> RegisterStudentUiState(
                            error = "A student with that name already exists in Class ${input.classLevel}."
                        )
                        else -> RegisterStudentUiState(error = "Something went wrong. Try again.")
                    }
                },
                onFailure = { _registerStudentState.value = RegisterStudentUiState(error = "Something went wrong. Try again.") }
            )
        }
    }

    fun consumeRegisterStudentResult() {
        _registerStudentState.value = RegisterStudentUiState()
    }

    private val _registerTeacherState = MutableStateFlow(RegisterTeacherUiState())
    val registerTeacherState: StateFlow<RegisterTeacherUiState> = _registerTeacherState.asStateFlow()

    fun registerTeacher(input: RegisterTeacherInput) {
        if (_registerTeacherState.value.isSubmitting) return
        _registerTeacherState.value = RegisterTeacherUiState(isSubmitting = true)
        viewModelScope.launch {
            runCatching {
                authRepository.registerStaff(
                    actorId = adminUserId,
                    fullName = input.name,
                    email = input.email,
                    password = input.temporaryPassword,
                    role = UserRole.TEACHER,
                    assignedClasses = input.assignedClasses
                )
            }.fold(
                onSuccess = { result ->
                    _registerTeacherState.value = when (result) {
                        is RegisterResult.Success -> RegisterTeacherUiState(
                            success = "${result.user.fullName} added as a teacher."
                        )
                        RegisterResult.EmailAlreadyUsed -> RegisterTeacherUiState(
                            error = "That email is already registered."
                        )
                        else -> RegisterTeacherUiState(error = "Something went wrong. Try again.")
                    }
                },
                onFailure = { _registerTeacherState.value = RegisterTeacherUiState(error = "Something went wrong. Try again.") }
            )
        }
    }

    fun consumeRegisterTeacherResult() {
        _registerTeacherState.value = RegisterTeacherUiState()
    }
}

data class RegisterTeacherUiState(
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val success: String? = null
)
