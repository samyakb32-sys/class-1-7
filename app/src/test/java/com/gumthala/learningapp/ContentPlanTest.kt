package com.gumthala.learningapp

import com.gumthala.learningapp.data.seed.LanguageChapterPlans
import com.gumthala.learningapp.data.seed.MathChapterPlans
import com.gumthala.learningapp.domain.model.AppLanguage
import com.gumthala.learningapp.domain.model.ContentConstants
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * End-to-end sanity check mirroring what ContentSeeder does for every class/subject:
 * every chapter plan must produce exactly QUESTIONS_PER_CHAPTER well-formed questions
 * without throwing, for every class 1-7.
 */
class ContentPlanTest {

    @Test
    fun `every Maths chapter for every class generates well-formed questions without throwing`() {
        for (classLevel in 1..7) {
            val plans = MathChapterPlans.forClass(classLevel)
            assertEquals(5, plans.size, "class $classLevel should have 5 Maths chapters")
            plans.forEach { plan ->
                assertTrue(plan.generators.isNotEmpty(), "chapter '${plan.titleEn}' has no generators")
                val random = Random("$classLevel-${plan.titleEn}".hashCode())
                val questions = (0 until ContentConstants.QUESTIONS_PER_CHAPTER).map { i ->
                    plan.generators[i % plan.generators.size](random)
                }
                assertEquals(ContentConstants.QUESTIONS_PER_CHAPTER, questions.size)
                questions.forEach { q ->
                    assertEquals(4, q.optionsEn.size, "class $classLevel / ${plan.titleEn}: $q")
                    assertEquals(4, q.optionsEn.toSet().size, "duplicate options in class $classLevel / ${plan.titleEn}: $q")
                    assertTrue(q.correctIndex in 0..3)
                }
            }
        }
    }

    @Test
    fun `every language subject chapter for every class generates well-formed questions without throwing`() {
        for (classLevel in 1..7) {
            for (isDevanagari in listOf(true, false)) {
                val plans = LanguageChapterPlans.forClass(classLevel, isDevanagari)
                assertEquals(5, plans.size, "class $classLevel (devanagari=$isDevanagari) should have 5 chapters")
                plans.forEach { plan ->
                    assertTrue(plan.generators.isNotEmpty(), "chapter '${plan.titleEn}' has no generators")
                    val random = Random("$classLevel-$isDevanagari-${plan.titleEn}".hashCode())
                    val targetLanguage = if (isDevanagari) AppLanguage.MARATHI else AppLanguage.ENGLISH
                    val questions = (0 until ContentConstants.QUESTIONS_PER_CHAPTER).map { i ->
                        plan.generators[i % plan.generators.size](random, targetLanguage)
                    }
                    assertEquals(ContentConstants.QUESTIONS_PER_CHAPTER, questions.size)
                    questions.forEach { q ->
                        assertEquals(4, q.optionsEn.size, "class $classLevel / ${plan.titleEn}: $q")
                        assertEquals(4, q.optionsEn.toSet().size, "duplicate options in class $classLevel / ${plan.titleEn}: $q")
                        assertTrue(q.correctIndex in 0..3)
                    }
                }
            }
        }
    }
}
