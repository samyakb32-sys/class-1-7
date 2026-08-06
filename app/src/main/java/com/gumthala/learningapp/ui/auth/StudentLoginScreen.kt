package com.gumthala.learningapp.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** TEMPORARY placeholder (plain Material) — see RoleSelectScreen for context. */
@Composable
fun StudentLoginScreen(viewModel: AuthViewModel, onBack: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var classLevel by remember { mutableIntStateOf(1) }
    var menuExpanded by remember { mutableStateOf(false) }
    val error by viewModel.errorMessage.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Student Login")
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your name") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )
        Row(modifier = Modifier.padding(top = 12.dp)) {
            OutlinedButton(onClick = { menuExpanded = true }) { Text("Class $classLevel") }
            DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                (1..7).forEach { level ->
                    DropdownMenuItem(text = { Text("Class $level") }, onClick = { classLevel = level; menuExpanded = false })
                }
            }
        }
        if (error != null) {
            Text(error.orEmpty(), modifier = Modifier.padding(top = 8.dp))
        }
        Button(
            onClick = { viewModel.studentLogin(name, classLevel) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) { Text("Log In") }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Back") }
    }
}
