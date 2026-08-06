package com.gumthala.learningapp.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gumthala.learningapp.domain.model.TrilingualText

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey val id: String,
    val subjectId: String,
    val classLevel: Int,
    val orderIndex: Int,
    @Embedded(prefix = "title_") val title: TrilingualText,
    @Embedded(prefix = "desc_") val description: TrilingualText,
    @Embedded(prefix = "example_") val example: TrilingualText
)
