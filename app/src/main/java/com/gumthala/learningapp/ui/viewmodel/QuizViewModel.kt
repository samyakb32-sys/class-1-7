package com.gumthala.learningapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.core.AppLanguage
import com.gumthala.learningapp.data.repo.QuizOutcome
import com.gumthala.learningapp.data.repo.QuizRepository
import com.gumthala.learningapp.data.repo.SubmittedAnswer
import com.gumthala.learningapp.domain.DifficultyLevel
import com.gumthala.learningapp.domain.Quiz
import com.gumthala.learningapp.ui.screens.QuizUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface QuizScreenState {
    data object Loading : QuizScreenState
    data class InProgress(val ui: QuizUiState) : QuizScreenState
    data class Finished(val outcome: QuizOutcome) : QuizScreenState
    data object Empty : QuizScreenState
    /** Something made the quiz state inconsistent (shouldn't happen, but never crash — recover here instead). */
    data object Error : QuizScreenState
}

/**
 * One real quiz attempt end-to-end: loads questions via QuizEngine (through
 * [QuizRepository.startQuiz], which already shuffles options per-question and
 * can filter by [difficulty]), walks the student through them one at a time
 * using the *existing* [QuizUiState] shape, and calls
 * [QuizRepository.finishQuiz] on the last question to persist the attempt,
 * roll up progress, and award stars/badges.
 *
 * Crash-safety note: every state read below goes through [current] /
 * [currentQuestion], which never throw — an inconsistent state (quiz not
 * loaded yet, index out of range, whatever) produces [QuizScreenState.Error]
 * instead of an exception. A student on stage should never see a red crash
 * screen; worst case here is "something went wrong, go back" — this is a
 * deliberate design decision, not an oversight, per the "must not crash"
 * requirement.
 */
@HiltViewModel(assistedFactory = QuizViewModel.Factory::class)
class QuizViewModel @AssistedInject constructor(
    @Assisted("chapterId") private val chapterId: String,
    @Assisted("userId") private val userId: String,
    @Assisted private val difficulty: DifficultyLevel,
    private val quizRepository: QuizRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        // chapterId and userId are both String — Dagger can't tell them apart without
        // identifiers on both the factory method and the constructor above. difficulty
        // is a distinct type, so it needs none. (First real compile error caught by CI:
        // this exact ambiguity — assisted-inject params of the same type need @Assisted("id").)
        fun create(
            @Assisted("chapterId") chapterId: String,
            @Assisted("userId") userId: String,
            difficulty: DifficultyLevel
        ): QuizViewModel
    }

    private val _screenState = MutableStateFlow<QuizScreenState>(QuizScreenState.Loading)
    val screenState: StateFlow<QuizScreenState> = _screenState.asStateFlow()

    private var quiz: Quiz? = null
    private var currentIndex = 0
    private var selectedOptionIndex: Int? = null
    private val startedAt = System.currentTimeMillis()
    private val answers = mutableListOf<SubmittedAnswer>()

    init {
        viewModelScope.launch {
            runCatching {
                quizRepository.startQuiz(chapterId, AppLanguage.ENGLISH, difficulty) // TODO: student's chosen language
            }.onSuccess { loaded ->
                quiz = loaded
                _screenState.value = if (loaded.questions.isEmpty()) QuizScreenState.Empty else render()
            }.onFailure {
                _screenState.value = QuizScreenState.Error
            }
        }
    }

    /** Current question, or null if anything is inconsistent — never throws. */
    private fun currentQuestion() = quiz?.questions?.getOrNull(currentIndex)

    fun selectOption(index: Int) {
        val question = currentQuestion() ?: run { _screenState.value = QuizScreenState.Error; return }
        if (selectedOptionIndex != null) return // already revealed; ignore further taps
        selectedOptionIndex = index
        val option = question.options.getOrNull(index)
        answers += SubmittedAnswer(
            questionId = question.questionId,
            selectedOptionId = option?.optionId,
            isCorrect = option?.isCorrect == true
        )
        _screenState.value = render()
    }

    fun next() {
        val total = quiz?.questions?.size ?: run { _screenState.value = QuizScreenState.Error; return }
        if (selectedOptionIndex == null) return // must answer before advancing
        if (currentIndex >= total - 1) {
            finish()
        } else {
            currentIndex += 1
            selectedOptionIndex = null
            _screenState.value = render()
        }
    }

    private fun finish() {
        viewModelScope.launch {
            runCatching {
                quizRepository.finishQuiz(
                    userId = userId,
                    chapterId = chapterId,
                    startedAt = startedAt,
                    answers = answers.toList()
                )
            }.onSuccess { outcome ->
                _screenState.value = QuizScreenState.Finished(outcome)
            }.onFailure {
                _screenState.value = QuizScreenState.Error
            }
        }
    }

    /** Builds the next UI state from current fields. Never throws — falls back to [QuizScreenState.Error]. */
    private fun render(): QuizScreenState {
        val q = quiz ?: return QuizScreenState.Error
        val question = q.questions.getOrNull(currentIndex) ?: return QuizScreenState.Error
        val correctIndex = question.options.indexOfFirst { it.isCorrect }
        val revealed = selectedOptionIndex != null
        return QuizScreenState.InProgress(
            QuizUiState(
                title = "Quiz Time!",
                questionNumber = currentIndex + 1,
                questionCount = q.questions.size,
                timerLabel = "",
                questionText = question.prompt,
                options = question.options.map { it.text },
                correctIndex = correctIndex,
                selectedIndex = selectedOptionIndex,
                revealed = revealed,
                feedbackTitle = if (!revealed) "" else {
                    if (selectedOptionIndex == correctIndex) "Correct! 🎉" else "Not quite"
                },
                feedbackDetail = if (!revealed) "" else question.hint ?: "",
                nextLabel = if (currentIndex >= q.questions.lastIndex) "Finish Quiz" else "Next Question"
            )
        )
    }
}
