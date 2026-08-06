package com.gumthala.learningapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.gumthala.learningapp.ui.components.BleedHeader
import com.gumthala.learningapp.ui.components.OptionGrid
import com.gumthala.learningapp.ui.components.PrimaryFullButton
import com.gumthala.learningapp.ui.components.PurpleGradient
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.ScreenPadding
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

data class QuizUiState(
    val title: String = "Quiz Time!",
    val questionNumber: Int = 3,
    val questionCount: Int = 10,
    val timerLabel: String = "00:45",
    val questionText: String = "What is 5 × 6?",
    val options: List<String> = listOf("20", "25", "30", "35"),
    val correctIndex: Int = 2,
    val selectedIndex: Int? = 2,
    val revealed: Boolean = true,
    val feedbackTitle: String = "Correct! 🎉",
    val feedbackDetail: String = "5 × 6 = 30",
    val nextLabel: String = "Next Question"
)

@Composable
fun QuizScreen(
    state: QuizUiState = QuizUiState(),
    onSelectOption: (Int) -> Unit = {},
    onNext: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(Color.White)) {

        BleedHeader(
            gradient = PurpleGradient,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 20.dp, end = 20.dp, top = 17.dp, bottom = 13.dp
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Text("🏆", style = body(TextSize.Screen))
                Text(
                    state.title,
                    style = display(TextSize.Screen, FontWeight.ExtraBold),
                    color = Color.White
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${state.questionNumber}/${state.questionCount}",
                    style = body(TextSize.Small, FontWeight.Bold),
                    color = Color.White
                )
                Text(
                    "⏱ ${state.timerLabel}",
                    style = body(TextSize.Small, FontWeight.Bold),
                    color = Color.White
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = ScreenPadding)) {

            Spacer(Modifier.height(15.dp))

            // `.qz-card`
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.Soft))
                    .background(AppColors.SurfaceSoft)
                    .padding(17.dp)
            ) {
                Text(
                    "Q${state.questionNumber}. ${state.questionText}",
                    style = display(TextSize.Title, FontWeight.Bold),
                    color = AppColors.Ink
                )
            }

            Spacer(Modifier.height(15.dp))

            OptionGrid(
                options = state.options,
                correctIndex = state.correctIndex,
                selectedIndex = state.selectedIndex,
                revealCorrect = state.revealed,
                showLetters = true,
                onSelect = onSelectOption
            )

            Spacer(Modifier.height(15.dp))

            if (state.revealed) {
                FeedbackBanner(
                    title = state.feedbackTitle,
                    detail = state.feedbackDetail,
                    isCorrect = state.selectedIndex == state.correctIndex
                )
                Spacer(Modifier.height(13.dp))
            }

            PrimaryFullButton(text = state.nextLabel, onClick = onNext, enabled = state.revealed)
        }
    }
}

/** `.fb` — the green feedback block under the answer grid. */
@Composable
private fun FeedbackBanner(title: String, detail: String, isCorrect: Boolean) {
    val background = if (isCorrect) AppColors.GreenBg else AppColors.PinkBg
    val foreground = if (isCorrect) Color(0xFF15803D) else Color(0xFFD6316B)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.Card))
            .background(background)
            .padding(horizontal = 15.dp, vertical = 13.dp)
    ) {
        Text(title, style = display(TextSize.Label, FontWeight.Bold), color = foreground)
        Text(
            detail,
            style = body(TextSize.Small, FontWeight.Bold),
            color = foreground.copy(alpha = 0.8f),
            textAlign = TextAlign.Start
        )
    }
}
