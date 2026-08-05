package com.gumthala.learningapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_attempts")
data class QuizAttemptEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val chapterId: String,
    val startedAtMillis: Long,
    val completedAtMillis: Long?,
    val correctCount: Int,
    val totalQuestions: Int,
    val starsEarned: Int,
    val xpEarned: Int,
    val isSynced: Boolean = false
)
