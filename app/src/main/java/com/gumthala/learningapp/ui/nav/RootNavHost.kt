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
import com.gumthala.learningapp.ui.screens.SubjectCardUi
import androidx.hilt.navigation.compose.hiltViewModel
import com.gumthala.learningapp.ui.components.RoleTabBar
import com.gumthala.learningapp.ui.components.TeacherTabs
import com.gumthala.learningapp.ui.components.AdminTabs
import com.gumthala.learningapp.ui.screens.ProfileScreen
import com.gumthala.learningapp.ui.screens.ProfileUiState
import com.gumthala.learningapp.ui.screens.admin.AdminDashboardScreen
import com.gumthala.learningapp.ui.screens.admin.ManageStudentsScreen
import com.gumthala.learningapp.ui.screens.auth.AppRole
import com.gumthala.learningapp.ui.screens.auth.ChangePasswordScreen
import com.gumthala.learningapp.ui.screens.auth.RoleSelectScreen
import com.gumthala.learningapp.ui.screens.auth.StaffLoginInput
import com.gumthala.learningapp.ui.screens.auth.StaffLoginScreen
import com.gumthala.learningapp.ui.screens.auth.StudentLoginInput
import com.gumthala.learningapp.ui.screens.auth.StudentLoginScreen
import com.gumthala.learningapp.ui.screens.roster.PickClassScreen
import com.gumthala.learningapp.ui.screens.roster.QuestionEditorScreen
import com.gumthala.learningapp.ui.screens.roster.RegisterStudentScreen
import com.gumthala.learningapp.ui.screens.roster.RegisterTeacherScreen
import com.gumthala.learningapp.ui.screens.SubjectsScreen
import com.gumthala.learningapp.ui.screens.teacher.TeacherDashboardScreen
import com.gumthala.learningapp.ui.screens.teacher.TeachingSlidesScreen
import com.gumthala.learningapp.ui.viewmodel.AdminViewModel
import com.gumthala.learningapp.ui.viewmodel.AuthViewModel
import com.gumthala.learningapp.ui.viewmodel.QuestionEditorViewModel
import com.gumthala.learningapp.ui.viewmodel.SignedInRole
import com.gumthala.learningapp.ui.viewmodel.SlideViewModel
import com.gumthala.learningapp.ui.viewmodel.TeacherViewModel

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

            authState.signedInAs == SignedInRole.STUDENT -> AppNavHost(
                userId = authState.signedInUserId.orEmpty(),
                classLevel = authState.signedInClassLevel ?: 1
            )

            // Staff with a still-default password (seeded founder admin, or anyone an
            // admin just reset) must set their own password before reaching anything
            // else — checked before role, since it applies to both TEACHER and ADMIN.
            authState.mustChangePassword && (authState.signedInAs == SignedInRole.TEACHER || authState.signedInAs == SignedInRole.ADMIN) ->
                ChangePasswordGate(authViewModel = authViewModel)

            authState.signedInAs == SignedInRole.TEACHER -> TeacherShell(
                teacherUserId = authState.signedInUserId.orEmpty(),
                displayName = authState.signedInName.orEmpty(),
                onLogout = { authViewModel.logout(); authStep = AuthStep.RoleSelect }
            )

            authState.signedInAs == SignedInRole.ADMIN -> AdminShell(
                adminUserId = authState.signedInUserId.orEmpty(),
                displayName = authState.signedInName.orEmpty(),
                onLogout = { authViewModel.logout(); authStep = AuthStep.RoleSelect }
            )
        }
    }
}

@Composable
private fun ChangePasswordGate(authViewModel: AuthViewModel) {
    var isSubmitting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    ChangePasswordScreen(
        onSubmit = { newPassword ->
            isSubmitting = true
            error = null
            authViewModel.changePassword(newPassword) { success ->
                isSubmitting = false
                if (success) {
                    authViewModel.passwordChanged()
                } else {
                    error = "Couldn't save your password. Try again."
                }
            }
        },
        submitError = error,
        isSubmitting = isSubmitting
    )
}

private sealed interface TeacherOverlay {
    data object None : TeacherOverlay
    data object SlideDecks : TeacherOverlay
    data object SlideViewer : TeacherOverlay
    data object RegisterStudent : TeacherOverlay
    data object QuestionSubjects : TeacherOverlay
    data class QuestionClass(val subject: SubjectCardUi) : TeacherOverlay
    data class QuestionChapters(val subject: SubjectCardUi, val classLevel: Int) : TeacherOverlay
    data class QuestionForm(val chapter: SubjectCardUi) : TeacherOverlay
}

@Composable
private fun TeacherShell(teacherUserId: String, displayName: String, onLogout: () -> Unit) {
    var tab by remember { mutableStateOf(TeacherTabs.HOME) }
    var overlay by remember { mutableStateOf<TeacherOverlay>(TeacherOverlay.None) }

    val viewModel: TeacherViewModel = hiltViewModel<TeacherViewModel, TeacherViewModel.Factory>(
        key = "teacher:$teacherUserId"
    ) { factory -> factory.create(teacherUserId) }
    val slideViewModel: SlideViewModel = hiltViewModel<SlideViewModel, SlideViewModel.Factory>(
        key = "slides:$teacherUserId"
    ) { factory -> factory.create(teacherUserId) }

    val questionViewModel: QuestionEditorViewModel = hiltViewModel<QuestionEditorViewModel, QuestionEditorViewModel.Factory>(
        key = "questions:$teacherUserId"
    ) { factory -> factory.create(teacherUserId) }

    val overview by viewModel.overview.collectAsState()
    val students by viewModel.students.collectAsState()
    val assignedClasses by viewModel.assignedClasses.collectAsState()
    val registerState by viewModel.registerState.collectAsState()
    val deckList by slideViewModel.deckListState.collectAsState()
    val viewerState by slideViewModel.viewerState.collectAsState()
    val questionSubjects by questionViewModel.subjects.collectAsState()
    val questionChapters by questionViewModel.chapters.collectAsState()
    val saveQuestionState by questionViewModel.saveState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f)) {
            when (overlay) {
                TeacherOverlay.SlideDecks -> SubjectsScreen(
                    subjects = deckList.decks,
                    onBack = { overlay = TeacherOverlay.None },
                    onSubjectClick = { deck ->
                        slideViewModel.openDeck(deck.id)
                        overlay = TeacherOverlay.SlideViewer
                    }
                )

                TeacherOverlay.SlideViewer -> TeachingSlidesScreen(
                    deckTitle = viewerState.title,
                    slides = viewerState.slides,
                    currentIndex = viewerState.currentIndex,
                    onBack = { overlay = TeacherOverlay.SlideDecks },
                    onPrevious = slideViewModel::previous,
                    onNext = slideViewModel::next,
                    onAddSlide = { /* teacher-authored slide — SlideRepository.saveCustomDeck is ready, no form yet */ }
                )

                TeacherOverlay.RegisterStudent -> RegisterStudentScreen(
                    allowedClassLevels = assignedClasses.ifEmpty { listOf(1) },
                    onSubmit = { input -> viewModel.registerStudent(input) },
                    submitError = registerState.error,
                    successMessage = registerState.success,
                    isSubmitting = registerState.isSubmitting
                )

                TeacherOverlay.QuestionSubjects -> SubjectsScreen(
                    subjects = questionSubjects,
                    onBack = { overlay = TeacherOverlay.None },
                    onSubjectClick = { subject -> overlay = TeacherOverlay.QuestionClass(subject) }
                )

                is TeacherOverlay.QuestionClass -> {
                    val step = overlay as TeacherOverlay.QuestionClass
                    PickClassScreen(
                        title = step.subject.title,
                        allowedClassLevels = assignedClasses.ifEmpty { listOf(1) },
                        onBack = { overlay = TeacherOverlay.QuestionSubjects },
                        onPick = { level ->
                            questionViewModel.selectSubjectAndClass(step.subject.id, level)
                            overlay = TeacherOverlay.QuestionChapters(step.subject, level)
                        }
                    )
                }

                is TeacherOverlay.QuestionChapters -> {
                    val step = overlay as TeacherOverlay.QuestionChapters
                    SubjectsScreen(
                        subjects = questionChapters,
                        onBack = { overlay = TeacherOverlay.QuestionClass(step.subject) },
                        onSubjectClick = { chapter ->
                            questionViewModel.consumeSaveResult()
                            overlay = TeacherOverlay.QuestionForm(chapter)
                        }
                    )
                }

                is TeacherOverlay.QuestionForm -> {
                    val step = overlay as TeacherOverlay.QuestionForm
                    QuestionEditorScreen(
                        chapterTitle = step.chapter.title,
                        onSubmit = { input -> questionViewModel.saveQuestion(step.chapter.id, input) },
                        submitError = saveQuestionState.error,
                        successMessage = saveQuestionState.success,
                        isSubmitting = saveQuestionState.isSubmitting
                    )
                }

                TeacherOverlay.None -> when (tab) {
                    TeacherTabs.HOME -> TeacherDashboardScreen(
                        overview = overview.copy(teacherName = displayName.ifBlank { "Teacher" }),
                        onRegisterStudent = { viewModel.consumeRegisterResult(); overlay = TeacherOverlay.RegisterStudent },
                        onAddEditQuestions = { overlay = TeacherOverlay.QuestionSubjects },
                        onTeachingSlides = { overlay = TeacherOverlay.SlideDecks },
                        onStudentProgress = { tab = TeacherTabs.STUDENTS }
                    )

                    TeacherTabs.STUDENTS -> ManageStudentsScreen(
                        students = students,
                        onBack = { tab = TeacherTabs.HOME },
                        onSearch = { },
                        onStudentClick = { },
                        onAddStudent = { viewModel.consumeRegisterResult(); overlay = TeacherOverlay.RegisterStudent }
                    )

                    TeacherTabs.SLIDES -> SubjectsScreen(
                        subjects = deckList.decks,
                        onBack = { tab = TeacherTabs.HOME },
                        onSubjectClick = { deck ->
                            slideViewModel.openDeck(deck.id)
                            overlay = TeacherOverlay.SlideViewer
                        }
                    )

                    else -> ProfileScreen(
                        state = DemoData.teacherProfile.copy(name = displayName.ifBlank { "Teacher" }),
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

private sealed interface AdminOverlay {
    data object None : AdminOverlay
    data object RegisterStudent : AdminOverlay
    data object RegisterTeacher : AdminOverlay
    data object QuestionSubjects : AdminOverlay
    data class QuestionClass(val subject: SubjectCardUi) : AdminOverlay
    data class QuestionChapters(val subject: SubjectCardUi, val classLevel: Int) : AdminOverlay
    data class QuestionForm(val chapter: SubjectCardUi) : AdminOverlay
}

@Composable
private fun AdminShell(adminUserId: String, displayName: String, onLogout: () -> Unit) {
    var tab by remember { mutableStateOf(AdminTabs.HOME) }
    var overlay by remember { mutableStateOf<AdminOverlay>(AdminOverlay.None) }

    val viewModel: AdminViewModel = hiltViewModel<AdminViewModel, AdminViewModel.Factory>(
        key = "admin:$adminUserId"
    ) { factory -> factory.create(adminUserId) }

    val questionViewModel: QuestionEditorViewModel = hiltViewModel<QuestionEditorViewModel, QuestionEditorViewModel.Factory>(
        key = "questions:$adminUserId"
    ) { factory -> factory.create(adminUserId) }

    val overview by viewModel.overview.collectAsState()
    val students by viewModel.students.collectAsState()
    val registerStudentState by viewModel.registerStudentState.collectAsState()
    val registerTeacherState by viewModel.registerTeacherState.collectAsState()
    val questionSubjects by questionViewModel.subjects.collectAsState()
    val questionChapters by questionViewModel.chapters.collectAsState()
    val saveQuestionState by questionViewModel.saveState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f)) {
            when (overlay) {
                AdminOverlay.RegisterStudent -> RegisterStudentScreen(
                    onSubmit = { input -> viewModel.registerStudent(input) },
                    submitError = registerStudentState.error,
                    successMessage = registerStudentState.success,
                    isSubmitting = registerStudentState.isSubmitting
                )

                AdminOverlay.RegisterTeacher -> RegisterTeacherScreen(
                    onSubmit = { input -> viewModel.registerTeacher(input) },
                    submitError = registerTeacherState.error,
                    successMessage = registerTeacherState.success,
                    isSubmitting = registerTeacherState.isSubmitting
                )

                AdminOverlay.QuestionSubjects -> SubjectsScreen(
                    subjects = questionSubjects,
                    onBack = { overlay = AdminOverlay.None },
                    onSubjectClick = { subject -> overlay = AdminOverlay.QuestionClass(subject) }
                )

                is AdminOverlay.QuestionClass -> {
                    val step = overlay as AdminOverlay.QuestionClass
                    PickClassScreen(
                        title = step.subject.title,
                        allowedClassLevels = com.gumthala.learningapp.core.ClassLevels.ALL,
                        onBack = { overlay = AdminOverlay.QuestionSubjects },
                        onPick = { level ->
                            questionViewModel.selectSubjectAndClass(step.subject.id, level)
                            overlay = AdminOverlay.QuestionChapters(step.subject, level)
                        }
                    )
                }

                is AdminOverlay.QuestionChapters -> {
                    val step = overlay as AdminOverlay.QuestionChapters
                    SubjectsScreen(
                        subjects = questionChapters,
                        onBack = { overlay = AdminOverlay.QuestionClass(step.subject) },
                        onSubjectClick = { chapter ->
                            questionViewModel.consumeSaveResult()
                            overlay = AdminOverlay.QuestionForm(chapter)
                        }
                    )
                }

                is AdminOverlay.QuestionForm -> {
                    val step = overlay as AdminOverlay.QuestionForm
                    QuestionEditorScreen(
                        chapterTitle = step.chapter.title,
                        onSubmit = { input -> questionViewModel.saveQuestion(step.chapter.id, input) },
                        submitError = saveQuestionState.error,
                        successMessage = saveQuestionState.success,
                        isSubmitting = saveQuestionState.isSubmitting
                    )
                }

                AdminOverlay.None -> when (tab) {
                    AdminTabs.HOME -> AdminDashboardScreen(
                        overview = overview.copy(adminName = displayName.ifBlank { "Admin" }),
                        onManageStudents = { tab = AdminTabs.PEOPLE },
                        onManageTeachers = { viewModel.consumeRegisterTeacherResult(); overlay = AdminOverlay.RegisterTeacher },
                        onManageContent = { tab = AdminTabs.CONTENT },
                        onHelpAndSupport = { }
                    )

                    AdminTabs.PEOPLE -> ManageStudentsScreen(
                        students = students,
                        onBack = { tab = AdminTabs.HOME },
                        onSearch = { },
                        onStudentClick = { },
                        onAddStudent = { viewModel.consumeRegisterStudentResult(); overlay = AdminOverlay.RegisterStudent }
                    )

                    AdminTabs.CONTENT -> AdminDashboardScreen(
                        overview = overview.copy(adminName = displayName.ifBlank { "Admin" }),
                        onManageStudents = { tab = AdminTabs.PEOPLE },
                        onManageTeachers = { overlay = AdminOverlay.RegisterTeacher },
                        onManageContent = { overlay = AdminOverlay.QuestionSubjects },
                        onHelpAndSupport = { }
                    )

                    else -> ProfileScreen(
                        state = DemoData.adminProfile.copy(name = displayName.ifBlank { "Admin" }),
                        onLogout = onLogout
                    )
                }
            }
        }
        RoleTabBar(items = AdminTabs.items, selectedIndex = tab, onSelect = { tab = it })
    }
}

/**
 * What's left on demo data after Stage 2: Teaching Slides (needs SlideRepository
 * wiring — SlideRepository exists but isn't connected here yet) and both Profile
 * screens' stats/menu (avatar, XP, achievements — cosmetic, no backing data model).
 */
private object DemoData {
    val teacherProfile = ProfileUiState(avatarEmoji = "👩‍🏫", name = "Teacher", subtitle = "Teacher")
    val adminProfile = ProfileUiState(avatarEmoji = "🛡️", name = "Admin", subtitle = "Admin")
}
