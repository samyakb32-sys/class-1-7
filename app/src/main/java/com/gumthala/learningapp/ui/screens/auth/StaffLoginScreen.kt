package com.gumthala.learningapp.ui.screens.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.core.SupportContact
import com.gumthala.learningapp.ui.screens.auth.AppRole
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body

data class StaffLoginInput(val email: String, val password: String)

/**
 * `Teacher / Admin Login` — shared shell for both staff roles; only the hero
 * copy changes. Password recovery is "email support", per spec — there is no
 * self-service reset since students/teachers have no way to verify identity
 * offline.
 */
@Composable
fun StaffLoginScreen(
    role: AppRole,
    onSubmit: (StaffLoginInput) -> Unit,
    modifier: Modifier = Modifier,
    submitError: String? = null,
    isSubmitting: Boolean = false,
    onBack: (() -> Unit)? = null
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val (emoji, title) = when (role) {
        AppRole.ADMIN -> "🛡️" to "Admin Login"
        else -> "🍎" to "Staff Login"
    }

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        LoginHero(emoji, title, "Sign in with your school email", onBack = onBack)

        Column(modifier = Modifier.padding(20.dp)) {
            FieldLabel("Email")
            InputBox(
                value = email,
                placeholder = "teacher@gumthalaschool.in",
                onValueChange = { email = it },
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Email,
                capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.None
            )

            Column(modifier = Modifier.padding(top = 12.dp)) {
                FieldLabel("Password")
                InputBox(
                    value = password,
                    placeholder = "••••••••",
                    onValueChange = { password = it },
                    masked = true,
                    capitalization = androidx.compose.ui.text.input.KeyboardCapitalization.None
                )
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
                text = if (isSubmitting) "Signing in…" else "Sign In",
                enabled = email.isNotBlank() && password.isNotBlank() && !isSubmitting,
                onClick = { onSubmit(StaffLoginInput(email.trim(), password)) }
            )
            HelperLink("Forgot password? ${SupportContact.EMAIL}")
        }
    }
}
