package com.gumthala.learningapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.ui.theme.Ink
import com.gumthala.learningapp.ui.theme.Muted
import com.gumthala.learningapp.ui.theme.Purple
import com.gumthala.learningapp.ui.theme.SurfaceLavender

/** The back-chevron / centered-title / trailing-icon bar used on Subjects & Practice. */
@Composable
fun SubTopBar(title: String, onBack: () -> Unit, trailingEmoji: String, onTrailingClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "‹",
            fontSize = 20.sp,
            color = Ink,
            modifier = Modifier.clickable(onClick = onBack)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Ink,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = trailingEmoji,
            fontSize = 16.sp,
            modifier = Modifier.clickable(onClick = onTrailingClick)
        )
    }
}

/** The "Section Title ................ See All" row used on Home/Progress. */
@Composable
fun SectionHeader(title: String, actionLabel: String? = null, onActionClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, style = MaterialTheme.typography.titleSmall, color = Ink)
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Purple,
                modifier = Modifier.clickable(onClick = onActionClick)
            )
        }
    }
}

/** The pale-lavender stat pill used on Home (streak/XP/coins) and mini-cards. */
@Composable
fun StatChip(value: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(SurfaceLavender, RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = value, style = MaterialTheme.typography.titleSmall, color = Ink)
            Text(text = label, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = Muted)
        }
    }
}

/** Small rounded badge, e.g. difficulty ("Easy-Hard") or the ✓ correct-answer marker. */
@Composable
fun Pill(text: String, background: Color, contentColor: Color, modifier: Modifier = Modifier, fontSize: androidx.compose.ui.unit.TextUnit = 8.sp) {
    Box(
        modifier = modifier
            .background(background, RoundedCornerShape(8.dp))
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(text = text, color = contentColor, fontSize = fontSize, fontWeight = FontWeight.ExtraBold)
    }
}
