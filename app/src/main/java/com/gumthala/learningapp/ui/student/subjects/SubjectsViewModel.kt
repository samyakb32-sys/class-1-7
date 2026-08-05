package com.gumthala.learningapp.ui.student.subjects

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.data.local.entity.ChapterEntity
import com.gumthala.learningapp.data.local.entity.SubjectEntity
import com.gumthala.learningapp.data.repository.ContentRepository
import com.gumthala.learningapp.data.repository.ProgressRepository
import com.gumthala.learningapp.domain.model.ContentConstants
import com.gumthala.learningapp.ui.student.StudentSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class SubjectRow(val subject: SubjectEntity, val chapters: List<ChapterEntity>, val completedChapterIds: Set<String>) {
    val completedChapters: Int get() = chapters.count { it.id in completedChapterIds }
}

class SubjectsViewModel(
    private val session: StudentSession,
    contentRepository: ContentRepository,
    progressRepository: ProgressRepository
) : ViewModel() {

    val subjects = contentRepository.observeSubjects(session.classLevel)
        .flatMapLatest { subjects ->
            combine(subjects.map { subject -> contentRepository.observeChapters(subject.id) }) { chaptersArrays ->
                subjects.zip(chaptersArrays.toList())
            }
        }
        .combine(progressRepository.observeCompletedChapterIds(session.studentId)) { subjectChapters, completedIds ->
            val completedSet = completedIds.toSet()
            subjectChapters.map { (subject, chapters) ->
                SubjectRow(subject, chapters, completedSet)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val chaptersPerSubject = ContentConstants.CHAPTERS_PER_SUBJECT
}
