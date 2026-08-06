package com.gumthala.learningapp.ui.student.home

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.data.local.entity.SubjectEntity
import com.gumthala.learningapp.domain.model.AppLanguage
import com.gumthala.learningapp.ui.components.SectionHeader
import com.gumthala.learningapp.ui.components.StatChip
import com.gumthala.learningapp.ui.theme.Ink
import com.gumthala.learningapp.ui.theme.Muted
import com.gumthala.learningapp.ui.theme.Purple
import com.gumthala.learningapp.ui.theme.PurpleDark
import com.gumthala.learningapp.ui.theme.SubjectColorFamily
import com.gumthala.learningapp.ui.theme.SurfaceLavender
import com.gumthala.learningapp.ui.theme.Yellow
import com.gumthala.learningapp.ui.theme.YellowBg

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    language: AppLanguage,
    onOpenSubjects: () -> Unit,
    onOpenLeaderboard: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        item { GreetRow(state.studentName, onOpenLeaderboard) }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatChip("🔥${state.streakDays}", "Learning Streak", Modifier.weight(1f))
                StatChip("⭐${state.xp}", "XP Points", Modifier.weight(1f))
                StatChip("🪙${state.coins}", "Coins", Modifier.weight(1f))
            }
        }
        item { KeepLearningBanner(onClick = onOpenSubjects) }
        item {
            SectionHeader("Featured Subjects", "See All", onOpenSubjects)
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.subjects.forEach { subject ->
                    FeatureChip(subject, language, Modifier.weight(1f), onClick = onOpenSubjects)
                }
            }
        }
        item {
            DailyChallengeCard(subjectName = state.upcomingSubjectName, onStart = onOpenSubjects)
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MiniCard("Weekly Progress", "${state.weeklyProgressPercent}%", Modifier.weight(1f))
                MiniCard("Upcoming Quiz", state.upcomingSubjectName, Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun GreetRow(studentName: String, onOpenLeaderboard: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(androidx.compose.ui.graphics.Color(0xFFDCEEFF), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🦉", fontSize = 20.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Good Morning,", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Muted)
            Text(
                text = "$studentName!",
                style = MaterialTheme.typography.titleMedium,
                color = Ink
            )
        }
        Text("👑", fontSize = 20.sp, modifier = Modifier.clickable(onClick = onOpenLeaderboard))
    }
}

@Composable
private fun KeepLearningBanner(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .background(
                Brush.linearGradient(listOf(Purple, PurpleDark)),
                RoundedCornerShape(18.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Keep Learning,\nKeep Growing! 🚀",
                color = androidx.compose.ui.graphics.Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp
            )
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Yellow, contentColor = androidx.compose.ui.graphics.Color(0xFF3A2600)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text("Start", style = MaterialTheme.typography.labelLarge, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun FeatureChip(subject: SubjectEntity, language: AppLanguage, modifier: Modifier, onClick: () -> Unit) {
    val family = SubjectColorFamily.fromKey(subject.colorFamily)
    Column(
        modifier = modifier
            .background(family.cardBg, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(family.chipBg, RoundedCornerShape(9.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(subject.iconEmoji, fontSize = 16.sp)
        }
        Text(
            text = subject.name.forLanguage(language),
            color = family.ink,
            style = MaterialTheme.typography.labelMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun DailyChallengeCard(subjectName: String, onStart: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
            .background(YellowBg, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("🏆", fontSize = 22.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text("Daily Challenge", style = MaterialTheme.typography.labelLarge, fontSize = 11.sp, color = androidx.compose.ui.graphics.Color(0xFF7A4D00))
            Text("Solve 5 $subjectName Qs · +50 XP", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = androidx.compose.ui.graphics.Color(0xFF9A6A00))
        }
        Button(
            onClick = onStart,
            colors = ButtonDefaults.buttonColors(containerColor = Purple, contentColor = androidx.compose.ui.graphics.Color.White),
            shape = RoundedCornerShape(9.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text("Start", fontSize = 10.sp, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun MiniCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(SurfaceLavender, RoundedCornerShape(14.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Muted)
        Text(value, style = MaterialTheme.typography.titleSmall, color = Ink)
    }
}
