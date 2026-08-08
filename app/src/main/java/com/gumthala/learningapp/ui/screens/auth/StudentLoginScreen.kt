package com.gumthala.learningapp.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.core.AppLanguage
import com.gumthala.learningapp.core.ClassLevels
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body

data class StudentLoginInput(val name: String, val classLevel: Int, val language: AppLanguage)

/**
 * `Student Login` — name + class + language, no password. The mockup's helper
 * line makes the "no self-signup" rule explicit: "Not registered? Ask your teacher".
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudentLoginScreen(
    onSubmit: (StudentLoginInput) -> Unit,
    modifier: Modifier = Modifier,
    submitError: String? = null,
    isSubmitting: Boolean = false,
    onBack: (() -> Unit)? = null
) {
    var name by remember { mutableStateOf("") }
    var classLevel by remember { mutableStateOf(1) }
    var language by remember { mutableStateOf(AppLanguage.ENGLISH) }

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        LoginHero("🎒", "Hi Explorer!", "Enter your name and pick your class", onBack = onBack)

        Column(modifier = Modifier.padding(20.dp)) {
            FieldLabel("Your Name")
            InputBox(value = name, placeholder = "e.g. Aarohi Sharma", onValueChange = { name = it })

            Column(modifier = Modifier.padding(top = 14.dp)) {
                FieldLabel("Select Class")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ClassLevels.ALL.forEach { level ->
                        SelectChip(text = level.toString(), selected = level == classLevel) {
                            classLevel = level
                        }
                    }
                }
            }

            Column(modifier = Modifier.padding(top = 14.dp)) {
                FieldLabel("Language")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AppLanguage.entries.forEach { lang ->
                        SelectChip(text = lang.nativeLabel, selected = lang == language) {
                            language = lang
                        }
                    }
                }
            }

            if (submitError != null) {
                Text(
                    submitError,
                    style = body(TextSize.Tiny, FontWeight.Bold),
                    color = AppColors.LogoutFg,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            LoginSubmitButton(
                text = if (isSubmitting) "Starting…" else "Start Learning →",
                enabled = name.isNotBlank() && !isSubmitting,
                onClick = { onSubmit(StudentLoginInput(name.trim(), classLevel, language)) }
            )
            HelperLink("Not registered? Ask your teacher")
        }
    }
}

/** `.chip-select span` — a small pill, purple when selected. */
@Composable
private fun SelectChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text,
        style = body(TextSize.Small, FontWeight.Bold),
        color = if (selected) androidx.compose.ui.graphics.Color.White else AppColors.Muted,
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.Cell))
            .background(if (selected) AppColors.Purple else AppColors.SurfaceSoft)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}
