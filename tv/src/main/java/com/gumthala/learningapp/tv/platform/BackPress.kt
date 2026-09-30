package com.gumthala.learningapp.tv.platform

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

/** The remote's BACK key. Kept here so the screens themselves never import Android. */
@Composable
fun BackPress(enabled: Boolean, onBack: () -> Unit) {
    BackHandler(enabled = enabled, onBack = onBack)
}
