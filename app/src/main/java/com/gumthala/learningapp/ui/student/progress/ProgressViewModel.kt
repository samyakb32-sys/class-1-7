package com.gumthala.learningapp.ui.student.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.data.repository.ContentRepository
import com.gumthala.learningapp.data.repository.ProgressRepository
import com.gumthala.learningapp.domain.model.ContentConstants
import com.gumthala.learningapp.ui.student.StudentSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

data class SubjectPerformance(val nameEn: String, val colorKey: String, val percent: Int)

data class ProgressUiState(
    val overallPercent: Int = 0,
    val completedChapters: Int = 0,
    val totalChapters: Int = 0,
    val subjectPerformance: List<SubjectPerformance> = emptyList(),
    val weeklyBars: List<Float> = List(7) { 0f }
)

class ProgressViewModel(
    private val session: StudentSession,
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) : ViewModel() {

    val uiState = contentRepository.observeSubjects(session.classLevel)
        .flatMapLatest { subjects ->
            combine(
                progressRepository.observeCompletedChapterIds(session.studentId),
                progressRepository.observeAttempts(session.studentId)
            ) { completedIds, attempts ->
                val totalChapters = subjects.size * ContentConstants.CHAPTERS_PER_SUBJECT
                val completedSet = completedIds.toSet()

                val perSubject = subjects.map { subject ->
                    // Chapters for this subject follow the "{subjectId}-chN" id scheme from ContentSeeder.
                    val subjectChapterIds = (0 until ContentConstants.CHAPTERS_PER_SUBJECT).map { "${subject.id}-ch$it" }
                    val done = subjectChapterIds.count { it in completedSet }
                    SubjectPerformance(
                        nameEn = subject.name.en,
                        colorKey = subject.colorFamily,
                        percent = (done * 100 / ContentConstants.CHAPTERS_PER_SUBJECT)
                    )
                }

                val today = Instant.now().atZone(ZoneId.systemDefault()).toLocalDate()
                val counts = IntArray(7)
                attempts.filter { it.completedAtMillis != null }.forEach { attempt ->
                    val day = Instant.ofEpochMilli(attempt.completedAtMillis!!).atZone(ZoneId.systemDefault()).toLocalDate()
                    val diff = ChronoUnit.DAYS.between(day, today).toInt()
                    if (diff in 0..6) counts[6 - diff]++
                }
                val maxCount = (counts.maxOrNull() ?: 0).coerceAtLeast(1)
                val bars = counts.map { it.toFloat() / maxCount }

                ProgressUiState(
                    overallPercent = if (totalChapters == 0) 0 else completedSet.size * 100 / totalChapters,
                    completedChapters = completedSet.size,
                    totalChapters = totalChapters,
                    subjectPerformance = perSubject,
                    weeklyBars = bars
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProgressUiState())
}
