package com.gumthala.learningapp.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * TEMPORARY placeholder screen (plain Material defaults only, no custom design) — the
 * commissioned mockup didn't include a Role Select / Login flow. Replace once that
 * mockup is provided; see the project README for details.
 */
@Composable
fun RoleSelectScreen(onSelectStudent: () -> Unit, onSelectStaff: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Class 1 to 7 Learning Course",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            "(Placeholder login — awaiting the login/role-select mockup)",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(onClick = onSelectStudent, modifier = Modifier.fillMaxWidth()) { Text("I'm a Student") }
        Button(onClick = onSelectStaff, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) { Text("I'm a Teacher / Admin") }
    }
}
