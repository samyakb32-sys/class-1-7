package com.gumthala.learningapp.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

/** TEMPORARY placeholder (plain Material) — see RoleSelectScreen for context. */
@Composable
fun StaffLoginScreen(viewModel: AuthViewModel, onBack: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var asAdmin by remember { mutableStateOf(false) }
    val error by viewModel.errorMessage.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Teacher / Admin Login")
        Row(modifier = Modifier.padding(top = 12.dp)) {
            FilterChip(selected = !asAdmin, onClick = { asAdmin = false }, label = { Text("Teacher") })
            FilterChip(selected = asAdmin, onClick = { asAdmin = true }, label = { Text("Admin") }, modifier = Modifier.padding(start = 8.dp))
        }
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        if (error != null) {
            Text(error.orEmpty(), modifier = Modifier.padding(top = 8.dp))
        }
        if (asAdmin) {
            Text(
                "First time? admin@classapp.local / Admin@123 — change it after logging in.",
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        Button(
            onClick = { viewModel.staffLogin(email, password, asAdmin) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) { Text("Log In") }
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Back") }
    }
}
