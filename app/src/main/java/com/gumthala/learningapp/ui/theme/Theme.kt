package com.gumthala.learningapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = AppColors.Purple,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = AppColors.Yellow,
    background = AppColors.Bg,
    onBackground = AppColors.Ink,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = AppColors.Ink,
    surfaceVariant = AppColors.SurfaceSoft,
    onSurfaceVariant = AppColors.Muted,
    error = AppColors.LogoutFg
)

/** The mockup is light-only; there is no dark variant to convert. */
@Composable
fun LearningAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
