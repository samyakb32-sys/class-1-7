package com.gumthala.learningapp.ui.student.lesson

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.data.local.entity.ChapterEntity
import com.gumthala.learningapp.data.local.entity.QuestionEntity
import com.gumthala.learningapp.data.local.entity.SubjectEntity
import com.gumthala.learningapp.data.repository.ContentRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

data class LessonUiState(
    val subject: SubjectEntity? = null,
    val chapter: ChapterEntity? = null,
    val sampleQuestion: QuestionEntity? = null,
    val chapterIndex: Int = 0,
    val totalChapters: Int = 1,
    val prevChapterId: String? = null,
    val nextChapterId: String? = null
)

/** One fixed chapter per instance — navigating to a different chapter pushes a new "lesson/{id}" route. */
class LessonViewModel(
    chapterId: String,
    contentRepository: ContentRepository
) : ViewModel() {

    val uiState: StateFlow<LessonUiState> = contentRepository.observeChapter(chapterId)
        .flatMapLatest { chapter ->
            if (chapter == null) {
                kotlinx.coroutines.flow.flowOf(LessonUiState())
            } else {
                combine(
                    contentRepository.observeQuestions(chapterId),
                    contentRepository.observeChapters(chapter.subjectId)
                ) { questions, siblings ->
                    val index = siblings.indexOfFirst { it.id == chapterId }.coerceAtLeast(0)
                    LessonUiState(
                        chapter = chapter,
                        sampleQuestion = questions.firstOrNull(),
                        chapterIndex = index,
                        totalChapters = siblings.size.coerceAtLeast(1),
                        prevChapterId = siblings.getOrNull(index - 1)?.id,
                        nextChapterId = siblings.getOrNull(index + 1)?.id
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LessonUiState())
}
