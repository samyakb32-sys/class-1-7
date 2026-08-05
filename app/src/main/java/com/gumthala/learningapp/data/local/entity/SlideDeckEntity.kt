package com.gumthala.learningapp.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gumthala.learningapp.domain.model.TrilingualText

/** A "Teaching Slides" deck, e.g. "A-Z Alphabet", "Multiplication Table 5", "Marathi Barakhadi". */
@Entity(tableName = "slide_decks")
data class SlideDeckEntity(
    @PrimaryKey val id: String,
    @Embedded(prefix = "title_") val title: TrilingualText,
    /** "default" decks ship with the app; "custom" decks were authored by a teacher. */
    val category: String,
    val createdByTeacherId: String? = null,
    val classLevel: Int? = null,
    val orderIndex: Int,
    val isSynced: Boolean = false
)
