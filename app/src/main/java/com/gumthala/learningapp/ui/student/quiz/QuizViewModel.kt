package com.gumthala.learningapp.ui.student.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.data.local.entity.QuizAnswerEntity
import com.gumthala.learningapp.data.local.entity.QuizAttemptEntity
import com.gumthala.learningapp.data.repository.ContentRepository
import com.gumthala.learningapp.data.repository.QuizRepository
import com.gumthala.learningapp.domain.model.AppLanguage
import com.gumthala.learningapp.domain.quiz.QuizEngine
import com.gumthala.learningapp.domain.quiz.QuizResult
import com.gumthala.learningapp.domain.quiz.ShuffledQuestion
import com.gumthala.learningapp.ui.student.StudentSession
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class QuizUiState(
    val loading: Boolean = true,
    val questions: List<ShuffledQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val selectedIndex: Int? = null,
    val revealed: Boolean = false,
    val correctCount: Int = 0,
    val elapsedSeconds: Int = 0,
    val quizResult: QuizResult? = null
) {
    val currentQuestion get() = questions.getOrNull(currentIndex)
    val isLastQuestion get() = currentIndex == questions.lastIndex
}

class QuizViewModel(
    private val chapterId: String,
    private val session: StudentSession,
    private val contentRepository: ContentRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    private var attempt: QuizAttemptEntity? = null
    private val answers = mutableListOf<QuizAnswerEntity>()
    private var questionStartedAt = 0L

    init {
        viewModelScope.launch {
            val questions = contentRepository.observeQuestions(chapterId)
            questions.collect { list ->
                if (list.isNotEmpty() && _uiState.value.questions.isEmpty()) {
                    val shuffled = QuizEngine.buildQuiz(list, session.language)
                    attempt = quizRepository.startAttempt(session.studentId, chapterId, shuffled.size)
                    questionStartedAt = System.currentTimeMillis()
                    _uiState.value = QuizUiState(loading = false, questions = shuffled)
                    startTimer()
                }
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            while (_uiState.value.quizResult == null) {
                delay(1000)
                _uiState.value = _uiState.value.copy(elapsedSeconds = _uiState.value.elapsedSeconds + 1)
            }
        }
    }

    fun selectAnswer(displayIndex: Int) {
        val state = _uiState.value
        if (state.revealed) return
        val question = state.currentQuestion ?: return
        val wasCorrect = displayIndex == question.correctDisplayIndex
        answers += QuizAnswerEntity(
            id = UUID.randomUUID().toString(),
            attemptId = attempt?.id.orEmpty(),
            questionId = question.question.id,
            selectedCanonicalIndex = displayIndex,
            wasCorrect = wasCorrect,
            timeTakenMillis = System.currentTimeMillis() - questionStartedAt
        )
        _uiState.value = state.copy(
            selectedIndex = displayIndex,
            revealed = true,
            correctCount = state.correctCount + if (wasCorrect) 1 else 0
        )
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (!state.revealed) return
        if (state.isLastQuestion) {
            finishQuiz()
            return
        }
        questionStartedAt = System.currentTimeMillis()
        _uiState.value = state.copy(
            currentIndex = state.currentIndex + 1,
            selectedIndex = null,
            revealed = false
        )
    }

    private fun finishQuiz() {
        val currentAttempt = attempt ?: return
        viewModelScope.launch {
            val result = quizRepository.completeAttempt(currentAttempt, session.classLevel, answers)
            _uiState.value = _uiState.value.copy(quizResult = result)
        }
    }

    fun questionAnsweredCorrectly(): Boolean = answers.lastOrNull()?.wasCorrect == true
}
