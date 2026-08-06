package com.gumthala.learningapp.ui.slides

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.data.local.entity.SlideDeckEntity
import com.gumthala.learningapp.data.local.entity.SlideEntity
import com.gumthala.learningapp.data.repository.SlideRepository
import com.gumthala.learningapp.domain.model.TrilingualText
import kotlinx.coroutines.launch
import java.util.UUID

private enum class DecksScreenState { LIST, VIEWER, NEW_DECK }

/**
 * Plain, unstyled placeholder pending a mockup — "Teaching Slides" deck browser. Shows the
 * default decks (A-Z, tables, barakhadi) plus any this teacher has authored, and lets the
 * teacher add a new custom deck. [teacherId] is null for Admin (browse-only, no authoring).
 */
@Composable
fun SlideDecksScreen(slideRepository: SlideRepository, teacherId: String?, onBack: () -> Unit) {
    var state by remember { mutableStateOf(DecksScreenState.LIST) }
    var selectedDeckId by remember { mutableStateOf<String?>(null) }
    val decks by slideRepository.observeAllDecks().collectAsState(initial = emptyList<SlideDeckEntity>())

    when (state) {
        DecksScreenState.VIEWER -> {
            val deckId = selectedDeckId
            if (deckId != null) {
                SlideViewerScreen(slideRepository, deckId, onBack = { state = DecksScreenState.LIST })
            }
        }
        DecksScreenState.NEW_DECK -> {
            NewSlideDeckScreen(
                slideRepository = slideRepository,
                teacherId = teacherId.orEmpty(),
                onBack = { state = DecksScreenState.LIST },
                onCreated = { state = DecksScreenState.LIST }
            )
        }
        DecksScreenState.LIST -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Teaching Slides") },
                        navigationIcon = {
                            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                        }
                    )
                }
            ) { padding ->
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    if (teacherId != null) {
                        Button(
                            onClick = { state = DecksScreenState.NEW_DECK },
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) { Text("+ New Custom Deck") }
                    }
                    LazyColumn {
                        items(decks) { deck ->
                            ListItem(
                                headlineContent = { Text(deck.title.en) },
                                supportingContent = { Text(if (deck.category == "default") "Default deck" else "Custom deck") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Button(
                                onClick = { selectedDeckId = deck.id; state = DecksScreenState.VIEWER },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                            ) { Text("Open") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NewSlideDeckScreen(
    slideRepository: SlideRepository,
    teacherId: String,
    onBack: () -> Unit,
    onCreated: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var slideHeadline by remember { mutableStateOf("") }
    var slideCaption by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Slide Deck") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
            Text(
                "Note: custom decks are only entered in one language for now — the same text " +
                    "is stored for English/Marathi/Hindi until a proper translation flow is built."
            )
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Deck title") },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            )
            OutlinedTextField(
                value = slideHeadline,
                onValueChange = { slideHeadline = it },
                label = { Text("First slide headline (e.g. a letter or fact)") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
            OutlinedTextField(
                value = slideCaption,
                onValueChange = { slideCaption = it },
                label = { Text("First slide caption") },
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
            Button(
                onClick = {
                    if (title.isNotBlank() && slideHeadline.isNotBlank()) {
                        val deckId = UUID.randomUUID().toString()
                        val deck = SlideDeckEntity(
                            id = deckId,
                            title = TrilingualText(title, title, title),
                            category = "custom",
                            createdByTeacherId = teacherId,
                            orderIndex = 99
                        )
                        val slide = SlideEntity(
                            id = UUID.randomUUID().toString(),
                            deckId = deckId,
                            orderIndex = 0,
                            headline = slideHeadline,
                            captionEn = slideCaption,
                            captionMr = slideCaption,
                            captionHi = slideCaption
                        )
                        scope.launch {
                            slideRepository.addCustomDeck(deck, listOf(slide))
                            onCreated()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            ) { Text("Create Deck") }
        }
    }
}
