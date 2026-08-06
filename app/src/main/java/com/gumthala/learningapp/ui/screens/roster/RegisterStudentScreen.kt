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

data class RegisterStudentInput(val name: String, val classLevel: Int, val rollNo: String?)

/**
 * The screen that was missing entirely: a teacher or admin registering a new
 * student. This is the ONLY way a student row gets created — StudentLoginScreen
 * has no signup path by design (see AuthRepository.signInStudent), so without
 * this screen no student could ever sign in.
 *
 * [allowedClassLevels] restricts the class chips: a teacher only sees their own
 * assigned classes (enforced again server-side by AuthRepository.registerStudent,
 * this is just so the UI doesn't offer an option that will be rejected).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterStudentScreen(
    allowedClassLevels: List<Int> = ClassLevels.ALL,
    onSubmit: (RegisterStudentInput) -> Unit,
    modifier: Modifier = Modifier,
    submitError: String? = null,
    successMessage: String? = null,
    isSubmitting: Boolean = false
) {
    var name by remember { mutableStateOf("") }
    var classLevel by remember { mutableStateOf(allowedClassLevels.firstOrNull() ?: 1) }
    var rollNo by remember { mutableStateOf("") }

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        LoginHero("👧", "Register Student", "Add a new student to your class")

        Column(modifier = Modifier.padding(20.dp)) {
            FieldLabel("Student's Name")
            InputBox(value = name, placeholder = "e.g. Aarohi Sharma", onValueChange = { name = it })

            Column(modifier = Modifier.padding(top = 14.dp)) {
                FieldLabel("Class")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    allowedClassLevels.forEach { level ->
                        ClassChip(level, selected = level == classLevel) { classLevel = level }
                    }
                }
            }

            Column(modifier = Modifier.padding(top = 14.dp)) {
                FieldLabel("Roll No. (optional)")
                InputBox(value = rollNo, placeholder = "e.g. 12", onValueChange = { rollNo = it })
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
                text = if (isSubmitting) "Adding…" else "Add Student",
                enabled = name.isNotBlank() && !isSubmitting,
                onClick = { onSubmit(RegisterStudentInput(name.trim(), classLevel, rollNo.trim().ifBlank { null })) }
            )
        }
    }
}

@Composable
private fun ClassChip(level: Int, selected: Boolean, onClick: () -> Unit) {
    Text(
        level.toString(),
        style = body(TextSize.Small, FontWeight.Bold),
        color = if (selected) Color.White else AppColors.Muted,
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.Cell))
            .background(if (selected) AppColors.Purple else AppColors.SurfaceSoft)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}
