package com.gumthala.learningapp.ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView

/** Tints the system status bar per-screen, matching the mockup's purple/purple-dark/white status bars. */
@Composable
fun ScreenStatusBar(color: Color) {
    val view = LocalView.current
    if (view.isInEditMode) return
    val activity = view.context as? Activity ?: return
    DisposableEffect(color) {
        val window = activity.window
        val previousColor = window.statusBarColor
        val controller = androidx.core.view.WindowInsetsControllerCompat(window, view)
        val previousLightStatusBars = controller.isAppearanceLightStatusBars
        window.statusBarColor = color.toArgb()
        controller.isAppearanceLightStatusBars = color.luminance() > 0.5f
        onDispose {
            window.statusBarColor = previousColor
            controller.isAppearanceLightStatusBars = previousLightStatusBars
        }
    }
}

private fun Color.toArgb(): Int = android.graphics.Color.argb(
    (alpha * 255).toInt(), (red * 255).toInt(), (green * 255).toInt(), (blue * 255).toInt()
)
