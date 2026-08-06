package com.gumthala.learningapp.ui.roster

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.data.repository.AuthRepository
import kotlinx.coroutines.launch

/** Plain, unstyled placeholder pending a mockup — Admin-only: register a new Teacher account. */
@Composable
fun RegisterTeacherScreen(authRepository: AuthRepository, onBack: () -> Unit, onRegistered: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var assignedClasses by remember { mutableStateOf(setOf<Int>()) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register Teacher") },
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
                label = { Text("Teacher name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Temporary password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
            )
            Text("Assigned classes", modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                (1..7).forEach { level ->
                    FilterChip(
                        selected = level in assignedClasses,
                        onClick = {
                            assignedClasses = if (level in assignedClasses) assignedClasses - level else assignedClasses + level
                        },
                        label = { Text("$level") }
                    )
                }
            }
            if (error != null) {
                Text(error.orEmpty(), modifier = Modifier.padding(top = 12.dp))
            }
            Button(
                onClick = {
                    when {
                        name.isBlank() || email.isBlank() || password.isBlank() -> error = "Fill in all fields"
                        assignedClasses.isEmpty() -> error = "Pick at least one class"
                        else -> scope.launch {
                            authRepository.registerTeacher(name, email, password, assignedClasses.sorted())
                            onRegistered()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            ) { Text("Register") }
        }
    }
}
