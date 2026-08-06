package com.gumthala.learningapp.ui.roster

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.data.repository.AuthRepository
import com.gumthala.learningapp.domain.model.Role
import kotlinx.coroutines.launch

/**
 * Plain, unstyled placeholder pending a mockup — registers a new student (no self-signup;
 * a Teacher registers into their own assigned classes, an Admin into any class).
 */
@Composable
fun RegisterStudentScreen(
    authRepository: AuthRepository,
    registeredByUserId: String,
    registeredByRole: Role,
    availableClasses: List<Int>,
    onBack: () -> Unit,
    onRegistered: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var classLevel by remember { mutableStateOf(availableClasses.firstOrNull() ?: 1) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register Student") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Student name") },
                modifier = Modifier.fillMaxWidth()
            )
            Text("Class", modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                availableClasses.forEach { level ->
                    FilterChip(
                        selected = classLevel == level,
                        onClick = { classLevel = level },
                        label = { Text("$level") }
                    )
                }
            }
            if (error != null) {
                Text(error.orEmpty(), modifier = Modifier.padding(top = 12.dp))
            }
            Button(
                onClick = {
                    if (name.isBlank()) {
                        error = "Enter the student's name"
                    } else {
                        scope.launch {
                            authRepository.registerStudent(name, classLevel, registeredByUserId, registeredByRole)
                            onRegistered()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            ) { Text("Register") }
        }
    }
}
