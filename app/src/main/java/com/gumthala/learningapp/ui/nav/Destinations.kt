package com.gumthala.learningapp.ui.nav

/** The five tab-bar destinations from the mockup, in its order. */
enum class TopLevelDestination(
    val route: String,
    val emoji: String,
    val label: String
) {
    HOME("home", "🏠", "Home"),
    LEARN("learn", "🎓", "Learn"),
    PRACTICE("practice", "🎯", "Practice"),
    PROGRESS("progress", "📊", "Progress"),
    PROFILE("profile", "👤", "Profile")
}

object Routes {
    const val LESSON = "lesson/{chapterId}"
    const val QUIZ = "quiz/{chapterId}"
    fun lesson(chapterId: String) = "lesson/$chapterId"
    fun quiz(chapterId: String) = "quiz/$chapterId"
}
