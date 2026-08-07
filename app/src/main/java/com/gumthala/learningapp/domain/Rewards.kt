package com.gumthala.learningapp.domain

import kotlin.math.roundToInt

/** Star thresholds, kept generous — this is play, not an exam. */
object Rewards {

    const val MAX_STARS = 3

    fun stars(correct: Int, total: Int): Int {
        if (total <= 0) return 0
        val pct = (correct * 100f / total).roundToInt()
        return when {
            pct >= 90 -> 3
            pct >= 70 -> 2
            pct >= 45 -> 1
            else -> 0
        }
    }

    /** How loud the Candy Burst celebration should be. */
    fun celebrationTier(stars: Int): CelebrationTier = when (stars) {
        3 -> CelebrationTier.SPECTACULAR
        2 -> CelebrationTier.GREAT
        1 -> CelebrationTier.GOOD
        else -> CelebrationTier.ENCOURAGE
    }
}

enum class CelebrationTier { ENCOURAGE, GOOD, GREAT, SPECTACULAR }

/**
 * Badge catalogue. [code] is stored; the label/description come from string
 * resources so they stay trilingual.
 */
enum class BadgeCode(val code: String) {
    FIRST_ADVENTURE("first_adventure"),
    THREE_STAR("three_star"),
    FIVE_CHAPTERS("five_chapters"),
    TEN_CHAPTERS("ten_chapters"),
    TWENTY_FIVE_STARS("twenty_five_stars"),
    FIFTY_STARS("fifty_stars"),
    SUBJECT_EXPLORER("subject_explorer"),
    PERFECT_RUN("perfect_run");

    companion object {
        fun evaluate(
            alreadyEarned: Set<String>,
            totalStars: Int,
            chaptersCompleted: Int,
            lastAttemptStars: Int,
            lastAttemptPerfect: Boolean
        ): List<BadgeCode> = buildList {
            fun offer(badge: BadgeCode, condition: Boolean) {
                if (condition && badge.code !in alreadyEarned) add(badge)
            }
            offer(FIRST_ADVENTURE, chaptersCompleted >= 1)
            offer(THREE_STAR, lastAttemptStars == 3)
            offer(PERFECT_RUN, lastAttemptPerfect)
            offer(FIVE_CHAPTERS, chaptersCompleted >= 5)
            offer(TEN_CHAPTERS, chaptersCompleted >= 10)
            offer(TWENTY_FIVE_STARS, totalStars >= 25)
            offer(FIFTY_STARS, totalStars >= 50)
        }
    }
}

/**
 * Low / Medium / High as seen by students. Maps onto QuestionEntity.difficulty
 * (1 = easy .. 5 = stretch, already on every question) rather than adding a new
 * column — no migration needed.
 *
 * Bands overlap slightly by design: if a chapter has thin coverage at the
 * edges, a strict non-overlapping split (e.g. High = only 5) could leave a
 * band with too few or zero questions. QuizRepository.startQuiz also falls
 * back to the full chapter if a filtered band comes up empty — belt and
 * braces so a student can never open a truly blank quiz.
 */
enum class DifficultyLevel(val label: String, val range: IntRange) {
    LOW("Low", 1..2),
    MEDIUM("Medium", 2..4),
    HIGH("High", 4..5)
}
