package com.gumthala.learningapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gumthala.learningapp.data.session.UserSession
import com.gumthala.learningapp.di.LocalAppContainer
import com.gumthala.learningapp.domain.model.AppLanguage
import com.gumthala.learningapp.domain.model.Role
import com.gumthala.learningapp.ui.admin.AdminHomeScreen
import com.gumthala.learningapp.ui.auth.AuthViewModel
import com.gumthala.learningapp.ui.auth.RoleSelectScreen
import com.gumthala.learningapp.ui.auth.StaffLoginScreen
import com.gumthala.learningapp.ui.auth.StudentLoginScreen
import com.gumthala.learningapp.ui.common.HelpSupportScreen
import com.gumthala.learningapp.ui.student.StudentNavGraph
import com.gumthala.learningapp.ui.student.StudentSession
import com.gumthala.learningapp.ui.teacher.TeacherHomeScreen
import kotlinx.coroutines.launch

@Composable
fun AppRoot() {
    val container = LocalAppContainer.current
    val session by container.sessionManager.session.collectAsState(initial = UNSET)
    val language by container.sessionManager.language.collectAsState(initial = AppLanguage.ENGLISH)

    Box(modifier = Modifier.fillMaxSize()) {
        when (session) {
            UNSET -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            null -> AuthFlow()
            else -> {
                val userSession = session!!
                when (userSession.role) {
                    Role.STUDENT -> {
                        val scope = rememberCoroutineScope()
                        StudentNavGraph(
                            session = StudentSession(userSession.userId, userSession.displayName, userSession.classLevel ?: 1, language),
                            onLogout = { scope.launch { container.sessionManager.logout() } }
                        )
                    }
                    Role.TEACHER -> {
                        val scope = rememberCoroutineScope()
                        TeacherHomeScreen(
                            teacherId = userSession.userId,
                            teacherName = userSession.displayName,
                            onLogout = { scope.launch { container.sessionManager.logout() } }
                        )
                    }
                    Role.ADMIN -> {
                        val scope = rememberCoroutineScope()
                        var showHelp by remember { mutableStateOf(false) }
                        if (showHelp) {
                            HelpSupportScreen(onBack = { showHelp = false })
                        } else {
                            AdminHomeScreen(
                                adminName = userSession.displayName,
                                onOpenHelp = { showHelp = true },
                                onLogout = { scope.launch { container.sessionManager.logout() } }
                            )
                        }
                    }
                }
            }
        }
    }
}

private val UNSET = UserSession(userId = "__unset__", role = Role.STUDENT, displayName = "", classLevel = null)

@Composable
private fun AuthFlow() {
    val container = LocalAppContainer.current
    val authViewModel = remember { AuthViewModel(container.authRepository, container.sessionManager) }
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "role_select") {
        composable("role_select") {
            RoleSelectScreen(
                onSelectStudent = { navController.navigate("student_login") },
                onSelectStaff = { navController.navigate("staff_login") }
            )
        }
        composable("student_login") {
            StudentLoginScreen(authViewModel, onBack = { navController.popBackStack() })
        }
        composable("staff_login") {
            StaffLoginScreen(authViewModel, onBack = { navController.popBackStack() })
        }
    }
}
