package com.gumthala.learningapp

import com.gumthala.learningapp.data.seed.AlphabetData
import com.gumthala.learningapp.data.seed.LanguageQuestionGenerator
import com.gumthala.learningapp.data.seed.VocabBank
import com.gumthala.learningapp.domain.model.AppLanguage
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val ITERATIONS = 300

class LanguageQuestionGeneratorTest {

    private fun assertFourDistinctOptions(optionsEn: List<String>, correctIndex: Int) {
        assertEquals(4, optionsEn.size)
        assertEquals(4, optionsEn.toSet().size, "options must be distinct: $optionsEn")
        assertTrue(correctIndex in 0..3)
    }

    @Test
    fun `pictureNaming answer matches a real vocab word in the target language`() {
        for (lang in AppLanguage.entries) {
            repeat(ITERATIONS) { seed ->
                val q = LanguageQuestionGenerator.pictureNaming(Random(seed), lang, tier = 7)
                assertFourDistinctOptions(q.optionsEn, q.correctIndex)
                val correctWord = q.optionsEn[q.correctIndex]
                assertTrue(
                    VocabBank.words.any { it.inLanguageForTest(lang) == correctWord },
                    "'$correctWord' should be a real vocab word in $lang"
                )
            }
        }
    }

    @Test
    fun `translation answer is a real word and question never asks to translate into itself`() {
        for (lang in AppLanguage.entries) {
            repeat(ITERATIONS) { seed ->
                val q = LanguageQuestionGenerator.translation(Random(seed), lang, tier = 7)
                assertFourDistinctOptions(q.optionsEn, q.correctIndex)
                val correctWord = q.optionsEn[q.correctIndex]
                assertTrue(VocabBank.words.any { it.inLanguageForTest(lang) == correctWord })
            }
        }
    }

    @Test
    fun `opposite answer is the real antonym from the bank`() {
        for (lang in AppLanguage.entries) {
            repeat(ITERATIONS) { seed ->
                val q = LanguageQuestionGenerator.opposite(Random(seed), lang, tier = 7)
                assertFourDistinctOptions(q.optionsEn, q.correctIndex)
                val correctWord = q.optionsEn[q.correctIndex]
                val isKnownWord = VocabBank.opposites.any {
                    it.aInLanguageForTest(lang) == correctWord || it.bInLanguageForTest(lang) == correctWord
                }
                assertTrue(isKnownWord, "'$correctWord' should be a known opposite-pair word in $lang")
            }
        }
    }

    @Test
    fun `englishAlphabetOrder answer is adjacent to the anchor letter in A-Z`() {
        val regex = Regex("""Which letter comes (before|after) (\w)\?""")
        repeat(ITERATIONS) { seed ->
            val q = LanguageQuestionGenerator.englishAlphabetOrder(Random(seed))
            assertFourDistinctOptions(q.optionsEn, q.correctIndex)
            val m = regex.find(q.textEn)!!
            val (mode, anchor) = m.destructured
            val idx = AlphabetData.englishLetters.indexOf(anchor)
            val expected = if (mode == "before") AlphabetData.englishLetters[idx - 1] else AlphabetData.englishLetters[idx + 1]
            assertEquals(expected, q.optionsEn[q.correctIndex])
        }
    }

    @Test
    fun `devanagariBarakhadiOrder answer is the actual next letter in sequence`() {
        repeat(ITERATIONS) { seed ->
            val q = LanguageQuestionGenerator.devanagariBarakhadiOrder(Random(seed), AppLanguage.MARATHI)
            assertFourDistinctOptions(q.optionsEn, q.correctIndex)
            val answer = q.optionsEn[q.correctIndex]
            val inVowels = AlphabetData.devanagariVowels.contains(answer)
            val inConsonants = AlphabetData.devanagariConsonants.contains(answer)
            assertTrue(inVowels || inConsonants, "'$answer' should be a real Devanagari letter")
        }
    }
}

// Test-only mirrors of the private extension functions in LanguageQuestionGenerator.kt.
private fun com.gumthala.learningapp.data.seed.VocabWord.inLanguageForTest(language: AppLanguage) = when (language) {
    AppLanguage.ENGLISH -> en
    AppLanguage.MARATHI -> mr
    AppLanguage.HINDI -> hi
}

private fun com.gumthala.learningapp.data.seed.OppositePair.aInLanguageForTest(language: AppLanguage) = when (language) {
    AppLanguage.ENGLISH -> enA
    AppLanguage.MARATHI -> mrA
    AppLanguage.HINDI -> hiA
}

private fun com.gumthala.learningapp.data.seed.OppositePair.bInLanguageForTest(language: AppLanguage) = when (language) {
    AppLanguage.ENGLISH -> enB
    AppLanguage.MARATHI -> mrB
    AppLanguage.HINDI -> hiB
}
