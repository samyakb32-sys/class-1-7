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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body

/**
 * Forced gate shown once, right after a staff member signs in with a
 * still-default password — either the founder admin's seeded credentials, or
 * a password an admin just reset for someone. Nothing else in the app is
 * reachable until this succeeds; see RootNavHost's mustChangePassword check.
 */
@Composable
fun ChangePasswordScreen(
    onSubmit: (String) -> Unit,
    modifier: Modifier = Modifier,
    submitError: String? = null,
    isSubmitting: Boolean = false
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val tooShort = newPassword.isNotEmpty() && newPassword.length < 6
    val mismatch = confirmPassword.isNotEmpty() && newPassword != confirmPassword
    val canSubmit = newPassword.length >= 6 && newPassword == confirmPassword && !isSubmitting

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        LoginHero("🔒", "Set a New Password", "This is your first sign-in — pick a password only you know")

        Column(modifier = Modifier.padding(20.dp)) {
            FieldLabel("New Password")
            InputBox(
                value = newPassword,
                placeholder = "At least 6 characters",
                onValueChange = { newPassword = it },
                masked = true,
                capitalization = KeyboardCapitalization.None
            )

            Column(modifier = Modifier.padding(top = 12.dp)) {
                FieldLabel("Confirm Password")
                InputBox(
                    value = confirmPassword,
                    placeholder = "Type it again",
                    onValueChange = { confirmPassword = it },
                    masked = true,
                    capitalization = KeyboardCapitalization.None
                )
            }

            if (tooShort) {
                Text(
                    "Password needs at least 6 characters.",
                    style = body(TextSize.Tiny, FontWeight.Bold),
                    color = AppColors.LogoutFg,
                    modifier = Modifier.padding(top = 10.dp)
                )
            } else if (mismatch) {
                Text(
                    "Passwords don't match.",
                    style = body(TextSize.Tiny, FontWeight.Bold),
                    color = AppColors.LogoutFg,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }
            if (submitError != null) {
                Text(
                    submitError,
                    style = body(TextSize.Tiny, FontWeight.Bold),
                    color = AppColors.LogoutFg,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            LoginSubmitButton(
                text = if (isSubmitting) "Saving…" else "Save & Continue",
                enabled = canSubmit,
                onClick = { onSubmit(newPassword) }
            )
        }
    }
}
