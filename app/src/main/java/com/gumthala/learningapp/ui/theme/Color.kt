package com.gumthala.learningapp.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Lifted verbatim from the mockup's `:root` custom properties. Do not invent new
 * colours here — if a screen needs one, it should come from the mockup.
 */
object AppColors {
    val Purple = Color(0xFF5B4FE8)
    val PurpleDark = Color(0xFF4A3FD0)
    val PurpleLight = Color(0xFF7C6EF0)   // gradient partner in quiz/progress headers
    val Blue = Color(0xFF3B82F6)
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
    val Bg = Color(0xFFF5F4FC)

    /** #F7F6FD — the soft card fill used by stat chips, cells and menu rows. */
    val SurfaceSoft = Color(0xFFF7F6FD)
    /** #EFEDFA — the 1px line above the tab bar. */
    val Divider = Color(0xFFEFEDFA)
    val TrackIdle = Color(0xFFE4E1FA)
    val PrevButtonBg = Color(0xFFF0EEFC)

    val LogoutBg = Color(0xFFFFE3E3)
    val LogoutFg = Color(0xFFDC2626)

    val OnYellowButton = Color(0xFF3A2600)
    val ChallengeTitle = Color(0xFF7A4D00)
    val ChallengeSub = Color(0xFF9A6A00)
}

/**
 * The six subject card palettes (.sc-blue … .sc-orange). Each is background /
 * icon-tile / foreground, exactly as in the mockup stylesheet.
 */
data class SubjectPalette(
    val background: Color,
    val iconTile: Color,
    val foreground: Color
) {
    companion object {
        val Blue = SubjectPalette(Color(0xFFEAF1FF), Color(0xFFCFE0FF), Color(0xFF2563EB))
        val Pink = SubjectPalette(AppColors.PinkBg, Color(0xFFFFC2D6), Color(0xFFD6316B))
        val Green = SubjectPalette(AppColors.GreenBg, Color(0xFFA7F3C4), Color(0xFF15803D))
        val Yellow = SubjectPalette(AppColors.YellowBg, Color(0xFFFDE49A), Color(0xFF946200))
        val Violet = SubjectPalette(AppColors.VioletBg, Color(0xFFD6C2FF), Color(0xFF6D28D9))
        val Orange = SubjectPalette(AppColors.OrangeBg, Color(0xFFFFC79E), Color(0xFFC2410C))

        val all = listOf(Blue, Pink, Green, Yellow, Violet, Orange)
    }
}

/** The three "Featured Subjects" tiles on Home (.feat.f1 / .f2 / .f3). */
object FeaturedPalette {
    val One = SubjectPalette(Color(0xFFEAF1FF), Color(0xFFDCE9FF), Color(0xFF2563EB))
    val Two = SubjectPalette(AppColors.PinkBg, Color(0xFFFFD3E2), Color(0xFFD6316B))
    val Three = SubjectPalette(AppColors.GreenBg, Color(0xFFBBF7D0), Color(0xFF15803D))
    val Four = SubjectPalette(AppColors.YellowBg, Color(0xFFFDE68A), AppColors.OnYellowButton)
}

/** Weekly Report bar tints, in the mockup's order. */
val WeeklyBarColors = listOf(
    Color(0xFFCFE0FF),
    Color(0xFFFDE49A),
    Color(0xFFA7F3C4),
    Color(0xFFD6C2FF),
    Color(0xFFFFC2D6),
    Color(0xFFCFE0FF),
    Color(0xFFFFC2D6)
)

/** Additional tokens from the login/admin/teacher/slides/leaderboard mockup. */
object AppColors2 {
    val OnGreenBox = Color(0xFF15803D)
    val OnPinkBox = Color(0xFFD6316B)
    val OnYellowBox = Color(0xFF946200)
    val OnVioletBox = Color(0xFF6D28D9)

    val PodiumGoldStart = Color(0xFFFFD873)
    val PodiumSilverStart = Color(0xFFC9CEDA)
    val PodiumSilverEnd = Color(0xFF9CA3B5)
    val PodiumBronzeStart = Color(0xFFE3A87C)
    val PodiumBronzeEnd = Color(0xFFC88752)

    val AvatarGradStart = Color(0xFFFFB56B)
    val AvatarGradEnd = Color(0xFFFF8A5C)
}
