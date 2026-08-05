package com.gumthala.learningapp.data.seed

import com.gumthala.learningapp.data.local.entity.ChapterEntity
import com.gumthala.learningapp.data.local.entity.QuestionEntity
import com.gumthala.learningapp.data.local.entity.SlideDeckEntity
import com.gumthala.learningapp.data.local.entity.SlideEntity
import com.gumthala.learningapp.data.local.entity.SubjectEntity
import com.gumthala.learningapp.data.repository.ContentRepository
import com.gumthala.learningapp.data.repository.SlideRepository
import com.gumthala.learningapp.domain.model.AppLanguage
import com.gumthala.learningapp.domain.model.TrilingualText
import kotlin.random.Random

/**
 * Builds the full curriculum (4 subjects x classes 1-7 x 5 chapters x 12 questions) and the
 * default Teaching Slides deck, then seeds them into Room on first launch only.
 *
 * Chapter *structure* (titles/descriptions) is curated per subject/class; every question's
 * numbers/vocabulary are procedurally generated (see MathQuestionGenerator / LanguageQuestionGenerator)
 * so they are guaranteed correct rather than hand-typed. This is a first content pass meant to be
 * reviewed and expanded by the school's Teachers/Admins via the in-app question editor.
 */
object ContentSeeder {

    private data class SubjectMeta(val key: String, val nameEn: String, val nameMr: String, val nameHi: String, val color: String, val icon: String)

    private val subjectMetas = listOf(
        SubjectMeta("maths", "Maths", "गणित", "गणित", "blue", "🔢"),
        SubjectMeta("english", "English", "इंग्रजी", "अंग्रेज़ी", "pink", "📖"),
        SubjectMeta("marathi", "Marathi", "मराठी", "मराठी", "orange", "✍️"),
        SubjectMeta("hindi", "Hindi", "हिंदी", "हिंदी", "violet", "🅰️")
    )

    suspend fun seedIfNeeded(contentRepository: ContentRepository, slideRepository: SlideRepository) {
        val subjects = mutableListOf<SubjectEntity>()
        val chapters = mutableListOf<ChapterEntity>()
        val questions = mutableListOf<QuestionEntity>()

        for (classLevel in 1..7) {
            subjectMetas.forEachIndexed { subjectOrder, meta ->
                val subjectId = "${meta.key}-$classLevel"
                subjects += SubjectEntity(
                    id = subjectId,
                    subjectKey = meta.key,
                    classLevel = classLevel,
                    name = TrilingualText(meta.nameEn, meta.nameMr, meta.nameHi),
                    colorFamily = meta.color,
                    iconEmoji = meta.icon,
                    orderIndex = subjectOrder
                )

                val random = Random(subjectId.hashCode())

                if (meta.key == "maths") {
                    MathChapterPlans.forClass(classLevel).forEachIndexed { chapterIndex, plan ->
                        val chapterId = "$subjectId-ch$chapterIndex"
                        val generated = (0 until QUESTIONS_PER_CHAPTER).map { i ->
                            plan.generators[i % plan.generators.size](random)
                        }
                        chapters += buildChapter(chapterId, subjectId, classLevel, chapterIndex, plan.titleEn, plan.titleMr, plan.titleHi, generated.first())
                        questions += generated.mapIndexed { i, g -> toQuestionEntity(chapterId, i, g) }
                    }
                } else {
                    val targetLanguage = when (meta.key) {
                        "english" -> AppLanguage.ENGLISH
                        "marathi" -> AppLanguage.MARATHI
                        else -> AppLanguage.HINDI
                    }
                    val isDevanagari = meta.key != "english"
                    LanguageChapterPlans.forClass(classLevel, isDevanagari).forEachIndexed { chapterIndex, plan ->
                        val chapterId = "$subjectId-ch$chapterIndex"
                        val generated = (0 until QUESTIONS_PER_CHAPTER).map { i ->
                            plan.generators[i % plan.generators.size](random, targetLanguage)
                        }
                        chapters += buildChapter(chapterId, subjectId, classLevel, chapterIndex, plan.titleEn, plan.titleMr, plan.titleHi, generated.first())
                        questions += generated.mapIndexed { i, g -> toQuestionEntity(chapterId, i, g) }
                    }
                }
            }
        }

        contentRepository.seedSubjectsIfEmpty(subjects)
        contentRepository.seedChaptersIfEmpty(chapters)
        contentRepository.seedQuestionsIfEmpty(questions)

        val (decks, slides) = buildDefaultSlideDecks()
        slideRepository.seedDefaultDecksIfEmpty(decks, slides)
    }

    private fun buildChapter(
        chapterId: String, subjectId: String, classLevel: Int, orderIndex: Int,
        titleEn: String, titleMr: String, titleHi: String, sample: GeneratedQuestion
    ) = ChapterEntity(
        id = chapterId,
        subjectId = subjectId,
        classLevel = classLevel,
        orderIndex = orderIndex,
        title = TrilingualText(titleEn, titleMr, titleHi),
        description = TrilingualText(
            "Practice problems on $titleEn.",
            "$titleMr वर सराव प्रश्न.",
            "$titleHi पर अभ्यास प्रश्न।"
        ),
        example = TrilingualText(
            "${sample.textEn} Answer: ${sample.optionsEn[sample.correctIndex]}",
            "${sample.textMr} उत्तर: ${sample.optionsMr[sample.correctIndex]}",
            "${sample.textHi} उत्तर: ${sample.optionsHi[sample.correctIndex]}"
        )
    )

    private fun toQuestionEntity(chapterId: String, index: Int, g: GeneratedQuestion) = QuestionEntity(
        id = "$chapterId-q$index",
        chapterId = chapterId,
        orderIndex = index,
        text = TrilingualText(g.textEn, g.textMr, g.textHi),
        optionsEn = g.optionsEn,
        optionsMr = g.optionsMr,
        optionsHi = g.optionsHi,
        correctIndex = g.correctIndex,
        difficulty = g.difficulty
    )

    private fun buildDefaultSlideDecks(): Pair<List<SlideDeckEntity>, List<SlideEntity>> {
        val decks = mutableListOf<SlideDeckEntity>()
        val slides = mutableListOf<SlideEntity>()

        decks += SlideDeckEntity("deck-az", TrilingualText("A-Z Alphabet", "इंग्रजी मुळाक्षरे A-Z", "अंग्रेज़ी वर्णमाला A-Z"), "default", orderIndex = 0)
        AlphabetData.englishLetters.forEachIndexed { i, letter ->
            slides += SlideEntity("deck-az-s$i", "deck-az", i, letter, "The letter $letter", "अक्षर $letter", "अक्षर $letter")
        }

        decks += SlideDeckEntity("deck-tables", TrilingualText("Multiplication Tables 1-10", "पाढे १ ते १०", "पहाड़े 1 से 10"), "default", orderIndex = 1)
        var tableSlideIndex = 0
        for (table in 1..10) {
            for (multiplier in 1..10) {
                slides += SlideEntity(
                    "deck-tables-s$tableSlideIndex", "deck-tables", tableSlideIndex,
                    "$table x $multiplier = ${table * multiplier}",
                    "Table of $table", "पाढा $table", "पहाड़ा $table"
                )
                tableSlideIndex++
            }
        }

        decks += SlideDeckEntity("deck-mr-barakhadi", TrilingualText("Marathi Barakhadi", "मराठी बाराखडी", "मराठी बारहखड़ी"), "default", orderIndex = 2)
        (AlphabetData.devanagariVowels + AlphabetData.devanagariConsonants).forEachIndexed { i, letter ->
            slides += SlideEntity("deck-mr-barakhadi-s$i", "deck-mr-barakhadi", i, letter, "Marathi letter $letter", "अक्षर $letter", "अक्षर $letter")
        }

        decks += SlideDeckEntity("deck-hi-barakhadi", TrilingualText("Hindi Barakhadi", "हिंदी बाराखडी", "हिंदी बारहखड़ी"), "default", orderIndex = 3)
        (AlphabetData.devanagariVowels + AlphabetData.devanagariConsonants).forEachIndexed { i, letter ->
            slides += SlideEntity("deck-hi-barakhadi-s$i", "deck-hi-barakhadi", i, letter, "Hindi letter $letter", "अक्षर $letter", "अक्षर $letter")
        }

        return decks to slides
    }

    private const val QUESTIONS_PER_CHAPTER = com.gumthala.learningapp.domain.model.ContentConstants.QUESTIONS_PER_CHAPTER
}
