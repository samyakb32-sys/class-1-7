package com.gumthala.learningapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.gumthala.learningapp.domain.DifficultyLevel
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

private data class DifficultyCopy(val emoji: String, val title: String, val sub: String, val bg: Color, val fg: Color)

private fun copyFor(level: DifficultyLevel): DifficultyCopy = when (level) {
    DifficultyLevel.LOW -> DifficultyCopy("🌱", "Low", "Warm up with the basics", AppColors.GreenBg, AppColors.Green)
    DifficultyLevel.MEDIUM -> DifficultyCopy("⭐", "Medium", "A good everyday challenge", AppColors.YellowBg, AppColors.OnYellowButton)
    DifficultyLevel.HIGH -> DifficultyCopy("🔥", "High", "For when you're ready to push", AppColors.PinkBg, AppColors.Pink)
}

/**
 * "How hard should this be?" — shown once per chapter before the quiz starts.
 * Every level is always tappable: QuizRepository.startQuiz falls back to the
 * full chapter if a band is thin, so picking High never risks an empty quiz
 * even on lightly-seeded content.
 */
@Composable
fun DifficultyPickerScreen(
    chapterTitle: String,
    onBack: () -> Unit,
    onPick: (DifficultyLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("‹", style = display(TextSize.Header, FontWeight.Bold), color = AppColors.Ink,
                modifier = Modifier.clickable(onClick = onBack))
            Text(
                chapterTitle,
                style = display(TextSize.Header, FontWeight.ExtraBold),
                color = AppColors.Ink,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            androidx.compose.foundation.layout.Spacer(Modifier.width(20.dp))
        }

        Text(
            "Choose your challenge",
            style = body(TextSize.Small, FontWeight.Bold),
            color = AppColors.Muted,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        DifficultyLevel.entries.forEach { level ->
            val copy = copyFor(level)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .clip(RoundedCornerShape(Radius.Sheet))
                    .background(copy.bg)
                    .clickable { onPick(level) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(copy.emoji, style = body(TextSize.Screen))
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(copy.title, style = display(TextSize.Title, FontWeight.Bold), color = copy.fg)
                    Text(copy.sub, style = body(TextSize.Tiny, FontWeight.SemiBold), color = AppColors.Muted)
                }
            }
        }
    }
}
