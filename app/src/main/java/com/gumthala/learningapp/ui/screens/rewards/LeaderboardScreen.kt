package com.gumthala.learningapp.ui.screens.rewards

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.components.BleedHeader
import com.gumthala.learningapp.ui.components.PurpleGradient
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.AppColors2
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

data class LeaderboardEntry(val rank: Int, val avatarEmoji: String, val name: String, val xp: Int)

/**
 * `Leaderboard` — per-class ranking with a 3-slot podium for the top scorers.
 * Backed by [com.gumthala.learningapp.data.repo.QuizRepository.observeLeaderboard],
 * which is already scoped to one class level — this screen never mixes classes.
 */
@Composable
fun LeaderboardScreen(classLabel: String, entries: List<LeaderboardEntry>, modifier: Modifier = Modifier) {
    val podium = entries.take(3)
    val rest = entries.drop(3)

    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            BleedHeader(gradient = PurpleGradient) {
                Column {
                    Text(
                        classLabel.uppercase(),
                        style = display(TextSize.Micro, FontWeight.Bold),
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Text(
                        "🏆 Leaderboard",
                        style = display(TextSize.Screen, FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
            }
        }
        if (podium.size == 3) {
            item { Podium(podium, modifier = Modifier.padding(top = 16.dp, bottom = 18.dp)) }
        }
        items(rest, key = { it.rank }) { entry ->
            LeaderboardRowView(entry, modifier = Modifier.padding(horizontal = 20.dp))
        }
        item { androidx.compose.foundation.layout.Spacer(Modifier.height(12.dp)) }
    }
}

@Composable
private fun Podium(top3: List<LeaderboardEntry>, modifier: Modifier = Modifier) {
    // Mockup order is silver, gold, bronze (2nd, 1st, 3rd) so the winner sits centred and tallest.
    val silver = top3.getOrNull(1)
    val gold = top3.getOrNull(0)
    val bronze = top3.getOrNull(2)

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.Bottom
    ) {
        silver?.let { PodiumSlot(it, height = 88.dp, listOf(AppColors2.PodiumSilverStart, AppColors2.PodiumSilverEnd)) }
        gold?.let { PodiumSlot(it, height = 110.dp, listOf(AppColors2.PodiumGoldStart, AppColors.Yellow)) }
        bronze?.let { PodiumSlot(it, height = 70.dp, listOf(AppColors2.PodiumBronzeStart, AppColors2.PodiumBronzeEnd)) }
    }
}

@Composable
private fun PodiumSlot(entry: LeaderboardEntry, height: androidx.compose.ui.unit.Dp, gradient: List<Color>) {
    Column(
        modifier = Modifier
            .width(84.dp)
            .height(height)
            .clip(RoundedCornerShape(topStart = Radius.Card, topEnd = Radius.Card))
            .background(Brush.linearGradient(gradient)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Bottom
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 6.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(entry.avatarEmoji, style = body(TextSize.Small))
        }
        Text(entry.name, style = display(TextSize.Small, FontWeight.Bold), color = Color.White)
        Text(
            "${entry.xp} XP",
            style = body(TextSize.Micro, FontWeight.Bold),
            color = Color.White.copy(alpha = 0.85f),
            modifier = Modifier.padding(bottom = 8.dp)
        )
    }
}

/** `.lb-row` — rank 4 and below, a flat row with rank / name / score. */
@Composable
private fun LeaderboardRowView(entry: LeaderboardEntry, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 7.dp)
            .clip(RoundedCornerShape(Radius.Cell))
            .background(AppColors.SurfaceSoft)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            entry.rank.toString(),
            style = display(TextSize.Small, FontWeight.ExtraBold),
            color = AppColors.Muted,
            modifier = Modifier.width(16.dp)
        )
        Text(
            entry.name,
            style = display(TextSize.Body, FontWeight.Bold),
            color = AppColors.Ink,
            modifier = Modifier.weight(1f)
        )
        Text("${entry.xp} XP", style = display(TextSize.Small, FontWeight.ExtraBold), color = AppColors.Purple)
    }
}
