package com.gumthala.learningapp.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.gumthala.learningapp.R

/** Display face used for headings, stat values, buttons and badges throughout the mockup. */
val DisplayFontFamily = FontFamily(
    Font(R.font.baloo2_variable, weight = FontWeight.Medium),
    Font(R.font.baloo2_variable, weight = FontWeight.SemiBold),
    Font(R.font.baloo2_variable, weight = FontWeight.Bold),
    Font(R.font.baloo2_variable, weight = FontWeight.ExtraBold)
)

/** Body face used for paragraphs, meta text and the status/tab bars. */
val BodyFontFamily = FontFamily(
    Font(R.font.nunito_variable, weight = FontWeight.Medium),
    Font(R.font.nunito_variable, weight = FontWeight.SemiBold),
    Font(R.font.nunito_variable, weight = FontWeight.Bold),
    Font(R.font.nunito_variable, weight = FontWeight.ExtraBold)
)
