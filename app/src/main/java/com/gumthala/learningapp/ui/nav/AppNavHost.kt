package com.gumthala.learningapp.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gumthala.learningapp.ui.components.AppTabBar
import com.gumthala.learningapp.ui.screens.HomeScreen
import com.gumthala.learningapp.ui.screens.LessonScreen
import com.gumthala.learningapp.ui.screens.PracticeScreen
import com.gumthala.learningapp.ui.screens.ProfileScreen
import com.gumthala.learningapp.ui.screens.ProgressScreen
import com.gumthala.learningapp.ui.screens.QuizScreen
import com.gumthala.learningapp.ui.screens.SubjectsScreen

/** Pushed screens that sit on top of a tab. */
private sealed interface Overlay {
    data object None : Overlay
    data object Lesson : Overlay
    data object Quiz : Overlay
}

/**
 * Wires the seven mockup screens together. The mockup shows Lesson under the
 * "Learn" tab and Quiz under "Practice", so they are modelled as pushed screens
 * over those tabs rather than tabs of their own.
 */
@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    var tab by remember { mutableStateOf(TopLevelDestination.HOME) }
    var overlay by remember { mutableStateOf<Overlay>(Overlay.None) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(Modifier.weight(1f)) {
            when (overlay) {
                Overlay.Lesson -> LessonScreen(
                    onBack = { overlay = Overlay.None },
                    onNext = { tab = TopLevelDestination.PRACTICE; overlay = Overlay.Quiz }
                )

                Overlay.Quiz -> QuizScreen()

                Overlay.None -> when (tab) {
                    TopLevelDestination.HOME -> HomeScreen(
                        onSeeAllSubjects = { tab = TopLevelDestination.LEARN },
                        onFeaturedClick = { tab = TopLevelDestination.LEARN },
                        onStartChallenge = { tab = TopLevelDestination.PRACTICE; overlay = Overlay.Quiz },
                        onContinueLearning = { tab = TopLevelDestination.LEARN; overlay = Overlay.Lesson }
                    )

                    TopLevelDestination.LEARN -> SubjectsScreen(
                        onBack = { tab = TopLevelDestination.HOME },
                        onSubjectClick = { overlay = Overlay.Lesson }
                    )

                    TopLevelDestination.PRACTICE -> PracticeScreen(
                        onBack = { tab = TopLevelDestination.HOME },
                        onModeClick = { overlay = Overlay.Quiz }
                    )

                    TopLevelDestination.PROGRESS -> ProgressScreen()

                    TopLevelDestination.PROFILE -> ProfileScreen()
                }
            }
        }

        AppTabBar(
            selected = tab,
            onSelect = { destination ->
                tab = destination
                overlay = Overlay.None
            }
        )
    }
}
