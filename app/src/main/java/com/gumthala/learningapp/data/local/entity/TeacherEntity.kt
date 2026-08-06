package com.gumthala.learningapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teachers")
data class TeacherEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    /** Classes (1-7) this teacher is assigned to teach / can view progress for. */
    val assignedClasses: List<Int>,
    val createdAtMillis: Long,
    val isSynced: Boolean = false
)
