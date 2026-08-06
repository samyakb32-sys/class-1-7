package com.gumthala.learningapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.TextUnit

/**
 * The mockup specifies Baloo 2 (display) and Nunito (body) from Google Fonts.
 *
 * This deliberately does NOT use the downloadable-fonts API (GoogleFont +
 * Font(googleFont = ...)): that requires Google Play Services, a live network
 * call on first use, and a signing-certificate array resource — three things
 * an offline-first rural-school app on a budget Android device should not
 * depend on just to render text. If the exact typefaces matter, bundle the
 * .ttf/.otf files under res/font/ and swap the two FontFamily.Default values
 * below for `FontFamily(Font(R.font.baloo2_bold), ...)`. Until then this
 * renders with the platform sans-serif at the mockup's weights and sizes,
 * which keeps every screen's proportions intact.
 */
val DisplayFamily: FontFamily = FontFamily.Default
val BodyFamily: FontFamily = FontFamily.Default

private val trimmed = LineHeightStyle(
    alignment = LineHeightStyle.Alignment.Center,
    trim = LineHeightStyle.Trim.None
)

fun display(size: TextUnit, weight: FontWeight = FontWeight.Bold) =
    TextStyle(fontFamily = DisplayFamily, fontWeight = weight, fontSize = size, lineHeightStyle = trimmed)

fun body(size: TextUnit, weight: FontWeight = FontWeight.Bold) =
    TextStyle(fontFamily = BodyFamily, fontWeight = weight, fontSize = size, lineHeightStyle = trimmed)

val AppTypography = Typography(
    headlineSmall = display(TextSize.Display, FontWeight.ExtraBold),
    titleLarge = display(TextSize.Screen, FontWeight.ExtraBold),
    titleMedium = display(TextSize.Header, FontWeight.Bold),
    titleSmall = display(TextSize.Title, FontWeight.Bold),
    labelLarge = display(TextSize.Label, FontWeight.Bold),
    labelMedium = body(TextSize.Small, FontWeight.Bold),
    labelSmall = body(TextSize.Micro, FontWeight.Bold),
    bodyMedium = body(TextSize.Body, FontWeight.SemiBold),
    bodySmall = body(TextSize.Tiny, FontWeight.Bold)
)
