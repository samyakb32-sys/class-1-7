package com.gumthala.learningapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.components.EmojiTile
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.ScreenPadding
import com.gumthala.learningapp.ui.theme.SubjectPalette
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

data class SubjectCardUi(
    val id: String,
    val emoji: String,
    val title: String,
    val badge: String,
    val meta: String,
    val palette: SubjectPalette
)

/** `.sub-topbar` — back chevron, centred title, trailing action. */
@Composable
fun TopBarRow(
    title: String,
    trailing: String,
    onBack: () -> Unit = {},
    onTrailing: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ScreenPadding, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "‹",
            style = display(TextSize.Screen, FontWeight.Bold),
            color = AppColors.Ink,
            modifier = Modifier.clickable(onClick = onBack)
        )
        Text(
            title,
            style = display(TextSize.Display, FontWeight.ExtraBold),
            color = AppColors.Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Text(
            trailing,
            style = body(TextSize.Header),
            modifier = Modifier.clickable(onClick = onTrailing)
        )
    }
}

@Composable
fun SubjectsScreen(
    subjects: List<SubjectCardUi> = defaultSubjects,
    onBack: () -> Unit = {},
    onSearch: () -> Unit = {},
    onSubjectClick: (SubjectCardUi) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(Color.White)) {
        TopBarRow(title = "Subjects", trailing = "🔍", onBack = onBack, onTrailing = onSearch)
        LazyColumn(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = ScreenPadding, end = ScreenPadding, bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            items(subjects, key = { it.id }) { subject ->
                SubjectCard(subject) { onSubjectClick(subject) }
            }
        }
    }
}

@Composable
private fun SubjectCard(subject: SubjectCardUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.Large))
            .background(subject.palette.background)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        EmojiTile(
            emoji = subject.emoji,
            background = subject.palette.iconTile,
            size = 47.dp,
            cornerRadius = Radius.Cell,
            fontSize = TextSize.Screen
        )
        Column(Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(subject.title, style = display(TextSize.Label, FontWeight.Bold), color = AppColors.Ink)
                BadgePill(
                    background = subject.palette.iconTile,
                    text = subject.badge,
                    foreground = subject.palette.foreground
                )
            }
            Text(
                subject.meta,
                style = body(TextSize.Tiny, FontWeight.Bold),
                color = AppColors.Muted,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

/** `.subj-card .badge` — a small pill using the card's own icon tint. */
@Composable
private fun BadgePill(background: Color, text: String, foreground: Color) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.Chip))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 4.dp)
    ) {
        Text(text, style = body(TextSize.Micro, FontWeight.ExtraBold), color = foreground)
    }
}

/**
 * The six subjects exactly as the mockup lists them. Note this includes Science
 * and EVS, which are not in the app's Maths / English / Marathi / Hindi spec —
 * see README, "Mockup vs spec".
 */
val defaultSubjects = listOf(
    SubjectCardUi("maths", "🔢", "Mathematics", "Easy-Hard", "78% Complete · 24/30", SubjectPalette.Blue),
    SubjectCardUi("english", "📖", "English", "Easy-Med", "65% Complete · 20/30", SubjectPalette.Pink),
    SubjectCardUi("science", "🔬", "Science", "Med-Hard", "70% Complete · 21/30", SubjectPalette.Green),
    SubjectCardUi("evs", "🌱", "EVS", "Easy-Med", "60% Complete · 18/30", SubjectPalette.Yellow),
    SubjectCardUi("hindi", "🅰️", "Hindi", "Easy-Med", "82% Complete · 25/30", SubjectPalette.Violet),
    SubjectCardUi("marathi", "✍️", "Marathi", "Medium", "68% Complete · 20/30", SubjectPalette.Orange)
)
