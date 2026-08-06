package com.gumthala.learningapp.ui.student.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.ui.components.ScreenStatusBar
import com.gumthala.learningapp.ui.theme.Ink
import com.gumthala.learningapp.ui.theme.LogoutRed
import com.gumthala.learningapp.ui.theme.LogoutRedBg
import com.gumthala.learningapp.ui.theme.Muted
import com.gumthala.learningapp.ui.theme.Purple
import com.gumthala.learningapp.ui.theme.PurpleDark
import com.gumthala.learningapp.ui.theme.SurfaceLavender

private data class MenuRow(val emoji: String, val label: String)

private val menuRows = listOf(
    MenuRow("🏅", "My Achievements"),
    MenuRow("📜", "My Certificates"),
    MenuRow("⚙️", "Settings"),
    MenuRow("💬", "Help & Support"),
    MenuRow("ℹ️", "About App")
)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onOpenAchievements: () -> Unit,
    onOpenHelp: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    ScreenStatusBar(PurpleDark)

    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(listOf(Purple, PurpleDark)),
                    RoundedCornerShape(bottomStart = 22.dp, bottomEnd = 22.dp)
                )
                .padding(top = 24.dp, bottom = 18.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .background(Brush.linearGradient(listOf(Color(0xFFFFB56B), Color(0xFFFF8A5C))), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🧒", fontSize = 30.sp)
            }
            Text(
                state.name,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text("Class ${state.classLevel} · Learner", color = Color.White.copy(alpha = 0.8f), fontSize = 10.sp)
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileStat("⭐${state.xp}", "XP")
                ProfileStat("🪙${state.coins}", "Coins")
                ProfileStat("🔥${state.streakDays}", "Streak")
            }
        }

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 9.dp)) {
            menuRows.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 9.dp)
                        .background(SurfaceLavender, RoundedCornerShape(12.dp))
                        .clickable {
                            when (row.label) {
                                "My Achievements" -> onOpenAchievements()
                                "Help & Support" -> onOpenHelp()
                                "Settings" -> onOpenSettings()
                                else -> Toast.makeText(context, "${row.label} is coming soon!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .padding(horizontal = 12.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(row.emoji, fontSize = 14.sp)
                    Text(row.label, style = MaterialTheme.typography.titleSmall, color = Ink, modifier = Modifier.weight(1f))
                    Text("›", color = Muted, fontSize = 14.sp)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LogoutRedBg, RoundedCornerShape(12.dp))
                    .clickable(onClick = onLogout)
                    .padding(vertical = 11.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Logout", style = MaterialTheme.typography.titleSmall, color = LogoutRed)
            }
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, style = MaterialTheme.typography.titleSmall, fontSize = 13.sp)
        Text(label, color = Color.White.copy(alpha = 0.75f), fontSize = 8.sp)
    }
}
