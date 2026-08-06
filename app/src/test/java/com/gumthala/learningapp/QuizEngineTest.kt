package com.gumthala.learningapp

import com.gumthala.learningapp.data.local.entity.QuestionEntity
import com.gumthala.learningapp.domain.model.AppLanguage
import com.gumthala.learningapp.domain.model.TrilingualText
import com.gumthala.learningapp.domain.quiz.QuizEngine
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class QuizEngineTest {

    private fun question(correctIndex: Int) = QuestionEntity(
        id = "q1",
        chapterId = "c1",
        orderIndex = 0,
        text = TrilingualText("What is 2+2?", "2+2 किती?", "2+2 कितना?"),
        optionsEn = listOf("4", "5", "6", "7"),
        optionsMr = listOf("4", "5", "6", "7"),
        optionsHi = listOf("4", "5", "6", "7"),
        correctIndex = correctIndex,
        difficulty = "easy"
    )

    @Test
    fun `shuffled options always contain the same correct value at the reported index`() {
        val q = question(correctIndex = 0)
        repeat(500) { seed ->
            val shuffled = QuizEngine.buildQuiz(listOf(q), AppLanguage.ENGLISH, Random(seed))
            val result = shuffled.single()
            assertEquals("4", result.displayOptions[result.correctDisplayIndex])
            assertEquals(4, result.displayOptions.toSet().size, "options must not be dropped/duplicated by shuffling")
        }
    }

    @Test
    fun `correct answer position is not always the same index across many shuffles`() {
        val q = question(correctIndex = 0)
        val positionsSeen = (0 until 200).map { seed ->
            QuizEngine.buildQuiz(listOf(q), AppLanguage.ENGLISH, Random(seed)).single().correctDisplayIndex
        }.toSet()
        assertTrue(positionsSeen.size > 1, "correct answer should land in more than one position across shuffles, saw: $positionsSeen")
    }

    @Test
    fun `scoreAttempt star and xp thresholds`() {
        assertEquals(3, QuizEngine.scoreAttempt(10, 10).starsEarned)
        assertEquals(120, QuizEngine.scoreAttempt(10, 10).xpEarned) // 10*10 + 20 bonus
        assertEquals(2, QuizEngine.scoreAttempt(6, 10).starsEarned)
        assertEquals(1, QuizEngine.scoreAttempt(1, 10).starsEarned)
        assertEquals(0, QuizEngine.scoreAttempt(0, 10).starsEarned)
        assertEquals(0, QuizEngine.scoreAttempt(0, 0).starsEarned)
    }
}
