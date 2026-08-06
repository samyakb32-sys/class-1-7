package com.gumthala.learningapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    headlineSmall = TextStyle(fontFamily = DisplayFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp),
    titleLarge = TextStyle(fontFamily = DisplayFontFamily, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp),
    titleMedium = TextStyle(fontFamily = DisplayFontFamily, fontWeight = FontWeight.Bold, fontSize = 15.sp),
    titleSmall = TextStyle(fontFamily = DisplayFontFamily, fontWeight = FontWeight.Bold, fontSize = 13.sp),
    labelLarge = TextStyle(fontFamily = DisplayFontFamily, fontWeight = FontWeight.Bold, fontSize = 12.sp),
    labelMedium = TextStyle(fontFamily = DisplayFontFamily, fontWeight = FontWeight.Bold, fontSize = 10.sp),
    labelSmall = TextStyle(fontFamily = DisplayFontFamily, fontWeight = FontWeight.Bold, fontSize = 9.sp),
    bodyLarge = TextStyle(fontFamily = BodyFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 14.sp),
    bodyMedium = TextStyle(fontFamily = BodyFontFamily, fontWeight = FontWeight.SemiBold, fontSize = 11.5.sp),
    bodySmall = TextStyle(fontFamily = BodyFontFamily, fontWeight = FontWeight.Bold, fontSize = 10.sp)
)
