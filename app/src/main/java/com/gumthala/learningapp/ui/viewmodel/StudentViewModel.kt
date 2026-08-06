package com.gumthala.learningapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.core.AppLanguage
import com.gumthala.learningapp.data.repo.ContentRepository
import com.gumthala.learningapp.data.repo.QuizRepository
import com.gumthala.learningapp.ui.screens.SubjectCardUi
import com.gumthala.learningapp.ui.theme.SubjectPalette
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class StudentSubjectsState(val subjects: List<SubjectCardUi> = emptyList())
data class StudentChaptersState(val subjectTitle: String = "", val chapters: List<SubjectCardUi> = emptyList())
data class StudentProgressState(
    val totalStars: Int = 0,
    val chaptersCompleted: Int = 0,
    val subjectBreakdown: List<Pair<String, Int>> = emptyList()
)

/**
 * Backs Subjects / (an implicit chapter list, reusing the Subjects card layout) /
 * Progress for one signed-in student. Scoped to the student's own [userId] and
 * [classLevel] via assisted injection since there's no real Navigation Compose
 * graph yet to carry nav args — see [AppNavHost] wiring.
 *
 * Deliberately does NOT cover Lesson or Home: chapters in the data model only
 * hold a title/blurb + quiz, not teaching-content body text, so LessonScreen's
 * paragraph/example fields have nothing real to bind to yet. Home's XP/Coins/
 * Streak/weekly-bar fields are cosmetic mockup extras with no backing columns
 * (see README "Mockup vs spec") — left as demo state rather than faked.
 */
@HiltViewModel(assistedFactory = StudentViewModel.Factory::class)
class StudentViewModel @AssistedInject constructor(
    @Assisted private val userId: String,
    @Assisted private val classLevel: Int,
    private val contentRepository: ContentRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(userId: String, classLevel: Int): StudentViewModel
    }

    private val language = AppLanguage.ENGLISH // TODO: source from the student's chosen login language
    private val palettes = SubjectPalette.all
    private val emojiBySubjectCode = mapOf(
        "maths" to "🔢", "english" to "📖", "marathi" to "✍️", "hindi" to "अ"
    )

    val subjectsState: StateFlow<StudentSubjectsState> = contentRepository.observeSubjects()
        .map { subjects ->
            StudentSubjectsState(
                subjects.mapIndexed { index, subject ->
                    SubjectCardUi(
                        id = subject.id,
                        emoji = emojiBySubjectCode[subject.code] ?: "📘",
                        title = subject.name.get(language),
                        badge = "Class $classLevel",
                        meta = "Tap to see chapters",
                        palette = palettes[index % palettes.size]
                    )
                }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StudentSubjectsState())

    private val selectedSubjectId = MutableStateFlow<String?>(null)

    val chaptersState: StateFlow<StudentChaptersState> = selectedSubjectId
        .flatMapLatest { subjectId ->
            if (subjectId == null) {
                kotlinx.coroutines.flow.flowOf(StudentChaptersState())
            } else {
                contentRepository.observeChapterProgress(userId, subjectId, classLevel).map { rows ->
                    StudentChaptersState(
                        chapters = rows.map { row ->
                            val stars = "⭐".repeat(row.stars).ifEmpty { "☆" }
                            SubjectCardUi(
                                id = row.chapter.id,
                                emoji = row.chapter.iconKey ?: "📄",
                                title = row.chapter.title.get(language),
                                badge = "${row.questionCount}Q",
                                meta = if (row.attemptCount > 0) "$stars · best ${row.bestCorrect}/${row.bestTotal}" else "Not attempted yet",
                                palette = palettes[row.chapter.orderIndex % palettes.size]
                            )
                        }
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StudentChaptersState())

    fun selectSubject(subjectId: String) {
        selectedSubjectId.value = subjectId
    }

    val progressState: StateFlow<StudentProgressState> = combine(
        quizRepository.observeTotalStars(userId),
        quizRepository.observeChaptersCompleted(userId)
    ) { totalStars, chaptersCompleted ->
        StudentProgressState(totalStars = totalStars, chaptersCompleted = chaptersCompleted)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StudentProgressState())
}
