package com.gumthala.learningapp.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.data.local.entity.TeacherEntity
import com.gumthala.learningapp.di.LocalAppContainer

/**
 * TEMPORARY placeholder (plain Material defaults) — no Admin dashboard mockup was
 * provided yet. Shows every registered teacher, functional but unstyled.
 */
@Composable
fun AdminHomeScreen(adminName: String, onOpenHelp: () -> Unit, onLogout: () -> Unit) {
    val container = LocalAppContainer.current
    val teachers by container.rosterRepository.observeAllTeachers().collectAsState(initial = emptyList<TeacherEntity>())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Welcome, $adminName")
        Text("Teachers:", modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(teachers) { teacher ->
                ListItem(
                    headlineContent = { Text(teacher.name) },
                    supportingContent = { Text(teacher.email) }
                )
                HorizontalDivider()
            }
        }
        Button(onClick = onOpenHelp, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("Help & Support") }
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Logout") }
    }
}
