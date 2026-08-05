package com.gumthala.learningapp.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.data.session.SessionManager
import com.gumthala.learningapp.domain.model.AppLanguage
import kotlinx.coroutines.launch

/** Plain, unstyled placeholder pending a mockup — app-wide content language switch. */
@Composable
fun LanguageSettingsScreen(sessionManager: SessionManager, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("App Language")
            AppLanguage.entries.forEach { lang ->
                Button(
                    onClick = { scope.launch { sessionManager.setLanguage(lang) } },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(lang.displayName) }
            }
        }
    }
}
