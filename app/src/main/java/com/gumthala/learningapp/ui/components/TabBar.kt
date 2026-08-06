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
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body

/** One `.tabbar` slot: an emoji plus its caption. */
data class TabBarItem(val emoji: String, val label: String)

/**
 * Generic `.tabbar` renderer shared by the Student, Teacher and Admin shells —
 * each role sees a different item set, but the same 52px/1px-rule/purple-active
 * treatment from the mockup.
 */
@Composable
fun RoleTabBar(
    items: List<TabBarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().background(Color.White)) {
        HorizontalDivider(thickness = 1.dp, color = AppColors.Divider)
        Row(
            modifier = Modifier.fillMaxWidth().height(65.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                val active = index == selectedIndex
                val interaction = remember { MutableInteractionSource() }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            onClick = { onSelect(index) }
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Text(item.emoji, style = body(TextSize.Body, FontWeight.Bold))
                    Text(
                        item.label,
                        style = body(TextSize.Micro, FontWeight.ExtraBold),
                        color = if (active) AppColors.Purple else AppColors.Muted
                    )
                }
            }
        }
    }
}

object TeacherTabs {
    val items = listOf(
        TabBarItem("🏠", "Home"),
        TabBarItem("👥", "Students"),
        TabBarItem("🖼️", "Slides"),
        TabBarItem("👤", "Profile")
    )
    const val HOME = 0
    const val STUDENTS = 1
    const val SLIDES = 2
    const val PROFILE = 3
}

object AdminTabs {
    val items = listOf(
        TabBarItem("🏠", "Home"),
        TabBarItem("👥", "People"),
        TabBarItem("📚", "Content"),
        TabBarItem("👤", "Profile")
    )
    const val HOME = 0
    const val PEOPLE = 1
    const val CONTENT = 2
    const val PROFILE = 3
}
