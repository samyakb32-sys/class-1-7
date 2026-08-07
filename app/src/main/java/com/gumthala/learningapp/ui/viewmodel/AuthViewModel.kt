package com.gumthala.learningapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.core.UserRole
import com.gumthala.learningapp.data.repo.AuthRepository
import com.gumthala.learningapp.data.repo.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/** What [RootNavHost] renders once sign-in resolves. Mirrors [UserRole] one-to-one. */
enum class SignedInRole { STUDENT, TEACHER, ADMIN }

data class AuthUiState(
    val isLoadingSession: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val signedInAs: SignedInRole? = null,
    val signedInUserId: String? = null,
    val signedInName: String? = null,
    val signedInClassLevel: Int? = null
)

/**
 * Backs both login screens with the real [AuthRepository] instead of the demo
 * shell-switch that [RootNavHost] used before. Kept deliberately thin — no
 * navigation logic here, just "did sign-in succeed, and as whom".
 *
 * Also restores a persisted session on launch: the spec requires staying signed
 * in until an explicit Logout, so [RootNavHost] must not default to Role Select
 * just because the process restarted.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            // Never let a startup crash strand the user on a spinner forever, and
            // never let it crash the app — worst case, treat as logged-out.
            runCatching { authRepository.currentSession.first() }
                .onSuccess { session ->
                    _state.value = if (session != null) {
                        AuthUiState(
                            isLoadingSession = false,
                            signedInAs = when (session.role) {
                                UserRole.ADMIN -> SignedInRole.ADMIN
                                UserRole.TEACHER -> SignedInRole.TEACHER
                                UserRole.STUDENT -> SignedInRole.STUDENT
                            },
                            signedInUserId = session.userId,
                            signedInName = session.displayName,
                            signedInClassLevel = session.classLevel
                        )
                    } else {
                        AuthUiState(isLoadingSession = false)
                    }
                }
                .onFailure { _state.value = AuthUiState(isLoadingSession = false) }
        }
    }

    fun signInStudent(name: String, classLevel: Int) {
        if (_state.value.isSubmitting) return
        _state.value = _state.value.copy(isSubmitting = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { authRepository.signInStudent(name, classLevel) }
                .onSuccess { result ->
                    when (result) {
                        is AuthResult.Success -> _state.value = AuthUiState(
                            isLoadingSession = false,
                            signedInAs = SignedInRole.STUDENT,
                            signedInUserId = result.user.id,
                            signedInName = result.user.fullName,
                            signedInClassLevel = result.user.classLevel
                        )
                        AuthResult.StudentNotRegistered -> _state.value = _state.value.copy(
                            isSubmitting = false,
                            errorMessage = "No student named \"$name\" is registered in Class $classLevel. Ask your teacher to add you first."
                        )
                        else -> _state.value = _state.value.copy(
                            isSubmitting = false,
                            errorMessage = "Something went wrong. Please try again."
                        )
                    }
                }
                .onFailure {
                    _state.value = _state.value.copy(isSubmitting = false, errorMessage = "Something went wrong. Please try again.")
                }
        }
    }

    fun signInStaff(email: String, password: String) {
        if (_state.value.isSubmitting) return
        _state.value = _state.value.copy(isSubmitting = true, errorMessage = null)
        viewModelScope.launch {
            runCatching { authRepository.signInStaff(email, password) }
                .onSuccess { result ->
                    when (result) {
                        is AuthResult.Success -> _state.value = AuthUiState(
                            isLoadingSession = false,
                            signedInAs = if (result.user.role == UserRole.ADMIN) SignedInRole.ADMIN else SignedInRole.TEACHER,
                            signedInUserId = result.user.id,
                            signedInName = result.user.fullName
                        )
                        AuthResult.InvalidCredentials -> _state.value = _state.value.copy(
                            isSubmitting = false,
                            errorMessage = "Email or password is incorrect."
                        )
                        AuthResult.AccountDisabled -> _state.value = _state.value.copy(
                            isSubmitting = false,
                            errorMessage = "This account has been disabled. Contact your admin."
                        )
                        else -> _state.value = _state.value.copy(
                            isSubmitting = false,
                            errorMessage = "Something went wrong. Please try again."
                        )
                    }
                }
                .onFailure {
                    _state.value = _state.value.copy(isSubmitting = false, errorMessage = "Something went wrong. Please try again.")
                }
        }
    }

    fun consumeSignIn() {
        _state.value = AuthUiState(isLoadingSession = false)
    }

    fun logout() {
        viewModelScope.launch { runCatching { authRepository.logout() } }
        _state.value = AuthUiState(isLoadingSession = false)
    }
}
