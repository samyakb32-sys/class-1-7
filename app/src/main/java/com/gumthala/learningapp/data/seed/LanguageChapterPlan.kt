package com.gumthala.learningapp.data.seed

import com.gumthala.learningapp.domain.model.AppLanguage
import kotlin.random.Random

data class LanguageChapterPlan(
    val titleEn: String, val titleMr: String, val titleHi: String,
    val generators: List<(Random, AppLanguage) -> GeneratedQuestion>
)

/**
 * Five chapters per class, reused identically for the English, Marathi and Hindi subjects
 * (only the target language of the answer options changes). Vocabulary difficulty (tier)
 * rises with class level.
 */
object LanguageChapterPlans {

    fun forClass(classLevel: Int, isDevanagariSubject: Boolean): List<LanguageChapterPlan> {
        val tier = classLevel
        if (classLevel == 1) {
            return listOf(
                if (isDevanagariSubject) {
                    LanguageChapterPlan("Barakhadi & Sounds", "बाराखडी आणि उच्चार", "बारहखड़ी और उच्चारण",
                        listOf({ r, lang -> LanguageQuestionGenerator.devanagariBarakhadiOrder(r, lang) }))
                } else {
                    LanguageChapterPlan("Alphabet & Sounds", "मुळाक्षरे आणि उच्चार", "वर्णमाला और उच्चारण",
                        listOf({ r, _ -> LanguageQuestionGenerator.englishAlphabetOrder(r) }))
                },
                LanguageChapterPlan("Picture Naming", "चित्र ओळख", "चित्र पहचान",
                    listOf({ r, lang -> LanguageQuestionGenerator.pictureNaming(r, lang, tier) })),
                LanguageChapterPlan("Simple Words", "साधे शब्द", "सरल शब्द",
                    listOf({ r, lang -> LanguageQuestionGenerator.pictureNaming(r, lang, tier) })),
                LanguageChapterPlan("Opposites", "विरुद्धार्थी शब्द", "विलोम शब्द",
                    listOf({ r, lang -> LanguageQuestionGenerator.opposite(r, lang, tier) })),
                LanguageChapterPlan("Word Match", "शब्द जुळवा", "शब्द मिलान",
                    listOf({ r, lang -> LanguageQuestionGenerator.translation(r, lang, tier) }))
            )
        }
        val stageTitles = when (classLevel) {
            2 -> Triple("Vocabulary Builder", "Picture & Word Match", "Opposites & Antonyms")
            3 -> Triple("Word Power", "Synonyms & Antonyms", "Reading Vocabulary")
            4 -> Triple("Advanced Vocabulary", "Synonyms & Antonyms", "Word Usage")
            5 -> Triple("Rich Vocabulary", "Synonyms & Antonyms", "Contextual Words")
            6 -> Triple("Extensive Vocabulary", "Synonyms & Antonyms", "Precise Word Choice")
            else -> Triple("Mastery Vocabulary", "Nuanced Synonyms & Antonyms", "Contextual Word Use")
        }
        return listOf(
            LanguageChapterPlan(stageTitles.first, "शब्दसंग्रह वाढ", "शब्दावली विकास",
                listOf({ r, lang -> LanguageQuestionGenerator.pictureNaming(r, lang, tier) })),
            LanguageChapterPlan(stageTitles.second, "समानार्थी व विरुद्धार्थी शब्द", "पर्यायवाची व विलोम शब्द",
                listOf({ r, lang -> LanguageQuestionGenerator.opposite(r, lang, tier) })),
            LanguageChapterPlan(stageTitles.third, "शब्दांचा वापर", "शब्द प्रयोग",
                listOf({ r, lang -> LanguageQuestionGenerator.translation(r, lang, tier) })),
            LanguageChapterPlan("Word Meanings", "शब्दांचे अर्थ", "शब्दों के अर्थ",
                listOf({ r, lang -> LanguageQuestionGenerator.pictureNaming(r, lang, tier) }, { r, lang -> LanguageQuestionGenerator.translation(r, lang, tier) })),
            LanguageChapterPlan("Language Skills", "भाषा कौशल्य", "भाषा कौशल",
                listOf({ r, lang -> LanguageQuestionGenerator.opposite(r, lang, tier) }, { r, lang -> LanguageQuestionGenerator.translation(r, lang, tier) }))
        )
    }
}
