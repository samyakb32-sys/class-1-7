package com.gumthala.learningapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Rolling totals kept up to date after every quiz completion; backs Home/Profile stats and the leaderboard. */
@Entity(tableName = "student_stats")
data class StudentStatsEntity(
    @PrimaryKey val studentId: String,
    val classLevel: Int,
    val xp: Int = 0,
    val coins: Int = 0,
    val streakDays: Int = 0,
    val lastActiveEpochDay: Long = 0,
    val lessonsCompleted: Int = 0
)
