package com.gumthala.learningapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_answers")
data class QuizAnswerEntity(
    @PrimaryKey val id: String,
    val attemptId: String,
    val questionId: String,
    val selectedCanonicalIndex: Int,
    val wasCorrect: Boolean,
    val timeTakenMillis: Long
)
