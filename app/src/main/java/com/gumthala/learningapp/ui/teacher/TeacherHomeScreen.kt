package com.gumthala.learningapp.ui.teacher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.data.local.entity.StudentEntity
import com.gumthala.learningapp.di.LocalAppContainer
import com.gumthala.learningapp.domain.model.Role
import com.gumthala.learningapp.ui.content.TeacherContentScreen
import com.gumthala.learningapp.ui.roster.RegisterStudentScreen
import com.gumthala.learningapp.ui.slides.SlideDecksScreen

private enum class TeacherScreenState { ROSTER, REGISTER_STUDENT, SLIDES, QUESTIONS }

/**
 * TEMPORARY placeholder (plain Material defaults) — no Teacher dashboard mockup was
 * provided yet. Lets a teacher register students into their assigned classes, see who
 * they've registered, and browse Teaching Slides — functional but unstyled.
 */
@Composable
fun TeacherHomeScreen(teacherId: String, teacherName: String, onLogout: () -> Unit) {
    val container = LocalAppContainer.current
    val teacher by container.rosterRepository.observeTeacher(teacherId).collectAsState(initial = null)
    var screenState by remember { mutableStateOf(TeacherScreenState.ROSTER) }

    when (screenState) {
        TeacherScreenState.REGISTER_STUDENT -> {
            RegisterStudentScreen(
                authRepository = container.authRepository,
                registeredByUserId = teacherId,
                registeredByRole = Role.TEACHER,
                availableClasses = teacher?.assignedClasses ?: listOf(1),
                onBack = { screenState = TeacherScreenState.ROSTER },
                onRegistered = { screenState = TeacherScreenState.ROSTER }
            )
        }
        TeacherScreenState.SLIDES -> {
            SlideDecksScreen(
                slideRepository = container.slideRepository,
                teacherId = teacherId,
                onBack = { screenState = TeacherScreenState.ROSTER }
            )
        }
        TeacherScreenState.QUESTIONS -> {
            TeacherContentScreen(
                contentRepository = container.contentRepository,
                teacherId = teacherId,
                availableClasses = teacher?.assignedClasses ?: listOf(1),
                onBack = { screenState = TeacherScreenState.ROSTER }
            )
        }
        TeacherScreenState.ROSTER -> {
            val students by container.rosterRepository.observeStudentsRegisteredBy(teacherId)
                .collectAsState(initial = emptyList<StudentEntity>())

            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Welcome, $teacherName")
                Text(
                    "Assigned classes: ${teacher?.assignedClasses?.joinToString() ?: "…"}",
                    modifier = Modifier.padding(top = 4.dp)
                )
                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { screenState = TeacherScreenState.REGISTER_STUDENT }, modifier = Modifier.weight(1f)) {
                        Text("+ Add Student")
                    }
                    OutlinedButton(onClick = { screenState = TeacherScreenState.SLIDES }, modifier = Modifier.weight(1f)) {
                        Text("Teaching Slides")
                    }
                }
                OutlinedButton(
                    onClick = { screenState = TeacherScreenState.QUESTIONS },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) { Text("My Quiz Questions") }
                Text("Students you've registered:", modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
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
    }
}
