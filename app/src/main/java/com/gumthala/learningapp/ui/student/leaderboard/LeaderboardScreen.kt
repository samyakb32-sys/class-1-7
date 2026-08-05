package com.gumthala.learningapp.ui.student.leaderboard

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.gumthala.learningapp.data.local.dao.LeaderboardRow
import com.gumthala.learningapp.di.LocalAppContainer
import com.gumthala.learningapp.ui.student.StudentSession

/** Plain, unstyled placeholder pending a mockup — per-class XP leaderboard. */
@Composable
fun LeaderboardScreen(session: StudentSession, onBack: () -> Unit) {
    val container = LocalAppContainer.current
    val rows by container.progressRepository.observeLeaderboard(session.classLevel).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Class ${session.classLevel} Leaderboard") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            itemsIndexed(rows) { index, row: LeaderboardRow ->
                ListItem(
                    headlineContent = { Text("${index + 1}. ${row.avatarEmoji} ${row.name}") },
                    supportingContent = { Text("🔥 ${row.streakDays}-day streak") },
                    trailingContent = { Text("⭐ ${row.xp} XP") }
                )
            }
        }
    }
}
