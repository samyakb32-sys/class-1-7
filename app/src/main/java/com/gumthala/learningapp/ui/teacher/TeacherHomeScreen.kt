package com.gumthala.learningapp.ui.teacher

import androidx.compose.foundation.layout.Arrangement
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
import com.gumthala.learningapp.data.local.entity.StudentEntity
import com.gumthala.learningapp.di.LocalAppContainer

/**
 * TEMPORARY placeholder (plain Material defaults) — no Teacher dashboard mockup was
 * provided yet. Shows the students this teacher has registered, functional but unstyled.
 */
@Composable
fun TeacherHomeScreen(teacherId: String, teacherName: String, onLogout: () -> Unit) {
    val container = LocalAppContainer.current
    val students by container.rosterRepository.observeStudentsRegisteredBy(teacherId)
        .collectAsState(initial = emptyList<StudentEntity>())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Welcome, $teacherName")
        Text("Students you've registered:", modifier = Modifier.padding(top = 12.dp, bottom = 4.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(students) { student ->
                ListItem(
                    headlineContent = { Text(student.name) },
                    supportingContent = { Text("Class ${student.classLevel}") }
                )
                HorizontalDivider()
            }
        }
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("Logout") }
    }
}
