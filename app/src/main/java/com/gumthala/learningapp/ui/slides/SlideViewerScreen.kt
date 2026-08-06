package com.gumthala.learningapp.ui.slides

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.data.local.entity.SlideEntity
import com.gumthala.learningapp.data.repository.SlideRepository

/**
 * Plain, unstyled placeholder pending a mockup — arrow-navigates through one Teaching Slides
 * deck (default A-Z / tables / barakhadi decks, or a teacher's custom deck).
 */
@Composable
fun SlideViewerScreen(slideRepository: SlideRepository, deckId: String, onBack: () -> Unit) {
    val slides by slideRepository.observeSlides(deckId).collectAsState(initial = emptyList<SlideEntity>())
    var index by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Slide ${index + 1} / ${slides.size}") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        val slide = slides.getOrNull(index)
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (slide != null) {
                Text(slide.headline, fontSize = 64.sp, textAlign = TextAlign.Center)
                Text(slide.captionEn, modifier = Modifier.padding(top = 16.dp), textAlign = TextAlign.Center)
            } else {
                Text("This deck has no slides yet.")
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { if (index > 0) index-- }, enabled = index > 0) { Text("‹ Prev") }
                Button(onClick = { if (index < slides.size - 1) index++ }, enabled = index < slides.size - 1) { Text("Next ›") }
            }
        }
    }
}
