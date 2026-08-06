package com.gumthala.learningapp.ui.student.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.data.local.entity.SubjectEntity
import com.gumthala.learningapp.data.repository.ContentRepository
import com.gumthala.learningapp.data.repository.ProgressRepository
import com.gumthala.learningapp.ui.student.StudentSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val studentName: String = "",
    val xp: Int = 0,
    val coins: Int = 0,
    val streakDays: Int = 0,
    val subjects: List<SubjectEntity> = emptyList(),
    val weeklyProgressPercent: Int = 0,
    val upcomingSubjectName: String = ""
)

class HomeViewModel(
    session: StudentSession,
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = combine(
        contentRepository.observeSubjects(session.classLevel),
        progressRepository.observeStats(session.studentId),
        progressRepository.observeCompletedChapterIds(session.studentId)
    ) { subjects, stats, completedChapterIds ->
        val language = session.language
        val totalChapters = subjects.size * com.gumthala.learningapp.domain.model.ContentConstants.CHAPTERS_PER_SUBJECT
        val weeklyProgress = if (totalChapters == 0) 0 else (completedChapterIds.size * 100 / totalChapters).coerceIn(0, 100)
        val upcoming = subjects.firstOrNull()?.name?.forLanguage(language) ?: ""
        HomeUiState(
            studentName = session.name,
            xp = stats?.xp ?: 0,
            coins = stats?.coins ?: 0,
            streakDays = stats?.streakDays ?: 0,
            subjects = subjects,
            weeklyProgressPercent = weeklyProgress,
            upcomingSubjectName = upcoming
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState(studentName = session.name))
}
