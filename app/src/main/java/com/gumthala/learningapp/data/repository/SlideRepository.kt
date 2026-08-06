package com.gumthala.learningapp.data.repository

import com.gumthala.learningapp.data.local.dao.SlideDao
import com.gumthala.learningapp.data.local.entity.SlideDeckEntity
import com.gumthala.learningapp.data.local.entity.SlideEntity

class SlideRepository(private val slideDao: SlideDao) {
    fun observeAllDecks() = slideDao.observeAllDecks()
    fun observeDecksByTeacher(teacherId: String) = slideDao.observeDecksByTeacher(teacherId)
    fun observeSlides(deckId: String) = slideDao.observeSlidesForDeck(deckId)

    suspend fun seedDefaultDecksIfEmpty(decks: List<SlideDeckEntity>, slides: List<SlideEntity>) {
        if (slideDao.deckCount() == 0) {
            slideDao.upsertDecks(decks)
            slideDao.upsertSlides(slides)
        }
    }

    suspend fun addCustomDeck(deck: SlideDeckEntity, slides: List<SlideEntity>) {
        slideDao.upsertDeck(deck)
        slideDao.upsertSlides(slides)
    }
}
