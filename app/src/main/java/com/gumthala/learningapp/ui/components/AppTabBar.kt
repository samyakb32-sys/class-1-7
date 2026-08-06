package com.gumthala.learningapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.nav.TopLevelDestination
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body

/**
 * `.tabbar` — 52px white bar, 1px #EFEDFA top rule, emoji over an 9px caption,
 * active item in purple.
 */
@Composable
fun AppTabBar(
    selected: TopLevelDestination,
    onSelect: (TopLevelDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().background(Color.White)) {
        HorizontalDivider(thickness = 1.dp, color = AppColors.Divider)
        Row(
            modifier = Modifier.fillMaxWidth().height(65.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopLevelDestination.entries.forEach { destination ->
                val active = destination == selected
                val interaction = remember { MutableInteractionSource() }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            onClick = { onSelect(destination) }
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(destination.emoji, style = body(TextSize.Body, FontWeight.Bold))
                    Text(
                        destination.label,
                        style = body(TextSize.Micro, FontWeight.ExtraBold),
                        color = if (active) AppColors.Purple else AppColors.Muted
                    )
                }
            }
        }
    }
}
