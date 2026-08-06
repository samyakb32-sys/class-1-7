package com.gumthala.learningapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    secondary = Violet,
    background = ScaffoldBg,
    surface = Color.White,
    onSurface = Ink,
    onBackground = Ink,
    error = LogoutRed
)

private val DarkColors = darkColorScheme(
    primary = PurpleLight,
    onPrimary = Color.White,
    secondary = Violet,
    background = Color(0xFF14122B),
    surface = Color(0xFF1E1B3A),
    onSurface = Color.White,
    onBackground = Color.White,
    error = LogoutRed
)

@Composable
fun LearningAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}
