package com.gumthala.learningapp.ui.student

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gumthala.learningapp.di.LocalAppContainer
import com.gumthala.learningapp.ui.common.HelpSupportScreen
import com.gumthala.learningapp.ui.student.home.HomeScreen
import com.gumthala.learningapp.ui.student.home.HomeViewModel
import com.gumthala.learningapp.ui.student.leaderboard.LeaderboardScreen
import com.gumthala.learningapp.ui.student.lesson.LessonScreen
import com.gumthala.learningapp.ui.student.lesson.LessonViewModel
import com.gumthala.learningapp.ui.student.practice.PracticeScreen
import com.gumthala.learningapp.ui.student.profile.AchievementsScreen
import com.gumthala.learningapp.ui.student.profile.ProfileScreen
import com.gumthala.learningapp.ui.student.profile.ProfileViewModel
import com.gumthala.learningapp.ui.student.progress.ProgressScreen
import com.gumthala.learningapp.ui.student.progress.ProgressViewModel
import com.gumthala.learningapp.ui.student.quiz.QuizScreen
import com.gumthala.learningapp.ui.student.quiz.QuizViewModel
import com.gumthala.learningapp.ui.student.subjects.SubjectsScreen
import com.gumthala.learningapp.ui.student.subjects.SubjectsViewModel
import com.gumthala.learningapp.ui.rememberViewModel
import com.gumthala.learningapp.ui.components.BottomTabBar
import com.gumthala.learningapp.ui.components.StudentTab
import kotlinx.coroutines.launch

private object Routes {
    const val HOME = "home"
    const val SUBJECTS = "subjects"
    const val LESSON = "lesson/{chapterId}"
    const val PRACTICE = "practice"
    const val QUIZ = "quiz/{chapterId}"
    const val PROGRESS = "progress"
    const val PROFILE = "profile"
    const val ACHIEVEMENTS = "achievements"
    const val HELP = "help"
    const val SETTINGS = "settings"
    const val LEADERBOARD = "leaderboard"

    fun lesson(chapterId: String) = "lesson/$chapterId"
    fun quiz(chapterId: String) = "quiz/$chapterId"
}

@Composable
fun StudentNavGraph(session: StudentSession, onLogout: () -> Unit) {
    val container = LocalAppContainer.current
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val currentTab = when {
        currentRoute == Routes.HOME -> StudentTab.HOME
        currentRoute == Routes.SUBJECTS || currentRoute == Routes.LESSON -> StudentTab.LEARN
        currentRoute == Routes.PRACTICE || currentRoute == Routes.QUIZ -> StudentTab.PRACTICE
        currentRoute == Routes.PROGRESS -> StudentTab.PROGRESS
        currentRoute == Routes.PROFILE -> StudentTab.PROFILE
        else -> null
    }
    val showBottomBar = currentTab != null

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomTabBar(
                    selected = currentTab ?: StudentTab.HOME,
                    showLabels = currentTab == StudentTab.HOME,
                    onSelect = { tab ->
                        val route = when (tab) {
                            StudentTab.HOME -> Routes.HOME
                            StudentTab.LEARN -> Routes.SUBJECTS
                            StudentTab.PRACTICE -> Routes.PRACTICE
                            StudentTab.PROGRESS -> Routes.PROGRESS
                            StudentTab.PROFILE -> Routes.PROFILE
                        }
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())) {
            NavHost(navController = navController, startDestination = Routes.HOME) {
                composable(Routes.HOME) {
                    val vm = rememberViewModel { HomeViewModel(session, container.contentRepository, container.progressRepository) }
                    HomeScreen(
                        vm,
                        session.language,
                        onOpenSubjects = { navController.navigate(Routes.SUBJECTS) },
                        onOpenLeaderboard = { navController.navigate(Routes.LEADERBOARD) }
                    )
                }
                composable(Routes.SUBJECTS) {
                    val vm = rememberViewModel { SubjectsViewModel(session, container.contentRepository, container.progressRepository) }
                    SubjectsScreen(
                        viewModel = vm,
                        language = session.language,
                        onBack = { navController.popBackStack() },
                        onOpenSubject = { _, chapterId -> chapterId?.let { navController.navigate(Routes.lesson(it)) } }
                    )
                }
                composable(
                    Routes.LESSON,
                    arguments = listOf(navArgument("chapterId") {})
                ) { entry ->
                    val chapterId = entry.arguments?.getString("chapterId").orEmpty()
                    val vm = rememberViewModel { LessonViewModel(chapterId, container.contentRepository) }
                    LessonScreen(
                        viewModel = vm,
                        language = session.language,
                        onBack = { navController.popBackStack() },
                        onNavigateToChapter = { id -> navController.navigate(Routes.lesson(id)) { popUpTo(Routes.SUBJECTS) } },
                        onPracticeNow = { id -> navController.navigate(Routes.quiz(id)) }
                    )
                }
                composable(Routes.PRACTICE) {
                    PracticeScreen(
                        onBack = { navController.popBackStack() },
                        onPickMultipleChoice = { navController.navigate(Routes.SUBJECTS) }
                    )
                }
                composable(
                    Routes.QUIZ,
                    arguments = listOf(navArgument("chapterId") {})
                ) { entry ->
                    val chapterId = entry.arguments?.getString("chapterId").orEmpty()
                    val vm = rememberViewModel {
                        QuizViewModel(chapterId, session, container.contentRepository, container.quizRepository)
                    }
                    QuizScreen(
                        viewModel = vm,
                        language = session.language,
                        onDone = { navController.popBackStack(Routes.SUBJECTS, inclusive = false) }
                    )
                }
                composable(Routes.PROGRESS) {
                    val vm = rememberViewModel { ProgressViewModel(session, container.contentRepository, container.progressRepository) }
                    ProgressScreen(vm)
                }
                composable(Routes.PROFILE) {
                    val vm = rememberViewModel { ProfileViewModel(session, container.progressRepository) }
                    ProfileScreen(
                        viewModel = vm,
                        onOpenAchievements = { navController.navigate(Routes.ACHIEVEMENTS) },
                        onOpenHelp = { navController.navigate(Routes.HELP) },
                        onOpenSettings = { navController.navigate(Routes.SETTINGS) },
                        onLogout = onLogout
                    )
                }
                composable(Routes.ACHIEVEMENTS) {
                    val vm = rememberViewModel { ProfileViewModel(session, container.progressRepository) }
                    AchievementsScreen(vm, onBack = { navController.popBackStack() })
                }
                composable(Routes.HELP) {
                    HelpSupportScreen(onBack = { navController.popBackStack() })
                }
                composable(Routes.SETTINGS) {
                    com.gumthala.learningapp.ui.common.LanguageSettingsScreen(container.sessionManager, onBack = { navController.popBackStack() })
                }
                composable(Routes.LEADERBOARD) {
                    LeaderboardScreen(session, onBack = { navController.popBackStack() })
                }
            }
        }
    }
}
