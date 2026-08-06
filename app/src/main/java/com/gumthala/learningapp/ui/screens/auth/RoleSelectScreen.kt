package com.gumthala.learningapp.ui.screens.auth

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
import com.gumthala.learningapp.ui.components.PurpleDeepGradient
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

enum class AppRole { STUDENT, TEACHER, ADMIN }

/** `.role-bg` — the very first screen: "Who's logging in today?" */
@Composable
fun RoleSelectScreen(onRoleSelected: (AppRole) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PurpleDeepGradient)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(Radius.Large))
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text("🚀", style = body(TextSize.Screen))
        }

        Text(
            "Class 1–7 Learning",
            style = display(TextSize.Header, FontWeight.ExtraBold),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 12.dp)
        )
        Text(
            "Who's logging in today?",
            style = body(TextSize.Tiny, FontWeight.Bold),
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 4.dp, bottom = 28.dp)
        )

        RoleCard("🎒", AppColors.VioletBg, "I'm a Student", "Name + Class — no password") {
            onRoleSelected(AppRole.STUDENT)
        }
        RoleCard("🍎", AppColors.YellowBg, "I'm a Teacher", "Email + password") {
            onRoleSelected(AppRole.TEACHER)
        }
        RoleCard("🛡️", AppColors.PinkBg, "I'm an Admin", "Email + password") {
            onRoleSelected(AppRole.ADMIN)
        }
    }
}

@Composable
private fun RoleCard(
    emoji: String,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clip(RoundedCornerShape(Radius.Sheet))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(Radius.Card))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, style = body(TextSize.Label))
        }
        Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
            Text(title, style = display(TextSize.Title, FontWeight.Bold), color = AppColors.Ink)
            Text(subtitle, style = body(TextSize.Tiny, FontWeight.Bold), color = AppColors.Muted)
        }
        Text("›", style = display(TextSize.Screen, FontWeight.Bold), color = AppColors.Muted)
    }
}
