package com.gumthala.learningapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A student can only sign in by picking their name from this table — there is no self-signup.
 * Rows are created by a Teacher (into their own assigned classes) or an Admin (any class).
 */
@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val name: String,
    val classLevel: Int,
    val registeredByUserId: String,
    val registeredByRole: String,
    val avatarEmoji: String = "🧒",
    val createdAtMillis: Long,
    val isSynced: Boolean = false
)
