package com.gumthala.learningapp.ui.student.lesson

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.domain.model.AppLanguage
import com.gumthala.learningapp.data.remote.tts.QuestionAudioPlayer
import com.gumthala.learningapp.ui.components.ScreenStatusBar
import com.gumthala.learningapp.ui.theme.GreenBg
import com.gumthala.learningapp.ui.theme.Ink
import com.gumthala.learningapp.ui.theme.CorrectGreenText
import com.gumthala.learningapp.ui.theme.Muted
import com.gumthala.learningapp.ui.theme.Purple
import com.gumthala.learningapp.ui.theme.SurfaceLavender
import com.gumthala.learningapp.ui.theme.SurfacePressed
import com.gumthala.learningapp.ui.theme.VioletBg
import com.gumthala.learningapp.ui.theme.Violet
import com.gumthala.learningapp.ui.theme.Yellow
import com.gumthala.learningapp.ui.theme.YellowBg
import kotlinx.coroutines.launch

@Composable
fun LessonScreen(
    viewModel: LessonViewModel,
    language: AppLanguage,
    onBack: () -> Unit,
    onNavigateToChapter: (String) -> Unit,
    onPracticeNow: (chapterId: String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val chapter = state.chapter ?: return

    ScreenStatusBar(Purple)
    val context = LocalContext.current
    val player = remember { QuestionAudioPlayer(context) }
    val scope = rememberCoroutineScope()
    androidx.compose.runtime.DisposableEffect(Unit) { onDispose { player.stop() } }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Purple)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("‹", color = Color.White, fontSize = 20.sp, modifier = Modifier.clickable(onClick = onBack))
            Text(chapter.title.forLanguage(language), color = Color.White, style = MaterialTheme.typography.titleMedium)
            Text("🔖", fontSize = 16.sp)
        }
        val progress = ((state.chapterIndex + 1).toFloat() / state.totalChapters).coerceIn(0f, 1f)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .background(Purple.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .height(5.dp)
                    .background(Yellow)
            )
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp, bottom = 14.dp)
                        .height(150.dp)
                        .background(VioletBg, RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "📘", fontSize = 56.sp)
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(chapter.title.forLanguage(language), style = MaterialTheme.typography.titleLarge, color = Ink)
                    Row(
                        modifier = Modifier
                            .background(VioletBg, RoundedCornerShape(20.dp))
                            .clickable {
                                scope.launch { player.play(chapter.description.forLanguage(language), language) }
                            }
                            .padding(horizontal = 11.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("🔊", fontSize = 11.sp)
                        Text("Read Aloud", color = Violet, style = MaterialTheme.typography.labelLarge, fontSize = 10.sp)
                    }
                }
            }
            item {
                Text(
                    text = chapter.description.forLanguage(language),
                    fontSize = 11.5.sp,
                    color = Muted,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                        .background(YellowBg, RoundedCornerShape(14.dp))
                        .padding(horizontal = 13.dp, vertical = 11.dp)
                ) {
                    Text("EXAMPLE", fontSize = 9.sp, color = com.gumthala.learningapp.ui.theme.AmberExampleLabel)
                    Text(
                        chapter.example.forLanguage(language),
                        style = MaterialTheme.typography.titleMedium,
                        color = Ink,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            val sample = state.sampleQuestion
            if (sample != null) {
                item {
                    Text("Try It", style = MaterialTheme.typography.titleSmall, color = Ink)
                    Text(
                        text = sample.text.forLanguage(language),
                        fontSize = 11.5.sp,
                        color = Muted,
                        modifier = Modifier.padding(top = 1.dp, bottom = 9.dp)
                    )
                }
                item {
                    val options = when (language) {
                        AppLanguage.ENGLISH -> sample.optionsEn
                        AppLanguage.MARATHI -> sample.optionsMr
                        AppLanguage.HINDI -> sample.optionsHi
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        options.withIndex().toList().chunked(2).forEach { rowOptions ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowOptions.forEach { (i, optionText) ->
                                    val isCorrect = i == sample.correctIndex
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(if (isCorrect) GreenBg else SurfaceLavender, RoundedCornerShape(10.dp))
                                            .padding(10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            optionText,
                                            color = if (isCorrect) CorrectGreenText else Ink,
                                            style = MaterialTheme.typography.titleSmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { state.prevChapterId?.let(onNavigateToChapter) },
                        enabled = state.prevChapterId != null,
                        colors = ButtonDefaults.buttonColors(containerColor = SurfacePressed, contentColor = Muted),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("‹ Prev", style = MaterialTheme.typography.labelLarge, fontSize = 10.sp)
                    }
                    Button(
                        onClick = { onPracticeNow(chapter.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = Purple, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Practice Now →", style = MaterialTheme.typography.labelLarge, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
