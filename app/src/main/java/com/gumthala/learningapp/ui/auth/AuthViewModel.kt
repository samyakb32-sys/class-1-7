package com.gumthala.learningapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.data.repository.AuthRepository
import com.gumthala.learningapp.data.repository.LoginResult
import com.gumthala.learningapp.data.session.SessionManager
import com.gumthala.learningapp.domain.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun studentLogin(name: String, classLevel: Int) {
        if (name.isBlank()) {
            _errorMessage.value = "Enter your name"
            return
        }
        viewModelScope.launch {
            when (val result = authRepository.studentLogin(name, classLevel)) {
                is LoginResult.Success -> sessionManager.signIn(result.userId, Role.STUDENT, result.displayName, result.classLevel)
                LoginResult.NotRegistered -> _errorMessage.value = "You're not registered yet. Ask your teacher to add you."
                LoginResult.InvalidCredentials -> _errorMessage.value = "Something went wrong. Try again."
            }
        }
    }

    fun staffLogin(email: String, password: String, asAdmin: Boolean) {
        if (email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Enter email and password"
            return
        }
        viewModelScope.launch {
            val result = if (asAdmin) authRepository.adminLogin(email, password) else authRepository.teacherLogin(email, password)
            when (result) {
                is LoginResult.Success -> sessionManager.signIn(result.userId, if (asAdmin) Role.ADMIN else Role.TEACHER, result.displayName)
                LoginResult.NotRegistered -> _errorMessage.value = "No account found for that email."
                LoginResult.InvalidCredentials -> _errorMessage.value = "Incorrect password."
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
