package com.gumthala.learningapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A single card within a SlideDeck, navigated with arrow controls. */
@Entity(tableName = "slides")
data class SlideEntity(
    @PrimaryKey val id: String,
    val deckId: String,
    val orderIndex: Int,
    /** The big glyph/word on the card, e.g. "A", "5 x 5 = 25", "क". Same across languages by nature. */
    val headline: String,
    val captionEn: String,
    val captionMr: String,
    val captionHi: String,
    val emoji: String? = null
)
