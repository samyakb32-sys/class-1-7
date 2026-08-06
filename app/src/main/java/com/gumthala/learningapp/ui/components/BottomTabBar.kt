package com.gumthala.learningapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.ui.theme.Muted
import com.gumthala.learningapp.ui.theme.Purple

enum class StudentTab(val emoji: String, val label: String) {
    HOME("🏠", "Home"),
    LEARN("🎓", "Learn"),
    PRACTICE("🎯", "Practice"),
    PROGRESS("📊", "Progress"),
    PROFILE("👤", "Profile")
}

/** Matches the mockup's 52dp tab bar; [showLabels] mirrors the mockup (labels only on Home). */
@Composable
fun BottomTabBar(
    selected: StudentTab,
    onSelect: (StudentTab) -> Unit,
    showLabels: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color.White)
            .border(width = 1.dp, color = com.gumthala.learningapp.ui.theme.HairlineBorder)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StudentTab.entries.forEach { tab ->
            val isSelected = tab == selected
            Column2(
                onClick = { onSelect(tab) },
                emoji = tab.emoji,
                label = tab.label,
                showLabel = showLabels,
                color = if (isSelected) Purple else Muted
            )
        }
    }
}

@Composable
private fun Column2(onClick: () -> Unit, emoji: String, label: String, showLabel: Boolean, color: Color) {
    androidx.compose.foundation.layout.Column(
        modifier = Modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(text = emoji, fontSize = 16.sp)
        if (showLabel) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}
