package com.gumthala.learningapp.ui.student.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.data.local.entity.BadgeEntity
import com.gumthala.learningapp.domain.quiz.BadgeRules

/**
 * Plain, unstyled placeholder pending a mockup for this screen — lists earned badges only.
 */
@Composable
fun AchievementsScreen(viewModel: ProfileViewModel, onBack: () -> Unit) {
    val badges by viewModel.badges.collectAsState(initial = emptyList())
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Achievements") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(badges) { badge: BadgeEntity ->
                Text(
                    text = "${badgeEmoji(badge.badgeKey)}  ${badgeLabel(badge.badgeKey)}",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

private fun badgeEmoji(key: String) = when (key) {
    BadgeRules.FIRST_QUIZ -> "🎯"
    BadgeRules.PERFECT_SCORE -> "💯"
    BadgeRules.STREAK_3 -> "🔥"
    BadgeRules.STREAK_7 -> "🔥🔥"
    BadgeRules.STREAK_30 -> "🔥🔥🔥"
    else -> "🏅"
}

private fun badgeLabel(key: String) = when (key) {
    BadgeRules.FIRST_QUIZ -> "First Quiz Completed"
    BadgeRules.PERFECT_SCORE -> "Perfect Score"
    BadgeRules.STREAK_3 -> "3-Day Streak"
    BadgeRules.STREAK_7 -> "7-Day Streak"
    BadgeRules.STREAK_30 -> "30-Day Streak"
    else -> key
}
