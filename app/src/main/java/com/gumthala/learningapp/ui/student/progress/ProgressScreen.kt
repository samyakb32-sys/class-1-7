package com.gumthala.learningapp.ui.student.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.ui.components.SectionHeader
import com.gumthala.learningapp.ui.theme.Ink
import com.gumthala.learningapp.ui.theme.Muted
import com.gumthala.learningapp.ui.theme.Purple
import com.gumthala.learningapp.ui.theme.PurpleLight
import com.gumthala.learningapp.ui.theme.SubjectColorFamily
import com.gumthala.learningapp.ui.theme.SurfaceLavender

@Composable
fun ProgressScreen(viewModel: ProgressViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        com.gumthala.learningapp.ui.components.ScreenStatusBar(Purple)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(Purple, PurpleLight)))
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("📈 My Progress", color = Color.White, style = MaterialTheme.typography.titleMedium, fontSize = 15.sp)
            Text("⚙️", fontSize = 16.sp)
        }

        LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 14.dp)) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .background(SurfaceLavender, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ProgressRing(percent = state.overallPercent)
                    Column {
                        Text("Overall Progress", style = MaterialTheme.typography.titleSmall, color = Ink)
                        Text("${state.completedChapters} / ${state.totalChapters} Chapters", fontSize = 10.sp, color = Muted)
                    }
                }
            }
            item { SectionHeader("Subject Performance") }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    state.subjectPerformance.forEach { perf ->
                        val family = SubjectColorFamily.fromKey(perf.colorKey)
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(family.cardBg, RoundedCornerShape(12.dp))
                                .padding(vertical = 9.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("${perf.percent}%", style = MaterialTheme.typography.titleSmall, color = Ink)
                            Text(perf.nameEn, fontSize = 8.sp, color = Ink)
                        }
                    }
                }
            }
            item { SectionHeader("Weekly Report") }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .padding(bottom = 20.dp)
                        .background(SurfaceLavender, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    val barColors = listOf(
                        Color(0xFFCFE0FF), Color(0xFFFDE49A), Color(0xFFA7F3C4), Color(0xFFD6C2FF),
                        Color(0xFFFFC2D6), Color(0xFFCFE0FF), Color(0xFFFFC2D6)
                    )
                    state.weeklyBars.forEachIndexed { i, fraction ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .height((fraction.coerceIn(0.06f, 1f) * 40).dp)
                                .background(barColors[i % barColors.size], RoundedCornerShape(4.dp))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressRing(percent: Int) {
    Box(modifier = Modifier.size(64.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(64.dp)) {
            drawArc(color = Color(0xFFE4E1FA), startAngle = -90f, sweepAngle = 360f, useCenter = false, style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx()))
            drawArc(
                color = Purple,
                startAngle = -90f,
                sweepAngle = 360f * (percent / 100f),
                useCenter = false,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 8.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$percent%", style = MaterialTheme.typography.titleSmall, color = Ink)
        }
    }
}
