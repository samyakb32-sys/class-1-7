package com.gumthala.learningapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.core.AppLanguage
import com.gumthala.learningapp.data.repo.SlideRepository
import com.gumthala.learningapp.ui.screens.SubjectCardUi
import com.gumthala.learningapp.ui.screens.teacher.TeachingSlide
import com.gumthala.learningapp.ui.theme.SubjectPalette
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DeckListState(val decks: List<SubjectCardUi> = emptyList())
data class SlideViewerState(val title: String = "", val slides: List<TeachingSlide> = emptyList(), val currentIndex: Int = 0)

private val deckCategoryEmoji = mapOf("alphabet" to "🔤", "tables" to "🔢", "barakhadi" to "अ", "custom" to "📝")

/**
 * Backs Teaching Slides for one teacher: the default decks (A–Z, tables 1–10,
 * both barakhadis) seeded by DefaultSlides, plus any custom decks that teacher
 * has authored. Currently read-only for content — adding a custom slide from
 * the UI is still a TODO (SlideRepository.saveCustomDeck exists and is ready
 * for it, just not wired to a form yet).
 */
@HiltViewModel(assistedFactory = SlideViewModel.Factory::class)
class SlideViewModel @AssistedInject constructor(
    @Assisted private val teacherUserId: String,
    private val slideRepository: SlideRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(teacherUserId: String): SlideViewModel
    }

    private val language = AppLanguage.ENGLISH

    val deckListState: StateFlow<DeckListState> = slideRepository.observeDecks(teacherUserId)
        .map { decks ->
            DeckListState(
                decks.map { deck ->
                    SubjectCardUi(
                        id = deck.id,
                        emoji = deckCategoryEmoji[deck.category] ?: "📄",
                        title = deck.title.get(language),
                        badge = if (deck.isDefault) "Default" else "My Deck",
                        meta = deck.category.replaceFirstChar { it.uppercase() },
                        palette = SubjectPalette.all[deck.orderIndex % SubjectPalette.all.size]
                    )
                }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DeckListState())

    private val selectedDeckId = MutableStateFlow<String?>(null)
    private val currentIndex = MutableStateFlow(0)

    private val deckContent = selectedDeckId.flatMapLatest { deckId ->
        if (deckId == null) flowOf(null) else slideRepository.observeDeck(deckId)
    }

    val viewerState: StateFlow<SlideViewerState> = combine(deckContent, currentIndex) { deck, index ->
        val slides = deck?.slides?.sortedBy { it.orderIndex }?.map {
            TeachingSlide(headline = it.headline, caption = it.caption.get(language))
        }.orEmpty()
        SlideViewerState(
            title = deck?.deck?.title?.get(language).orEmpty(),
            slides = slides,
            currentIndex = index.coerceIn(0, (slides.size - 1).coerceAtLeast(0))
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SlideViewerState())

    fun openDeck(deckId: String) {
        selectedDeckId.value = deckId
        currentIndex.value = 0
    }

    fun previous() {
        if (currentIndex.value > 0) currentIndex.value -= 1
    }

    fun next() {
        val last = viewerState.value.slides.lastIndex
        if (currentIndex.value < last) currentIndex.value += 1
    }
}
