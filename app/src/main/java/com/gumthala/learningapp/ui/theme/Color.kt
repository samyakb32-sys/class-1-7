package com.gumthala.learningapp.ui.theme

import androidx.compose.ui.graphics.Color

// Design tokens lifted 1:1 from the mockup's :root CSS custom properties.
val Purple = Color(0xFF5B4FE8)
val PurpleDark = Color(0xFF4A3FD0)
val PurpleLight = Color(0xFF7C6EF0) // second gradient stop on Quiz/Progress headers
val Pink = Color(0xFFFF5C8A)
val PinkBg = Color(0xFFFFE3EC)
val Green = Color(0xFF22C55E)
val GreenBg = Color(0xFFDCFCE7)
val Yellow = Color(0xFFF5A623)
val YellowBg = Color(0xFFFEF3D6)
val Violet = Color(0xFF8B5CF6)
val VioletBg = Color(0xFFEDE4FF)
val Orange = Color(0xFFFB923C)
val OrangeBg = Color(0xFFFFE8D6)
val Ink = Color(0xFF1E1B3A)
val Muted = Color(0xFF8B87A8)
val ScaffoldBg = Color(0xFFF5F4FC)

// Neutral surfaces reused across nearly every screen.
val SurfaceLavender = Color(0xFFF7F6FD)
val SurfacePressed = Color(0xFFF0EEFC)
val HairlineBorder = Color(0xFFEFEDFA)

// One-off spot colors.
val AmberChallengeTitle = Color(0xFF7A4D00)
val AmberChallengeSubtitle = Color(0xFF9A6A00)
val AmberExampleLabel = Color(0xFF946200)
val CorrectGreenText = Color(0xFF15803D)
val LogoutRed = Color(0xFFDC2626)
val LogoutRedBg = Color(0xFFFFE3E3)

/** One of the six "subject color family" tokens (sc-blue / sc-pink / sc-green / sc-yellow / sc-violet / sc-orange). */
enum class SubjectColorFamily(val cardBg: Color, val chipBg: Color, val ink: Color, val badgeBg: Color) {
    BLUE(cardBg = Color(0xFFEAF1FF), chipBg = Color(0xFFCFE0FF), ink = Color(0xFF2563EB), badgeBg = Color(0xFFDCE9FF)),
    PINK(cardBg = PinkBg, chipBg = Color(0xFFFFC2D6), ink = Color(0xFFD6316B), badgeBg = Color(0xFFFFC2D6)),
    GREEN(cardBg = GreenBg, chipBg = Color(0xFFA7F3C4), ink = CorrectGreenText, badgeBg = Color(0xFFA7F3C4)),
    YELLOW(cardBg = YellowBg, chipBg = Color(0xFFFDE49A), ink = AmberExampleLabel, badgeBg = Color(0xFFFDE49A)),
    VIOLET(cardBg = VioletBg, chipBg = Color(0xFFD6C2FF), ink = Color(0xFF6D28D9), badgeBg = Color(0xFFD6C2FF)),
    ORANGE(cardBg = OrangeBg, chipBg = Color(0xFFFFC79E), ink = Color(0xFFC2410C), badgeBg = Color(0xFFFFC79E));

    companion object {
        fun fromKey(key: String): SubjectColorFamily = when (key) {
            "blue" -> BLUE
            "pink" -> PINK
            "green" -> GREEN
            "yellow" -> YELLOW
            "violet" -> VIOLET
            "orange" -> ORANGE
            else -> BLUE
        }
    }
}
