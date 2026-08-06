package com.gumthala.learningapp.ui.admin

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
import com.gumthala.learningapp.data.local.entity.TeacherEntity
import com.gumthala.learningapp.di.LocalAppContainer
import com.gumthala.learningapp.domain.model.ContentConstants
import com.gumthala.learningapp.domain.model.Role
import com.gumthala.learningapp.ui.content.TeacherContentScreen
import com.gumthala.learningapp.ui.roster.RegisterStudentScreen
import com.gumthala.learningapp.ui.roster.RegisterTeacherScreen
import com.gumthala.learningapp.ui.slides.SlideDecksScreen

private enum class AdminScreenState { HOME, REGISTER_STUDENT, REGISTER_TEACHER, SLIDES, QUESTIONS, HELP }

/**
 * TEMPORARY placeholder (plain Material defaults) — no Admin dashboard mockup was
 * provided yet. Lets the admin register students/teachers, see rosters, and browse
 * Teaching Slides — functional but unstyled.
 */
@Composable
fun AdminHomeScreen(adminId: String, adminName: String, onLogout: () -> Unit) {
    val container = LocalAppContainer.current
    var screenState by remember { mutableStateOf(AdminScreenState.HOME) }

    when (screenState) {
        AdminScreenState.REGISTER_STUDENT -> {
            RegisterStudentScreen(
                authRepository = container.authRepository,
                registeredByUserId = adminId,
                registeredByRole = Role.ADMIN,
                availableClasses = (1..7).toList(),
                onBack = { screenState = AdminScreenState.HOME },
                onRegistered = { screenState = AdminScreenState.HOME }
            )
        }
        AdminScreenState.REGISTER_TEACHER -> {
            RegisterTeacherScreen(
                authRepository = container.authRepository,
                onBack = { screenState = AdminScreenState.HOME },
                onRegistered = { screenState = AdminScreenState.HOME }
            )
        }
        AdminScreenState.SLIDES -> {
            SlideDecksScreen(
                slideRepository = container.slideRepository,
                teacherId = null,
                onBack = { screenState = AdminScreenState.HOME }
            )
        }
        AdminScreenState.QUESTIONS -> {
            TeacherContentScreen(
                contentRepository = container.contentRepository,
                teacherId = null,
                availableClasses = (1..7).toList(),
                onBack = { screenState = AdminScreenState.HOME }
            )
        }
        AdminScreenState.HELP -> {
            com.gumthala.learningapp.ui.common.HelpSupportScreen(onBack = { screenState = AdminScreenState.HOME })
        }
        AdminScreenState.HOME -> {
            val teachers by container.rosterRepository.observeAllTeachers().collectAsState(initial = emptyList<TeacherEntity>())
            val students by container.rosterRepository.observeAllStudents().collectAsState(initial = emptyList<StudentEntity>())

            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("Welcome, $adminName")
                Text("${students.size} students · ${teachers.size} teachers registered")

                Row(modifier = Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { screenState = AdminScreenState.REGISTER_STUDENT }, modifier = Modifier.weight(1f)) {
                        Text("+ Student")
                    }
                    Button(onClick = { screenState = AdminScreenState.REGISTER_TEACHER }, modifier = Modifier.weight(1f)) {
                        Text("+ Teacher")
                    }
                }
                OutlinedButton(
                    onClick = { screenState = AdminScreenState.SLIDES },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) { Text("Teaching Slides") }
                OutlinedButton(
                    onClick = { screenState = AdminScreenState.QUESTIONS },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) { Text("Manage Quiz Questions") }

                Text("Teachers:", modifier = Modifier.padding(top = 16.dp, bottom = 4.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(teachers) { teacher ->
                        ListItem(
                            headlineContent = { Text(teacher.name) },
                            supportingContent = { Text("${teacher.email} · Classes ${teacher.assignedClasses.joinToString()}") }
                        )
                        HorizontalDivider()
                    }
                }
                Text(
                    "Content coverage: ${ContentConstants.CHAPTERS_PER_SUBJECT} chapters × 4 subjects × 7 classes seeded by default.",
                    modifier = Modifier.padding(top = 8.dp)
                )
                Button(onClick = { screenState = AdminScreenState.HELP }, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                    Text("Help & Support")
                }
                Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Logout") }
            }
        }
    }
}
