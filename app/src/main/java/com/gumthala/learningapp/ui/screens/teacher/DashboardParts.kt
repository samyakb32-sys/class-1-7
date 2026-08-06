package com.gumthala.learningapp.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

/**
 * `.stat-box` — a coloured tile with a big number and a small caption. Shared by
 * Admin and Teacher dashboards (the mockup's `.stat-grid` is 2 columns on both).
 */
@Composable
fun StatBox(number: String, label: String, background: Color, foreground: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.Soft))
            .background(background)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(number, style = display(TextSize.Title, FontWeight.ExtraBold), color = foreground)
        Text(label, style = body(TextSize.Micro, FontWeight.Bold), color = foreground)
    }
}

/**
 * `.action-row` — an icon tile + title/subtitle + trailing chevron, used for
 * every management entry point on the Admin and Teacher dashboards.
 */
@Composable
fun ActionRow(
    emoji: String,
    iconBg: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .clip(RoundedCornerShape(Radius.Soft))
            .background(AppColors.SurfaceSoft)
            .clickable(onClick = onClick)
            .padding(11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(Radius.Cell))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, style = body(TextSize.Body))
        }
        Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
            Text(title, style = display(TextSize.Small, FontWeight.Bold), color = AppColors.Ink)
            Text(subtitle, style = body(TextSize.Micro, FontWeight.Bold), color = AppColors.Muted)
        }
        Text("›", style = display(TextSize.Title, FontWeight.Bold), color = AppColors.Muted)
    }
}
