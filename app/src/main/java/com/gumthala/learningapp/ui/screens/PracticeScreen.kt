package com.gumthala.learningapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.components.EmojiTile
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.ScreenPadding
import com.gumthala.learningapp.ui.theme.SubjectPalette
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

data class PracticeModeUi(
    val id: String,
    val emoji: String,
    val title: String,
    val subtitle: String,
    val palette: SubjectPalette
)

@Composable
fun PracticeScreen(
    modes: List<PracticeModeUi> = defaultPracticeModes,
    onBack: () -> Unit = {},
    onModeClick: (PracticeModeUi) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        TopBarRow(title = "Practice", trailing = "🙂", onBack = onBack)
        LazyColumn(
            contentPadding = PaddingValues(start = ScreenPadding, end = ScreenPadding, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            items(modes, key = { it.id }) { mode ->
                PracticeRow(mode) { onModeClick(mode) }
            }
        }
    }
}

/**
 * `.prac-item` — note the mockup uses the soft #F7F6FD fill for the row and only
 * tints the icon tile, unlike the fully-tinted subject cards.
 */
@Composable
private fun PracticeRow(mode: PracticeModeUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.Soft))
            .background(AppColors.SurfaceSoft)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EmojiTile(
            emoji = mode.emoji,
            background = mode.palette.iconTile,
            size = 45.dp,
            cornerRadius = Radius.Cell
        )
        Column {
            Text(mode.title, style = display(TextSize.Label, FontWeight.Bold), color = AppColors.Ink)
            Text(mode.subtitle, style = body(TextSize.Tiny, FontWeight.Bold), color = AppColors.Muted)
        }
    }
}

/**
 * Six modes as drawn. Only "Multiple Choice" and "Image Questions" are covered by
 * the current quiz engine — see README, "Mockup vs spec".
 */
val defaultPracticeModes = listOf(
    PracticeModeUi("mcq", "🔤", "Multiple Choice", "Select the correct answer", SubjectPalette.Violet),
    PracticeModeUi("drag", "🧩", "Drag & Drop", "Drag and place items", SubjectPalette.Yellow),
    PracticeModeUi("match", "🔗", "Match the Pair", "Match related items", SubjectPalette.Green),
    PracticeModeUi("blanks", "✏️", "Fill in the Blanks", "Type the missing word", SubjectPalette.Pink),
    PracticeModeUi("image", "🖼️", "Image Questions", "Answer from images", SubjectPalette.Blue),
    PracticeModeUi("voice", "🎙️", "Voice Questions", "Speak your answer", SubjectPalette.Orange)
)
