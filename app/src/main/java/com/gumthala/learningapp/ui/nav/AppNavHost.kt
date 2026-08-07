package com.gumthala.learningapp.ui.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.gumthala.learningapp.ui.components.AppTabBar
import com.gumthala.learningapp.ui.components.PrimaryFullButton
import com.gumthala.learningapp.domain.DifficultyLevel
import com.gumthala.learningapp.ui.screens.DifficultyPickerScreen
import com.gumthala.learningapp.ui.screens.HomeScreen
import com.gumthala.learningapp.ui.screens.LessonScreen
import com.gumthala.learningapp.ui.screens.PracticeScreen
import com.gumthala.learningapp.ui.screens.ProfileScreen
import com.gumthala.learningapp.ui.screens.ProgressScreen
import com.gumthala.learningapp.ui.screens.QuizScreen
import com.gumthala.learningapp.ui.screens.SubjectCardUi
import com.gumthala.learningapp.ui.screens.SubjectsScreen
import com.gumthala.learningapp.ui.screens.celebration.QuizCompleteScreen
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.display
import com.gumthala.learningapp.ui.viewmodel.QuizScreenState
import com.gumthala.learningapp.ui.viewmodel.QuizViewModel
import com.gumthala.learningapp.ui.viewmodel.StudentViewModel

/** Pushed screens that sit on top of a tab. */
private sealed interface Overlay {
    data object None : Overlay
    data object Chapters : Overlay
    data object Lesson : Overlay
    data class QuizDifficulty(val chapter: SubjectCardUi) : Overlay
    data class Quiz(val chapterId: String, val difficulty: DifficultyLevel) : Overlay
}

/**
 * Wires the student screens together. Subjects → chapter list → Quiz is now a
 * real loop backed by [StudentViewModel] / [QuizViewModel] over Room — pick a
 * subject, pick a chapter, answer real questions, see the attempt persisted.
 *
 * Lesson stays on demo data: chapters only store a title/blurb, not a teaching
 * body, so there's nothing real for LessonScreen's paragraph/example fields to
 * bind to yet — that needs a content-model addition, not just wiring. Home
 * likewise stays on demo data; see README "Mockup vs spec" for why (XP/Coins/
 * Streak/weekly-bar fields have no backing columns).
 */
@Composable
fun AppNavHost(userId: String, classLevel: Int, modifier: Modifier = Modifier) {
    var tab by remember { mutableStateOf(TopLevelDestination.HOME) }
    var overlay by remember { mutableStateOf<Overlay>(Overlay.None) }

    val studentViewModel: StudentViewModel = hiltViewModel<StudentViewModel, StudentViewModel.Factory>(
        key = "student:$userId:$classLevel"
    ) { factory -> factory.create(userId, classLevel) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(Modifier.weight(1f)) {
            when (val current = overlay) {
                Overlay.Lesson -> LessonScreen(
                    onBack = { overlay = Overlay.Chapters },
                    onNext = { overlay = Overlay.None; tab = TopLevelDestination.PRACTICE }
                )

                Overlay.Chapters -> {
                    val chaptersState by studentViewModel.chaptersState.collectAsState()
                    SubjectsScreen(
                        subjects = chaptersState.chapters,
                        onBack = { overlay = Overlay.None },
                        onSubjectClick = { chapter: SubjectCardUi -> overlay = Overlay.QuizDifficulty(chapter) }
                    )
                }

                is Overlay.QuizDifficulty -> DifficultyPickerScreen(
                    chapterTitle = current.chapter.title,
                    onBack = { overlay = Overlay.Chapters },
                    onPick = { level -> overlay = Overlay.Quiz(current.chapter.id, level) }
                )

                is Overlay.Quiz -> QuizHost(
                    chapterId = current.chapterId,
                    userId = userId,
                    difficulty = current.difficulty,
                    onDone = { overlay = Overlay.None; tab = TopLevelDestination.LEARN }
                )

                Overlay.None -> when (tab) {
                    TopLevelDestination.HOME -> HomeScreen(
                        onSeeAllSubjects = { tab = TopLevelDestination.LEARN },
                        onFeaturedClick = { tab = TopLevelDestination.LEARN },
                        onStartChallenge = { tab = TopLevelDestination.LEARN },
                        onContinueLearning = { tab = TopLevelDestination.LEARN }
                    )

                    TopLevelDestination.LEARN -> {
                        val subjectsState by studentViewModel.subjectsState.collectAsState()
                        SubjectsScreen(
                            subjects = subjectsState.subjects,
                            onBack = { tab = TopLevelDestination.HOME },
                            onSubjectClick = { subject: SubjectCardUi ->
                                studentViewModel.selectSubject(subject.id)
                                overlay = Overlay.Chapters
                            }
                        )
                    }

                    TopLevelDestination.PRACTICE -> PracticeScreen(
                        onBack = { tab = TopLevelDestination.HOME },
                        onModeClick = { tab = TopLevelDestination.LEARN }
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

/**
 * Drives the existing (unmodified) [QuizScreen] with a real [QuizViewModel].
 * Handles all three states the ViewModel can be in: still loading questions,
 * no questions exist for this chapter yet, or answering. On finish, shows an
 * inline result (no Candy Burst animation yet — that's a separate build step)
 * with a real star count and a way back out.
 */
@Composable
private fun QuizHost(chapterId: String, userId: String, difficulty: DifficultyLevel, onDone: () -> Unit) {
    val viewModel: QuizViewModel = hiltViewModel<QuizViewModel, QuizViewModel.Factory>(
        key = "quiz:$chapterId:$userId:${difficulty.name}"
    ) { factory -> factory.create(chapterId, userId, difficulty) }
    val state by viewModel.screenState.collectAsState()

    when (val s = state) {
        QuizScreenState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }

        QuizScreenState.Empty -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "No questions in this chapter yet.",
                    style = display(TextSize.Label, FontWeight.Bold),
                    color = AppColors.Muted
                )
                PrimaryFullButton(
                    text = "Back",
                    onClick = onDone,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        QuizScreenState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Something went wrong with this quiz.",
                    style = display(TextSize.Label, FontWeight.Bold),
                    color = AppColors.Muted
                )
                PrimaryFullButton(
                    text = "Back",
                    onClick = onDone,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        is QuizScreenState.InProgress -> QuizScreen(
            state = s.ui,
            onSelectOption = viewModel::selectOption,
            onNext = viewModel::next
        )

        is QuizScreenState.Finished -> QuizCompleteScreen(
            mascotSeed = chapterId,
            correctCount = s.outcome.correctCount,
            totalCount = s.outcome.totalCount,
            starsEarned = s.outcome.starsEarned,
            celebration = s.outcome.celebration,
            newBadges = s.outcome.newBadges,
            onContinue = onDone
        )
    }
}
