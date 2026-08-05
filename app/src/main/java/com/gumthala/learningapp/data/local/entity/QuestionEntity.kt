package com.gumthala.learningapp.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gumthala.learningapp.domain.model.TrilingualText

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val chapterId: String,
    val orderIndex: Int,
    @Embedded(prefix = "text_") val text: TrilingualText,
    val optionsEn: List<String>,
    val optionsMr: List<String>,
    val optionsHi: List<String>,
    /** Index into the canonical (unshuffled) options list that is correct. Same index across all 3 languages. */
    val correctIndex: Int,
    val difficulty: String,
    val imageUrl: String? = null,
    /** Null for built-in curriculum questions; set when a teacher authored this question. */
    val createdByTeacherId: String? = null,
    val isSynced: Boolean = false
)
