package com.gumthala.learningapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String,
    val studentId: String,
    val badgeKey: String,
    val earnedAtMillis: Long
)
