package com.gumthala.learningapp.data.repo

import com.gumthala.learningapp.core.LocalizedText
import com.gumthala.learningapp.data.local.SlideDao
import com.gumthala.learningapp.data.local.SlideDeckEntity
import com.gumthala.learningapp.data.local.SlideEntity
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class SlideDraft(
    val headline: String,
    val caption: LocalizedText = LocalizedText(""),
    val imageRef: String? = null
)

@Singleton
class SlideRepository @Inject constructor(
    private val slideDao: SlideDao
) {
    fun observeDecks(teacherId: String) = slideDao.observeDecksFor(teacherId)
    fun observeDeck(deckId: String) = slideDao.observeDeck(deckId)

    suspend fun saveCustomDeck(
        ownerUserId: String,
        title: LocalizedText,
        slides: List<SlideDraft>,
        classLevel: Int? = null,
        deckId: String? = null
    ): String {
        val id = deckId ?: UUID.randomUUID().toString()
        slideDao.upsertDeck(
            SlideDeckEntity(
                id = id,
                title = title,
                category = "custom",
                ownerUserId = ownerUserId,
                isDefault = false,
                classLevel = classLevel,
                updatedAt = System.currentTimeMillis(),
                isSynced = false
            )
        )
        slideDao.deleteSlidesFor(id)
        slideDao.upsertSlides(
            slides.mapIndexed { index, draft ->
                SlideEntity(
                    id = UUID.randomUUID().toString(),
                    deckId = id,
                    orderIndex = index,
                    headline = draft.headline,
                    caption = draft.caption,
                    imageRef = draft.imageRef
                )
            }
        )
        return id
    }

    suspend fun deleteCustomDeck(deckId: String) = slideDao.deleteCustomDeck(deckId)
}
