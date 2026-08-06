package com.gumthala.learningapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.core.AppLanguage
import com.gumthala.learningapp.data.repo.QuizOutcome
import com.gumthala.learningapp.data.repo.QuizRepository
import com.gumthala.learningapp.data.repo.SubmittedAnswer
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
}

/**
 * One real quiz attempt end-to-end: loads questions via [QuizEngine] (through
 * [QuizRepository.startQuiz], which already shuffles options per-question), walks
 * the student through them one at a time using the *existing* [QuizUiState] shape
 * (no screen changes needed — QuizScreen was already single-question-at-a-time),
 * and calls [QuizRepository.finishQuiz] on the last question to persist the
 * attempt, roll up progress, and award stars/badges.
 */
@HiltViewModel(assistedFactory = QuizViewModel.Factory::class)
class QuizViewModel @AssistedInject constructor(
    @Assisted private val chapterId: String,
    @Assisted private val userId: String,
    private val quizRepository: QuizRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(chapterId: String, userId: String): QuizViewModel
    }

    private val _screenState = MutableStateFlow<QuizScreenState>(QuizScreenState.Loading)
    val screenState: StateFlow<QuizScreenState> = _screenState.asStateFlow()

    private var quiz: Quiz? = null
    private var currentIndex = 0
    private var selectedOptionIndex: Int? = null
    private var startedAt = System.currentTimeMillis()
    private val answers = mutableListOf<SubmittedAnswer>()

    init {
        startedAt = System.currentTimeMillis()
        viewModelScope.launch {
            val loaded = quizRepository.startQuiz(chapterId, AppLanguage.ENGLISH) // TODO: student's chosen language
            quiz = loaded
            _screenState.value = if (loaded.questions.isEmpty()) QuizScreenState.Empty else buildInProgress()
        }
    }

    fun selectOption(index: Int) {
        val q = quiz ?: return
        val question = q.questions.getOrNull(currentIndex) ?: return
        if (selectedOptionIndex != null) return // already revealed; ignore further taps
        selectedOptionIndex = index
        val option = question.options.getOrNull(index)
        answers += SubmittedAnswer(
            questionId = question.questionId,
            selectedOptionId = option?.optionId,
            isCorrect = option?.isCorrect == true
        )
        _screenState.value = buildInProgress()
    }

    fun next() {
        val q = quiz ?: return
        if (selectedOptionIndex == null) return // must answer before advancing
        if (currentIndex >= q.questions.lastIndex) {
            finish()
        } else {
            currentIndex += 1
            selectedOptionIndex = null
            _screenState.value = buildInProgress()
        }
    }

    private fun finish() {
        viewModelScope.launch {
            val outcome = quizRepository.finishQuiz(
                userId = userId,
                chapterId = chapterId,
                startedAt = startedAt,
                answers = answers.toList()
            )
            _screenState.value = QuizScreenState.Finished(outcome)
        }
    }

    private fun buildInProgress(): QuizScreenState.InProgress {
        val q = quiz!!
        val question = q.questions[currentIndex]
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
