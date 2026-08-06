package com.gumthala.learningapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.ScreenPadding
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

data class ProfileStat(val value: String, val label: String)
data class ProfileMenuItemUi(val id: String, val emoji: String, val label: String)

data class ProfileUiState(
    val avatarEmoji: String = "👧",
    val name: String = "Aarohi Sharma",
    val subtitle: String = "Class 5 · Learner",
    val stats: List<ProfileStat> = listOf(
        ProfileStat("⭐1,250", "XP"),
        ProfileStat("🪙320", "Coins"),
        ProfileStat("🔥12", "Streak")
    ),
    val menu: List<ProfileMenuItemUi> = listOf(
        ProfileMenuItemUi("achievements", "🏅", "My Achievements"),
        ProfileMenuItemUi("certificates", "📜", "My Certificates"),
        ProfileMenuItemUi("settings", "⚙️", "Settings"),
        ProfileMenuItemUi("help", "💬", "Help & Support"),
        ProfileMenuItemUi("about", "ℹ️", "About App")
    )
)

@Composable
fun ProfileScreen(
    state: ProfileUiState = ProfileUiState(),
    onMenuClick: (ProfileMenuItemUi) -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        ProfileHeader(state)

        Column(modifier = Modifier.padding(horizontal = ScreenPadding)) {
            Spacer(Modifier.height(12.dp))
            state.menu.forEach { item ->
                MenuRow(item) { onMenuClick(item) }
                Spacer(Modifier.height(11.dp))
            }
            LogoutRow(onLogout)
            Spacer(Modifier.height(20.dp))
        }
    }
}

/** `.prof-head` — 160° purple gradient with 22px bottom corners. */
@Composable
private fun ProfileHeader(state: ProfileUiState) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = Radius.Sheet, bottomEnd = Radius.Sheet))
            .background(Brush.linearGradient(listOf(AppColors.Purple, AppColors.PurpleDark)))
            .padding(start = 20.dp, end = 20.dp, top = 30.dp, bottom = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(85.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(Color(0xFFFFB56B), Color(0xFFFF8A5C))))
                .border(4.dp, Color.White.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(state.avatarEmoji, style = body(TextSize.Display * 1.6f))
        }
        Spacer(Modifier.height(10.dp))
        Text(state.name, style = display(TextSize.Screen, FontWeight.ExtraBold), color = Color.White)
        Text(
            state.subtitle,
            style = body(TextSize.Small, FontWeight.Bold),
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 3.dp)
        )
        Row(
            modifier = Modifier.padding(top = 15.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            state.stats.forEach { stat ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        stat.value,
                        style = display(TextSize.Title, FontWeight.ExtraBold),
                        color = Color.White
                    )
                    Text(
                        stat.label,
                        style = body(TextSize.Micro, FontWeight.Bold),
                        color = Color.White.copy(alpha = 0.75f)
                    )
                }
            }
        }
    }
}

/** `.p-menu` */
@Composable
private fun MenuRow(item: ProfileMenuItemUi, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.Card))
            .background(AppColors.SurfaceSoft)
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(item.emoji, style = body(TextSize.Header))
        Text(
            item.label,
            style = display(TextSize.Body, FontWeight.Bold),
            color = AppColors.Ink,
            modifier = Modifier.weight(1f)
        )
        Text("›", style = body(TextSize.Body, FontWeight.Bold), color = AppColors.Muted)
    }
}

/** `.p-menu.logout` — centred, red on #FFE3E3. */
@Composable
private fun LogoutRow(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.Card))
            .background(AppColors.LogoutBg)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Logout", style = display(TextSize.Body, FontWeight.Bold), color = AppColors.LogoutFg)
    }
}
