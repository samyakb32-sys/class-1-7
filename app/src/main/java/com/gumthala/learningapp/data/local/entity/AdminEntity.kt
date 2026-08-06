package com.gumthala.learningapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "admins")
data class AdminEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val passwordSalt: String,
    val createdAtMillis: Long,
    val isSynced: Boolean = false
)
