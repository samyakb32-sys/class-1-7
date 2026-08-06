package com.gumthala.learningapp.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class QuestionWithOptions(
    @Embedded val question: QuestionEntity,
    @Relation(parentColumn = "id", entityColumn = "questionId")
    val options: List<OptionEntity>
)

data class DeckWithSlides(
    @Embedded val deck: SlideDeckEntity,
    @Relation(parentColumn = "id", entityColumn = "deckId")
    val slides: List<SlideEntity>
)

/** One row per student for the per-class leaderboard. */
data class LeaderboardRow(
    val userId: String,
    val fullName: String,
    val avatarKey: String?,
    val classLevel: Int,
    val totalStars: Int,
    val chaptersCompleted: Int
)

/** Teacher/admin view of how a student is doing. */
data class StudentProgressRow(
    val userId: String,
    val fullName: String,
    val classLevel: Int,
    val chaptersCompleted: Int,
    val totalStars: Int,
    val lastActiveAt: Long?
)

data class ChapterProgressRow(
    @Embedded val chapter: ChapterEntity,
    val questionCount: Int,
    val stars: Int,
    val bestCorrect: Int,
    val bestTotal: Int,
    val attemptCount: Int
)
