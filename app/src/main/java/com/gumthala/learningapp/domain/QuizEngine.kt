package com.gumthala.learningapp.domain

import com.gumthala.learningapp.core.AppLanguage
import com.gumthala.learningapp.data.local.QuestionWithOptions
import kotlin.random.Random

data class QuizOption(
    val optionId: String,
    val text: String,
    val imageRef: String?,
    val isCorrect: Boolean
)

data class QuizQuestion(
    val questionId: String,
    val prompt: String,
    val imageRef: String?,
    val hint: String?,
    /** already shuffled — render in this order */
    val options: List<QuizOption>
) {
    val correctOptionId: String? get() = options.firstOrNull { it.isCorrect }?.optionId
}

data class Quiz(
    val chapterId: String,
    val language: AppLanguage,
    val questions: List<QuizQuestion>
)

/**
 * Builds a playable quiz from stored content.
 *
 * Options are shuffled per question with a fresh [Random], and the shuffle is
 * rejected and retried if the correct answer lands in the same slot as the
 * previous question — so a student can never learn "it's always B".
 */
object QuizEngine {

    fun build(
        chapterId: String,
        source: List<QuestionWithOptions>,
        language: AppLanguage,
        shuffleQuestions: Boolean = true,
        random: Random = Random(System.nanoTime())
    ): Quiz {
        val ordered = if (shuffleQuestions) source.shuffled(random) else source
        var previousCorrectIndex = -1

        val questions = ordered.mapNotNull { row ->
            if (row.options.isEmpty()) return@mapNotNull null

            var shuffled = row.options.shuffled(random)
            var correctIndex = shuffled.indexOfFirst { it.isCorrect }

            // Avoid repeating the previous question's correct slot when we can.
            var guard = 0
            while (shuffled.size > 1 && correctIndex == previousCorrectIndex && guard < 8) {
                shuffled = row.options.shuffled(random)
                correctIndex = shuffled.indexOfFirst { it.isCorrect }
                guard++
            }
            previousCorrectIndex = correctIndex

            QuizQuestion(
                questionId = row.question.id,
                prompt = row.question.prompt.get(language),
                imageRef = row.question.imageRef,
                hint = row.question.hint.get(language).takeIf { it.isNotBlank() },
                options = shuffled.map {
                    QuizOption(
                        optionId = it.id,
                        text = it.text.get(language),
                        imageRef = it.imageRef,
                        isCorrect = it.isCorrect
                    )
                }
            )
        }
        return Quiz(chapterId = chapterId, language = language, questions = questions)
    }
}
