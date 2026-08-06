package com.gumthala.learningapp.ui.screens.roster

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.core.ClassLevels
import com.gumthala.learningapp.ui.screens.auth.FieldLabel
import com.gumthala.learningapp.ui.screens.auth.InputBox
import com.gumthala.learningapp.ui.screens.auth.LoginHero
import com.gumthala.learningapp.ui.screens.auth.LoginSubmitButton
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body

data class RegisterTeacherInput(
    val name: String,
    val email: String,
    val temporaryPassword: String,
    val assignedClasses: List<Int>
)

/**
 * Admin-only: create a teacher account. Mirrors AuthRepository.registerStaff's
 * rules — role is fixed to TEACHER here (admins get made a different way, not
 * exposed in UI at all, since the founder admin already exists from SeedLoader
 * and adding more admins isn't in scope yet).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterTeacherScreen(
    onSubmit: (RegisterTeacherInput) -> Unit,
    modifier: Modifier = Modifier,
    submitError: String? = null,
    successMessage: String? = null,
    isSubmitting: Boolean = false
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var assigned by remember { mutableStateOf(setOf<Int>()) }

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        LoginHero("🍎", "Register Teacher", "Add a teacher and assign their classes")

        Column(modifier = Modifier.padding(20.dp)) {
            FieldLabel("Teacher's Name")
            InputBox(value = name, placeholder = "e.g. Mrs. Patil", onValueChange = { name = it })

            Column(modifier = Modifier.padding(top = 12.dp)) {
                FieldLabel("Email")
                InputBox(
                    value = email,
                    placeholder = "teacher@gumthalaschool.in",
                    onValueChange = { email = it },
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                    capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.None
                )
            }

            Column(modifier = Modifier.padding(top = 12.dp)) {
                FieldLabel("Temporary Password")
                InputBox(
                    value = password,
                    placeholder = "Teacher changes this later",
                    onValueChange = { password = it },
                    capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.None
                )
            }

            Column(modifier = Modifier.padding(top = 14.dp)) {
                FieldLabel("Assign Classes")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ClassLevels.ALL.forEach { level ->
                        val selected = level in assigned
                        Text(
                            level.toString(),
                            style = body(TextSize.Small, FontWeight.Bold),
                            color = if (selected) Color.White else AppColors.Muted,
                            modifier = Modifier
                                .clip(RoundedCornerShape(Radius.Cell))
                                .background(if (selected) AppColors.Purple else AppColors.SurfaceSoft)
                                .clickable {
                                    assigned = if (selected) assigned - level else assigned + level
                                }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        )
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
            if (successMessage != null) {
                Text(
                    successMessage,
                    style = body(TextSize.Tiny, FontWeight.Bold),
                    color = AppColors.Green,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            LoginSubmitButton(
                text = if (isSubmitting) "Adding…" else "Add Teacher",
                enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank() &&
                    assigned.isNotEmpty() && !isSubmitting,
                onClick = {
                    onSubmit(
                        RegisterTeacherInput(name.trim(), email.trim(), password, assigned.sorted())
                    )
                }
            )
        }
    }
}
