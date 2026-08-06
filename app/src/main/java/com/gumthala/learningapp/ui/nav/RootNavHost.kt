package com.gumthala.learningapp.ui.nav

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.gumthala.learningapp.ui.components.RoleTabBar
import com.gumthala.learningapp.ui.components.TeacherTabs
import com.gumthala.learningapp.ui.components.AdminTabs
import com.gumthala.learningapp.ui.screens.ProfileScreen
import com.gumthala.learningapp.ui.screens.ProfileUiState
import com.gumthala.learningapp.ui.screens.admin.AdminDashboardScreen
import com.gumthala.learningapp.ui.screens.admin.AdminOverview
import com.gumthala.learningapp.ui.screens.admin.ManageStudentsScreen
import com.gumthala.learningapp.ui.screens.admin.ManagedStudentRow
import com.gumthala.learningapp.ui.screens.auth.AppRole
import com.gumthala.learningapp.ui.screens.auth.RoleSelectScreen
import com.gumthala.learningapp.ui.screens.auth.StaffLoginInput
import com.gumthala.learningapp.ui.screens.auth.StaffLoginScreen
import com.gumthala.learningapp.ui.screens.auth.StudentLoginInput
import com.gumthala.learningapp.ui.screens.auth.StudentLoginScreen
import com.gumthala.learningapp.ui.screens.teacher.TeacherDashboardScreen
import com.gumthala.learningapp.ui.screens.teacher.TeacherOverview
import com.gumthala.learningapp.ui.screens.teacher.TeachingSlide
import com.gumthala.learningapp.ui.screens.teacher.TeachingSlidesScreen
import com.gumthala.learningapp.ui.viewmodel.AuthViewModel
import com.gumthala.learningapp.ui.viewmodel.SignedInRole

/** Where the app is in the login flow, before any role-specific shell takes over. */
private sealed interface AuthStep {
    data object RoleSelect : AuthStep
    data object StudentLogin : AuthStep
    data class StaffLogin(val role: AppRole) : AuthStep
}

/**
 * Entry point: Role Select → the matching login → the matching shell.
 * [AuthViewModel] backs both login screens with the real
 * [com.gumthala.learningapp.data.repo.AuthRepository] and restores a persisted
 * session on launch, so a returning user skips straight to their shell —
 * per spec, sign-in only clears on an explicit Logout.
 */
@Composable
fun RootNavHost(modifier: Modifier = Modifier) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.state.collectAsState()
    var authStep by remember { mutableStateOf<AuthStep>(AuthStep.RoleSelect) }

    Column(modifier = modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding()) {
        when {
            authState.isLoadingSession -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            authState.signedInAs == null -> when (val step = authStep) {
                AuthStep.RoleSelect -> RoleSelectScreen(
                    onRoleSelected = { role ->
                        authStep = if (role == AppRole.STUDENT) AuthStep.StudentLogin else AuthStep.StaffLogin(role)
                    }
                )

                AuthStep.StudentLogin -> StudentLoginScreen(
                    onSubmit = { input: StudentLoginInput ->
                        authViewModel.signInStudent(input.name, input.classLevel)
                    },
                    submitError = authState.errorMessage,
                    isSubmitting = authState.isSubmitting
                )

                is AuthStep.StaffLogin -> StaffLoginScreen(
                    role = step.role,
                    onSubmit = { input: StaffLoginInput ->
                        authViewModel.signInStaff(input.email, input.password)
                    },
                    submitError = authState.errorMessage,
                    isSubmitting = authState.isSubmitting
                )
            }

            authState.signedInAs == SignedInRole.STUDENT -> AppNavHost()

            authState.signedInAs == SignedInRole.TEACHER -> TeacherShell(
                displayName = authState.signedInName.orEmpty(),
                onLogout = { authViewModel.logout(); authStep = AuthStep.RoleSelect }
            )

            authState.signedInAs == SignedInRole.ADMIN -> AdminShell(
                displayName = authState.signedInName.orEmpty(),
                onLogout = { authViewModel.logout(); authStep = AuthStep.RoleSelect }
            )
        }
    }
}

private sealed interface TeacherOverlay {
    data object None : TeacherOverlay
    data object Slides : TeacherOverlay
}

@Composable
private fun TeacherShell(displayName: String, onLogout: () -> Unit) {
    var tab by remember { mutableStateOf(TeacherTabs.HOME) }
    var overlay by remember { mutableStateOf<TeacherOverlay>(TeacherOverlay.None) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f)) {
            when (overlay) {
                TeacherOverlay.Slides -> TeachingSlidesScreen(
                    deckTitle = "Alphabet A–Z",
                    slides = DemoData.alphabetSlides,
                    currentIndex = 12,
                    onBack = { overlay = TeacherOverlay.None },
                    onPrevious = {},
                    onNext = {},
                    onAddSlide = { /* teacher-authored slide — not wired */ }
                )

                TeacherOverlay.None -> when (tab) {
                    TeacherTabs.HOME -> TeacherDashboardScreen(
                        overview = DemoData.teacherOverview.copy(
                            teacherName = displayName.ifBlank { DemoData.teacherOverview.teacherName }
                        ),
                        onRegisterStudent = { },
                        onAddEditQuestions = { },
                        onTeachingSlides = { overlay = TeacherOverlay.Slides },
                        onStudentProgress = { tab = TeacherTabs.STUDENTS }
                    )

                    TeacherTabs.STUDENTS -> ManageStudentsScreen(
                        students = DemoData.teacherStudents,
                        onBack = { tab = TeacherTabs.HOME },
                        onSearch = { },
                        onStudentClick = { },
                        onAddStudent = { }
                    )

                    TeacherTabs.SLIDES -> TeachingSlidesScreen(
                        deckTitle = "Alphabet A–Z",
                        slides = DemoData.alphabetSlides,
                        currentIndex = 12,
                        onBack = { tab = TeacherTabs.HOME },
                        onPrevious = {},
                        onNext = {},
                        onAddSlide = { }
                    )

                    else -> ProfileScreen(
                        state = DemoData.teacherProfile.copy(
                            name = displayName.ifBlank { DemoData.teacherProfile.name }
                        ),
                        onLogout = onLogout
                    )
                }
            }
        }
        if (overlay == TeacherOverlay.None) {
            RoleTabBar(items = TeacherTabs.items, selectedIndex = tab, onSelect = { tab = it })
        }
    }
}

@Composable
private fun AdminShell(displayName: String, onLogout: () -> Unit) {
    var tab by remember { mutableStateOf(AdminTabs.HOME) }

    Column(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f)) {
            when (tab) {
                AdminTabs.HOME -> AdminDashboardScreen(
                    overview = DemoData.adminOverview.copy(
                        adminName = displayName.ifBlank { DemoData.adminOverview.adminName }
                    ),
                    onManageStudents = { tab = AdminTabs.PEOPLE },
                    onManageTeachers = { tab = AdminTabs.PEOPLE },
                    onManageContent = { tab = AdminTabs.CONTENT },
                    onHelpAndSupport = { }
                )

                AdminTabs.PEOPLE -> ManageStudentsScreen(
                    students = DemoData.adminStudents,
                    onBack = { tab = AdminTabs.HOME },
                    onSearch = { },
                    onStudentClick = { },
                    onAddStudent = { }
                )

                AdminTabs.CONTENT -> AdminDashboardScreen(
                    overview = DemoData.adminOverview.copy(
                        adminName = displayName.ifBlank { DemoData.adminOverview.adminName }
                    ),
                    onManageStudents = { tab = AdminTabs.PEOPLE },
                    onManageTeachers = { tab = AdminTabs.PEOPLE },
                    onManageContent = { },
                    onHelpAndSupport = { }
                )

                else -> ProfileScreen(
                    state = DemoData.adminProfile.copy(
                        name = displayName.ifBlank { DemoData.adminProfile.name }
                    ),
                    onLogout = onLogout
                )
            }
        }
        RoleTabBar(items = AdminTabs.items, selectedIndex = tab, onSelect = { tab = it })
    }
}

/**
 * Placeholder content standing in for repository reads. Every one of these
 * numbers should come from AuthRepository / QuizRepository / SlideRepository
 * once the screens are wired — see README "What still needs doing".
 */
private object DemoData {
    val teacherOverview = TeacherOverview("Mrs. Patil", myStudentCount = 32, avgProgressPct = 81)
    val adminOverview = AdminOverview("Principal", studentCount = 184, teacherCount = 12, avgProgressPct = 78, questionCount = 340)

    val teacherProfile = ProfileUiState(avatarEmoji = "👩‍🏫", name = "Mrs. Patil", subtitle = "Teacher · Classes 4–6")
    val adminProfile = ProfileUiState(avatarEmoji = "🛡️", name = "Principal", subtitle = "Admin")

    val teacherStudents = listOf(
        ManagedStudentRow("s1", "👧", "Aarohi Sharma", "Class 5 · Mrs. Patil", 85),
        ManagedStudentRow("s2", "👧", "Isha Kulkarni", "Class 6 · Mrs. Patil", 91),
        ManagedStudentRow("s3", "👧", "Sanika Jadhav", "Class 5 · Mrs. Patil", 88)
    )
    val adminStudents = teacherStudents + listOf(
        ManagedStudentRow("s4", "👦", "Rohan Deshmukh", "Class 4 · Mr. Kale", 72),
        ManagedStudentRow("s5", "👦", "Vedant More", "Class 3 · Mr. Kale", 64)
    )

    val alphabetSlides = ('A'..'Z').map { letter -> TeachingSlide(letter.toString(), "$letter for …") }
}
