package com.gumthala.learningapp.ui.student.practice

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.ui.components.SubTopBar
import com.gumthala.learningapp.ui.theme.Ink
import com.gumthala.learningapp.ui.theme.Muted
import com.gumthala.learningapp.ui.theme.SubjectColorFamily
import com.gumthala.learningapp.ui.theme.SurfaceLavender

private data class PracticeMode(val titleEn: String, val subtitleEn: String, val emoji: String, val family: SubjectColorFamily, val implemented: Boolean)

private val practiceModes = listOf(
    PracticeMode("Multiple Choice", "Select the correct answer", "🔤", SubjectColorFamily.VIOLET, implemented = true),
    PracticeMode("Drag & Drop", "Drag and place items", "🧩", SubjectColorFamily.YELLOW, implemented = false),
    PracticeMode("Match the Pair", "Match related items", "🔗", SubjectColorFamily.GREEN, implemented = false),
    PracticeMode("Fill in the Blanks", "Type the missing word", "✏️", SubjectColorFamily.PINK, implemented = false),
    PracticeMode("Image Questions", "Answer from images", "🖼️", SubjectColorFamily.BLUE, implemented = false),
    PracticeMode("Voice Questions", "Speak your answer", "🎙️", SubjectColorFamily.ORANGE, implemented = false)
)

@Composable
fun PracticeScreen(onBack: () -> Unit, onPickMultipleChoice: () -> Unit) {
    val context = LocalContext.current
    Column(modifier = Modifier.fillMaxSize()) {
        SubTopBar(title = "Practice", onBack = onBack, trailingEmoji = "🙂")
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(practiceModes) { mode ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 9.dp)
                        .background(SurfaceLavender, RoundedCornerShape(14.dp))
                        .clickable {
                            if (mode.implemented) {
                                onPickMultipleChoice()
                            } else {
                                Toast.makeText(context, "${mode.titleEn} is coming soon!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(mode.family.chipBg, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(mode.emoji, fontSize = 16.sp)
                    }
                    Column {
                        Text(mode.titleEn, style = MaterialTheme.typography.titleSmall, color = Ink)
                        Text(mode.subtitleEn, fontSize = 9.sp, color = Muted)
                    }
                }
            }
        }
    }
}
