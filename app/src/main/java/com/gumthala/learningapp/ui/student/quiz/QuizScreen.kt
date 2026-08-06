package com.gumthala.learningapp.ui.student.quiz

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.data.remote.tts.QuestionAudioPlayer
import com.gumthala.learningapp.domain.model.AppLanguage
import com.gumthala.learningapp.ui.components.CandyBurst
import com.gumthala.learningapp.ui.components.ScreenStatusBar
import com.gumthala.learningapp.ui.theme.CorrectGreenText
import com.gumthala.learningapp.ui.theme.GreenBg
import com.gumthala.learningapp.ui.theme.Ink
import com.gumthala.learningapp.ui.theme.Muted
import com.gumthala.learningapp.ui.theme.Purple
import com.gumthala.learningapp.ui.theme.PurpleLight
import com.gumthala.learningapp.ui.theme.SurfaceLavender
import com.gumthala.learningapp.ui.theme.VioletBg
import kotlinx.coroutines.launch

private val WrongBg = Color(0xFFFEE2E2)
private val WrongText = Color(0xFFB91C1C)

@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    language: AppLanguage,
    onDone: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    ScreenStatusBar(Purple)

    val context = LocalContext.current
    val player = remember { QuestionAudioPlayer(context) }
    val scope = rememberCoroutineScope()
    DisposableEffect(Unit) { onDispose { player.stop() } }

    if (state.loading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Purple) }
        return
    }

    if (state.quizResult != null) {
        QuizResultOverlay(state = state, onDone = onDone)
        return
    }

    val question = state.currentQuestion ?: return

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(Purple, PurpleLight)))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("🏆", fontSize = 16.sp)
                Text("Quiz Time!", color = Color.White, style = MaterialTheme.typography.titleMedium, fontSize = 15.sp)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${state.currentIndex + 1}/${state.questions.size}", color = Color.White, fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Text("⏱ ${formatTime(state.elapsedSeconds)}", color = Color.White, fontSize = 10.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(SurfaceLavender, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Q${state.currentIndex + 1}. ${question.question.text.forLanguage(language)}",
                    style = MaterialTheme.typography.titleSmall,
                    color = Ink,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .background(VioletBg, CircleShape)
                        .clickable {
                            scope.launch { player.play(question.question.text.forLanguage(language), language) }
                        }
                        .padding(8.dp)
                ) {
                    Text("🔊", fontSize = 13.sp)
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                items(question.displayOptions.size) { i ->
                    val isCorrect = i == question.correctDisplayIndex
                    val isSelected = i == state.selectedIndex
                    val bg = when {
                        state.revealed && isCorrect -> GreenBg
                        state.revealed && isSelected -> WrongBg
                        else -> SurfaceLavender
                    }
                    val fg = when {
                        state.revealed && isCorrect -> CorrectGreenText
                        state.revealed && isSelected -> WrongText
                        else -> Ink
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(2.4f)
                            .background(bg, RoundedCornerShape(10.dp))
                            .clickable(enabled = !state.revealed) { viewModel.selectAnswer(i) }
                            .padding(11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(question.displayOptions[i], color = fg, style = MaterialTheme.typography.titleSmall)
                        if (state.revealed && isCorrect) {
                            Text("✓", color = CorrectGreenText, fontSize = 10.sp, modifier = Modifier.align(Alignment.TopEnd))
                        }
                    }
                }
            }

            if (state.revealed) {
                val wasCorrect = viewModel.questionAnsweredCorrectly()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .background(if (wasCorrect) GreenBg else WrongBg, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Column {
                        Text(
                            if (wasCorrect) "Correct! 🎉" else "Not quite! 😊",
                            style = MaterialTheme.typography.titleSmall,
                            color = if (wasCorrect) CorrectGreenText else WrongText
                        )
                        Text(
                            "Answer: ${question.displayOptions[question.correctDisplayIndex]}",
                            fontSize = 10.sp,
                            color = if (wasCorrect) CorrectGreenText else WrongText
                        )
                    }
                }
            }

            Button(
                onClick = { viewModel.nextQuestion() },
                enabled = state.revealed,
                colors = ButtonDefaults.buttonColors(containerColor = Purple, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (state.isLastQuestion) "See Results" else "Next Question",
                    style = MaterialTheme.typography.titleSmall,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun QuizResultOverlay(state: QuizUiState, onDone: () -> Unit) {
    val result = state.quizResult ?: return
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        CandyBurst(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🎉", fontSize = 48.sp)
            Text("Quiz Complete!", style = MaterialTheme.typography.headlineSmall, color = Ink)
            Text(
                "${result.correctCount}/${result.totalQuestions} correct",
                style = MaterialTheme.typography.bodyLarge,
                color = Muted,
                modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { i ->
                    Text(if (i < result.starsEarned) "⭐" else "☆", fontSize = 28.sp)
                }
            }
            Text("+${result.xpEarned} XP", style = MaterialTheme.typography.titleMedium, color = Purple, modifier = Modifier.padding(top = 10.dp, bottom = 20.dp))
            Button(
                onClick = onDone,
                colors = ButtonDefaults.buttonColors(containerColor = Purple, contentColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Done", style = MaterialTheme.typography.titleSmall)
            }
        }
    }
}

private fun formatTime(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%02d:%02d".format(m, s)
}
