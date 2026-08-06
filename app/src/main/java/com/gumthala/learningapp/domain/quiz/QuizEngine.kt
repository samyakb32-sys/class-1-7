package com.gumthala.learningapp.domain.quiz

import com.gumthala.learningapp.data.local.entity.QuestionEntity
import com.gumthala.learningapp.domain.model.AppLanguage
import kotlin.random.Random

/** A question with its options shuffled into a fresh, unpredictable on-screen order. */
data class ShuffledQuestion(
    val question: QuestionEntity,
    val displayOptions: List<String>,
    val correctDisplayIndex: Int
)

data class QuizResult(
    val correctCount: Int,
    val totalQuestions: Int,
    val starsEarned: Int,
    val xpEarned: Int
)

object QuizEngine {

    /** Shuffles each question's answer options independently so the correct answer's position is never predictable. */
    fun buildQuiz(questions: List<QuestionEntity>, language: AppLanguage, random: Random = Random.Default): List<ShuffledQuestion> =
        questions.map { question -> shuffleOptions(question, language, random) }

    private fun shuffleOptions(question: QuestionEntity, language: AppLanguage, random: Random): ShuffledQuestion {
        val canonicalOptions = when (language) {
            AppLanguage.ENGLISH -> question.optionsEn
            AppLanguage.MARATHI -> question.optionsMr
            AppLanguage.HINDI -> question.optionsHi
        }
        val order = canonicalOptions.indices.shuffled(random)
        val displayOptions = order.map { canonicalOptions[it] }
        val correctDisplayIndex = order.indexOf(question.correctIndex)
        return ShuffledQuestion(question, displayOptions, correctDisplayIndex)
    }

    /** 1-3 stars based on accuracy, plus 10 XP per correct answer and a 20 XP full-marks bonus. */
    fun scoreAttempt(correctCount: Int, totalQuestions: Int): QuizResult {
        val accuracy = if (totalQuestions == 0) 0f else correctCount.toFloat() / totalQuestions
        val stars = when {
            accuracy >= 0.9f -> 3
            accuracy >= 0.6f -> 2
            accuracy > 0f -> 1
            else -> 0
        }
        val xp = correctCount * 10 + if (correctCount == totalQuestions && totalQuestions > 0) 20 else 0
        return QuizResult(correctCount, totalQuestions, stars, xp)
    }
}
