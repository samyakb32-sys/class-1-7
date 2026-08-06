package com.gumthala.learningapp.data.seed

import com.gumthala.learningapp.core.LocalizedText
import kotlinx.serialization.Serializable

/**
 * Shape of the JSON in app/src/main/assets/seed/. One file per subject keeps the
 * files small enough to hand-edit; the loader merges them all.
 */
@Serializable
data class SeedFile(
    val version: Int,
    val subject: SeedSubject,
    val classes: List<SeedClass>
)

@Serializable
data class SeedSubject(
    val code: String,
    val name: LocalizedText,
    val iconKey: String,
    val colorHex: String,
    val orderIndex: Int
)

@Serializable
data class SeedClass(
    val classLevel: Int,
    val chapters: List<SeedChapter>
)

@Serializable
data class SeedChapter(
    val id: String,
    val title: LocalizedText,
    val blurb: LocalizedText? = null,
    val iconKey: String? = null,
    val questions: List<SeedQuestion>
)

@Serializable
data class SeedQuestion(
    val id: String,
    val prompt: LocalizedText,
    val imageRef: String? = null,
    val hint: LocalizedText? = null,
    val difficulty: Int = 3,
    val options: List<SeedOption>
)

@Serializable
data class SeedOption(
    val text: LocalizedText,
    val imageRef: String? = null,
    val correct: Boolean = false
)
