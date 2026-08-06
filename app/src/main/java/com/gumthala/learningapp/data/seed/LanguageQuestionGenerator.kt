package com.gumthala.learningapp.data.seed

import com.gumthala.learningapp.domain.model.AppLanguage
import kotlin.random.Random

/**
 * Generates vocabulary/language questions for the English, Marathi and Hindi subjects.
 * [targetLanguage] controls which language the *answer options* are written in (i.e. which
 * subject this is for); the question stem itself is always given in all three UI languages.
 */
object LanguageQuestionGenerator {

    private fun VocabWord.inLanguage(language: AppLanguage): String = when (language) {
        AppLanguage.ENGLISH -> en
        AppLanguage.MARATHI -> mr
        AppLanguage.HINDI -> hi
    }

    private fun OppositePair.aInLanguage(language: AppLanguage): String = when (language) {
        AppLanguage.ENGLISH -> enA
        AppLanguage.MARATHI -> mrA
        AppLanguage.HINDI -> hiA
    }

    private fun OppositePair.bInLanguage(language: AppLanguage): String = when (language) {
        AppLanguage.ENGLISH -> enB
        AppLanguage.MARATHI -> mrB
        AppLanguage.HINDI -> hiB
    }

    private fun languageName(language: AppLanguage) = when (language) {
        AppLanguage.ENGLISH -> Triple("English", "इंग्रजी", "अंग्रेज़ी")
        AppLanguage.MARATHI -> Triple("Marathi", "मराठी", "मराठी")
        AppLanguage.HINDI -> Triple("Hindi", "हिंदी", "हिंदी")
    }

    /** "What is this called in {target}? {emoji}" — answer options are all in [targetLanguage]. */
    fun pictureNaming(random: Random, targetLanguage: AppLanguage, tier: Int): GeneratedQuestion {
        val pool = VocabBank.wordsUpToTier(tier)
        val word = pool.random(random)
        val (nameEn, nameMr, nameHi) = languageName(targetLanguage)
        val correct = word.inLanguage(targetLanguage)
        val distractors = pool.filter { it != word }.shuffled(random).take(3).map { it.inLanguage(targetLanguage) }
        val options = (listOf(correct) + distractors)
        return GeneratedQuestion(
            textEn = "What is this called in $nameEn? ${word.emoji}",
            textMr = "$nameMr मध्ये याला काय म्हणतात? ${word.emoji}",
            textHi = "$nameHi में इसे क्या कहते हैं? ${word.emoji}",
            optionsEn = options,
            optionsMr = options,
            optionsHi = options,
            correctIndex = 0,
            difficulty = "easy"
        )
    }

    /** Translate a word from a random other language into [targetLanguage]. */
    fun translation(random: Random, targetLanguage: AppLanguage, tier: Int): GeneratedQuestion {
        val pool = VocabBank.wordsUpToTier(tier)
        val word = pool.random(random)
        val sourceLanguage = AppLanguage.entries.filter { it != targetLanguage }.random(random)
        val (srcEn, srcMr, srcHi) = languageName(sourceLanguage)
        val (tgtEn, tgtMr, tgtHi) = languageName(targetLanguage)
        val sourceWord = word.inLanguage(sourceLanguage)
        val correct = word.inLanguage(targetLanguage)
        val distractors = pool.filter { it != word }.shuffled(random).take(3).map { it.inLanguage(targetLanguage) }
        val options = (listOf(correct) + distractors)
        return GeneratedQuestion(
            textEn = "What is \"$sourceWord\" ($srcEn) in $tgtEn?",
            textMr = "\"$sourceWord\" ($srcMr) याला $tgtMr मध्ये काय म्हणतात?",
            textHi = "\"$sourceWord\" ($srcHi) को $tgtHi में क्या कहते हैं?",
            optionsEn = options,
            optionsMr = options,
            optionsHi = options,
            correctIndex = 0,
            difficulty = "medium"
        )
    }

    /** "What is the opposite of {word}?" answered in [targetLanguage]. */
    fun opposite(random: Random, targetLanguage: AppLanguage, tier: Int): GeneratedQuestion {
        val pool = VocabBank.oppositesUpToTier(tier)
        val pair = pool.random(random)
        val askReverse = random.nextBoolean()
        val prompt = if (askReverse) pair.bInLanguage(targetLanguage) else pair.aInLanguage(targetLanguage)
        val correct = if (askReverse) pair.aInLanguage(targetLanguage) else pair.bInLanguage(targetLanguage)
        val distractorPool = pool.filter { it != pair }
            .map { if (askReverse) it.aInLanguage(targetLanguage) else it.bInLanguage(targetLanguage) }
        val distractors = distractorPool.shuffled(random).take(3)
        val options = (listOf(correct) + distractors)
        return GeneratedQuestion(
            textEn = "What is the opposite of \"$prompt\"?",
            textMr = "\"$prompt\" याचा विरुद्धार्थी शब्द कोणता?",
            textHi = "\"$prompt\" का विलोम शब्द क्या है?",
            optionsEn = options,
            optionsMr = options,
            optionsHi = options,
            correctIndex = 0,
            difficulty = "medium"
        )
    }

    /** English A-Z sequencing: "Which letter comes after X?" */
    fun englishAlphabetOrder(random: Random): GeneratedQuestion {
        val letters = AlphabetData.englishLetters
        val index = random.nextInt(0, letters.size - 1)
        val askNext = random.nextBoolean() && index > 0
        val correct = if (askNext) letters[index - 1] else letters[index + 1]
        val anchor = letters[index]
        val distractors = letters.filter { it != correct && it != anchor }.shuffled(random).take(3)
        val options = (listOf(correct) + distractors)
        return GeneratedQuestion(
            textEn = if (askNext) "Which letter comes before $anchor?" else "Which letter comes after $anchor?",
            textMr = if (askNext) "$anchor च्या आधी कोणते अक्षर येते?" else "$anchor नंतर कोणते अक्षर येते?",
            textHi = if (askNext) "$anchor से पहले कौन सा अक्षर आता है?" else "$anchor के बाद कौन सा अक्षर आता है?",
            optionsEn = options,
            optionsMr = options,
            optionsHi = options,
            correctIndex = 0,
            difficulty = "easy"
        )
    }

    /** Devanagari barakhadi sequencing, shared by the Marathi and Hindi subjects. */
    fun devanagariBarakhadiOrder(random: Random, targetLanguage: AppLanguage): GeneratedQuestion {
        val useVowels = random.nextBoolean()
        val sequence = if (useVowels) AlphabetData.devanagariVowels else AlphabetData.devanagariConsonants
        val index = random.nextInt(0, sequence.size - 1)
        val anchor = sequence[index]
        val correct = sequence[index + 1]
        val distractors = sequence.filter { it != correct && it != anchor }.shuffled(random).take(3)
        val options = (listOf(correct) + distractors)
        val (_, tgtMr, tgtHi) = languageName(targetLanguage)
        return GeneratedQuestion(
            textEn = "In the $tgtMr barakhadi, which letter comes after $anchor?",
            textMr = "बाराखडीत $anchor नंतर कोणते अक्षर येते?",
            textHi = "बारहखड़ी में $anchor के बाद कौन सा अक्षर आता है?",
            optionsEn = options,
            optionsMr = options,
            optionsHi = options,
            correctIndex = 0,
            difficulty = "easy"
        )
    }
}
