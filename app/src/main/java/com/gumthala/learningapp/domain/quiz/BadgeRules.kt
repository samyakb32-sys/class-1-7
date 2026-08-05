package com.gumthala.learningapp.domain.quiz

/** Badge keys awarded by ProgressRepository as a student plays; UI maps each key to icon/copy. */
object BadgeRules {
    const val FIRST_QUIZ = "first_quiz"
    const val PERFECT_SCORE = "perfect_score"
    const val STREAK_3 = "streak_3"
    const val STREAK_7 = "streak_7"
    const val STREAK_30 = "streak_30"

    fun badgesForStreak(streakDays: Int): List<String> = buildList {
        if (streakDays >= 3) add(STREAK_3)
        if (streakDays >= 7) add(STREAK_7)
        if (streakDays >= 30) add(STREAK_30)
    }
}
