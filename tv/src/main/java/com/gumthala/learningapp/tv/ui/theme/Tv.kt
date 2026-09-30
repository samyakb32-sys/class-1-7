package com.gumthala.learningapp.tv.ui.theme

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit

/** Brand colours (same purple as the phone app) plus the friendly extras a TV needs. */
object Tv {
    val Purple = Color(0xFF5B4FE8)
    val PurpleDark = Color(0xFF3B2FB8)
    val PurpleDeep = Color(0xFF2A1F8F)
    val PurpleLight = Color(0xFF7C6EF0)

    val Sun = Color(0xFFFFC93C)
    val SunDeep = Color(0xFFFF9F1C)
    val Green = Color(0xFF22C55E)
    val GreenDark = Color(0xFF15803D)
    val GreenSoft = Color(0xFFDCFCE7)
    val Pink = Color(0xFFFF5C8A)
    val PinkSoft = Color(0xFFFFE3EC)
    val Blue = Color(0xFF3B82F6)
    val BlueSoft = Color(0xFFDCEBFF)
    val Orange = Color(0xFFFB923C)
    val OrangeSoft = Color(0xFFFFE8D6)
    val Red = Color(0xFFEF4444)
    val RedSoft = Color(0xFFFEE2E2)
    val Teal = Color(0xFF14B8A6)
    val Violet = Color(0xFF8B5CF6)
    val VioletSoft = Color(0xFFEDE4FF)
    val YellowSoft = Color(0xFFFEF3D6)

    val Ink = Color(0xFF1E1B3A)
    val Muted = Color(0xFF6B6890)
    val Paper = Color.White
    val PaperSoft = Color(0xFFF7F6FD)
    val Line = Color(0xFFE4E1FA)
    val FocusRing = Color(0xFFFFD23F)

    /** Text. Sizes are in the normalised 960dp-wide space (see [TvScaled]); sp == dp here. */
    fun text(
        size: TextUnit,
        color: Color = Ink,
        weight: FontWeight = FontWeight.Bold,
        family: FontFamily = FontFamily.Default,
    ) = TextStyle(color = color, fontSize = size, fontWeight = weight, fontFamily = family)
}

/**
 * Every TV reports a different density (tvdpi, xhdpi, 4K...). Rather than sizing for each, the whole UI
 * is laid out in a virtual 960dp-wide space and scaled to the real screen. 1 dp and 1 sp are then the
 * same on every TV, and text is big enough for a child sitting across the room.
 */
@Composable
fun TvScaled(content: @Composable () -> Unit) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val target = constraints.maxWidth.toFloat() / 960f
        CompositionLocalProvider(LocalDensity provides Density(density = target, fontScale = 1f)) {
            content()
        }
    }
}
