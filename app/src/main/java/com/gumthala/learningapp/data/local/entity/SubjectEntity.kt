package com.gumthala.learningapp.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gumthala.learningapp.domain.model.TrilingualText

/** One row per subject per class, e.g. "maths-3" = Maths for Class 3. */
@Entity(tableName = "subjects")
data class SubjectEntity(
    @PrimaryKey val id: String,
    val subjectKey: String,
    val classLevel: Int,
    @Embedded(prefix = "name_") val name: TrilingualText,
    val colorFamily: String,
    val iconEmoji: String,
    val orderIndex: Int
)
