package com.gumthala.learningapp.ui.screens.roster

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.screens.SubjectCardUi
import com.gumthala.learningapp.ui.screens.auth.FieldLabel
import com.gumthala.learningapp.ui.screens.auth.InputBox
import com.gumthala.learningapp.ui.screens.auth.LoginHero
import com.gumthala.learningapp.ui.screens.auth.LoginSubmitButton
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

data class QuestionDraftInput(
    val promptEn: String,
    val optionsEn: List<String>,
    val correctOptionIndex: Int
)

/**
 * The other previously-missing screen: a teacher writing their own question
 * for a chapter they've already picked. Only English is captured here —
 * ContentRepository.saveQuestion takes a full LocalizedText for Marathi/Hindi
 * too, but there's no translation-assist in this build, so a teacher without
 * those languages would just leave them blank; capturing only English avoids
 * silently shipping empty-string "translations". A trilingual-content workflow
 * is really the content-corpus problem (see README), not a UI gap.
 *
 * Options are fixed at exactly 4 to match the MCQ spec and OptionGrid's layout.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuestionEditorScreen(
    chapterTitle: String,
    onSubmit: (QuestionDraftInput) -> Unit,
    modifier: Modifier = Modifier,
    submitError: String? = null,
    successMessage: String? = null,
    isSubmitting: Boolean = false
) {
    var prompt by remember { mutableStateOf("") }
    var optionA by remember { mutableStateOf("") }
    var optionB by remember { mutableStateOf("") }
    var optionC by remember { mutableStateOf("") }
    var optionD by remember { mutableStateOf("") }
    var correctIndex by remember { mutableStateOf(0) }

    val options = listOf(optionA, optionB, optionC, optionD)
    val setters = listOf<(String) -> Unit>({ optionA = it }, { optionB = it }, { optionC = it }, { optionD = it })
    val letters = listOf("A", "B", "C", "D")

    Column(modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        LoginHero("✏️", "New Question", chapterTitle)

        Column(modifier = Modifier.padding(20.dp)) {
            FieldLabel("Question")
            InputBox(value = prompt, placeholder = "e.g. What is 4 + 6?", onValueChange = { prompt = it })

            Column(modifier = Modifier.padding(top = 14.dp)) {
                FieldLabel("Options — tap the circle to mark the correct one")
                letters.forEachIndexed { index, letter ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val isCorrect = index == correctIndex
                        androidx.compose.foundation.layout.Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isCorrect) AppColors.Green else AppColors.SurfaceSoft)
                                .clickable { correctIndex = index }
                                .padding(10.dp)
                        ) {
                            Text(
                                letter,
                                style = body(TextSize.Small, FontWeight.Bold),
                                color = if (isCorrect) Color.White else AppColors.Muted
                            )
                        }
                        InputBox(
                            value = options[index],
                            placeholder = "Option $letter",
                            onValueChange = setters[index],
                            modifier = Modifier.weight(1f).padding(start = 10.dp)
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
                text = if (isSubmitting) "Saving…" else "Save Question",
                enabled = prompt.isNotBlank() && options.all { it.isNotBlank() } && !isSubmitting,
                onClick = {
                    onSubmit(QuestionDraftInput(prompt.trim(), options.map { it.trim() }, correctIndex))
                    prompt = ""; optionA = ""; optionB = ""; optionC = ""; optionD = ""; correctIndex = 0
                }
            )
        }
    }
}
