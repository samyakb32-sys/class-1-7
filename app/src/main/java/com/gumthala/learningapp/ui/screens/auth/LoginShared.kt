package com.gumthala.learningapp.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.components.PrimaryFullButton
import com.gumthala.learningapp.ui.components.PurpleGradient
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

/** `.login-hero` — the gradient banner shared by both login screens. */
@Composable
fun LoginHero(emoji: String, title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(PurpleGradient, RoundedCornerShape(bottomStart = Radius.Banner, bottomEnd = Radius.Banner))
            .padding(top = 32.dp, bottom = 24.dp, start = 20.dp, end = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(emoji, style = body(TextSize.Screen))
        Text(
            title,
            style = display(TextSize.Header, FontWeight.ExtraBold),
            color = Color.White,
            modifier = Modifier.padding(top = 6.dp)
        )
        Text(
            subtitle,
            style = body(TextSize.Small, FontWeight.SemiBold),
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

/** `.field label` — a small bold caption above an input. */
@Composable
fun FieldLabel(text: String) {
    Text(
        text,
        style = display(TextSize.Tiny, FontWeight.Bold),
        color = AppColors.Ink,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

/**
 * `.field .input` — the flat text-entry box used across every login field.
 * A real, typeable field styled to match the mockup's flat box exactly —
 * a login screen the student/teacher can't actually type into isn't usable.
 */
@Composable
fun InputBox(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    masked: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.Sentences
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = body(TextSize.Small, FontWeight.Bold).copy(color = AppColors.Ink),
        visualTransformation = if (masked) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, capitalization = capitalization),
        cursorBrush = androidx.compose.ui.graphics.SolidColor(AppColors.Purple),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.Card))
            .background(AppColors.SurfaceSoft)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        decorationBox = { inner ->
            if (value.isEmpty()) {
                Text(placeholder, style = body(TextSize.Small, FontWeight.Bold), color = AppColors.Muted)
            }
            inner()
        }
    )
}

/** `.helper-link` — the small purple line under the submit button. */
@Composable
fun HelperLink(text: String, onClick: (() -> Unit)? = null) {
    Text(
        text,
        style = body(TextSize.Tiny, FontWeight.Bold),
        color = AppColors.Purple,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    )
}

@Composable
fun LoginSubmitButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    PrimaryFullButton(text = text, onClick = onClick, enabled = enabled, modifier = Modifier.padding(top = 6.dp))
}
