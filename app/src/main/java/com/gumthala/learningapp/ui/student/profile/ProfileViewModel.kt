package com.gumthala.learningapp.ui.student.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.data.repository.ProgressRepository
import com.gumthala.learningapp.ui.student.StudentSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ProfileUiState(
    val name: String = "",
    val classLevel: Int = 1,
    val xp: Int = 0,
    val coins: Int = 0,
    val streakDays: Int = 0
)

class ProfileViewModel(
    session: StudentSession,
    progressRepository: ProgressRepository
) : ViewModel() {
    val uiState = progressRepository.observeStats(session.studentId)
        .map { stats ->
            ProfileUiState(
                name = session.name,
                classLevel = session.classLevel,
                xp = stats?.xp ?: 0,
                coins = stats?.coins ?: 0,
                streakDays = stats?.streakDays ?: 0
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState(name = session.name, classLevel = session.classLevel))

    val badges = progressRepository.observeBadges(session.studentId)
}
