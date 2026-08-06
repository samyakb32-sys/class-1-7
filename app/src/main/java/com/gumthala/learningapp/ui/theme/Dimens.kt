package com.gumthala.learningapp.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * The mockup was drawn inside a 290px-wide phone frame. A real handset is around
 * 360–412dp of usable width, so every px value is scaled by 1.25 to keep the same
 * visual proportions while staying legible (an 8px label would be an unreadable
 * 8sp on a device).
 *
 * Proportions, ratios and relative spacing are unchanged — only the base unit is.
 * Set [MockupScale] to 1.0 to render at literal mockup sizes.
 */
const val MockupScale = 1.25f

/** Horizontal padding of the mockup's `.content` (14px). */
val ScreenPadding = 18.dp

object Radius {
    val Chip = 10.dp        // 8px
    val Cell = 13.dp        // 10px
    val Card = 15.dp        // 12px
    val Soft = 18.dp        // 14px
    val Large = 20.dp       // 16px
    val Banner = 22.dp      // 18px
    val Pill = 25.dp        // 20px
    val Sheet = 28.dp       // 22px — profile header bottom corners
}

object TextSize {
    val Micro = 10.sp       // 8px  — stat chip labels
    val Tiny = 11.sp        // 9px  — meta rows, sub-labels
    val Small = 12.sp       // 10px — "See All", buttons
    val Body = 14.sp        // 11–11.5px — paragraphs, menu rows
    val Label = 15.sp       // 12px — section headings, feedback
    val Title = 16.sp       // 13px — card titles, quiz question
    val Header = 17.sp      // 14px — lesson header
    val Screen = 19.sp      // 15–16px — quiz header, greeting name
    val Display = 21.sp     // 17px — screen titles, lesson heading
}
