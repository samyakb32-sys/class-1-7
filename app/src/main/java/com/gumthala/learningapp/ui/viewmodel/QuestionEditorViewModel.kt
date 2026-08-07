package com.gumthala.learningapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gumthala.learningapp.core.AppLanguage
import com.gumthala.learningapp.core.LocalizedText
import com.gumthala.learningapp.data.repo.ContentRepository
import com.gumthala.learningapp.data.repo.OptionDraft
import com.gumthala.learningapp.ui.screens.SubjectCardUi
import com.gumthala.learningapp.ui.screens.roster.QuestionDraftInput
import com.gumthala.learningapp.ui.theme.SubjectPalette
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class SaveQuestionUiState(
    val isSubmitting: Boolean = false,
    val error: String? = null,
    val success: String? = null
)

/**
 * Backs the subject → chapter → question-form flow in [QuestionEditorScreen].
 * Class level is chosen by the caller (RootNavHost restricts the choice to the
 * teacher's assignedClassLevels — this ViewModel doesn't re-check that, unlike
 * AuthRepository.registerStudent, since saveQuestion has no such restriction
 * server-side; a teacher authoring a question for someone else's class is a
 * judgment call this build leaves permissive rather than blocking).
 */
@HiltViewModel(assistedFactory = QuestionEditorViewModel.Factory::class)
class QuestionEditorViewModel @AssistedInject constructor(
    @Assisted private val authorUserId: String,
    private val contentRepository: ContentRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(authorUserId: String): QuestionEditorViewModel
    }

    private val language = AppLanguage.ENGLISH

    val subjects: StateFlow<List<SubjectCardUi>> = contentRepository.observeSubjects()
        .map { subjects ->
            subjects.mapIndexed { index, subject ->
                SubjectCardUi(
                    id = subject.id,
                    emoji = "📘",
                    title = subject.name.get(language),
                    badge = "",
                    meta = "Tap to pick a class & chapter",
                    palette = SubjectPalette.all[index % SubjectPalette.all.size]
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val subjectAndClass = MutableStateFlow<Pair<String, Int>?>(null)

    val chapters: StateFlow<List<SubjectCardUi>> = subjectAndClass
        .flatMapLatest { pair ->
            if (pair == null) flowOf(emptyList()) else {
                contentRepository.observeChapters(pair.first, pair.second).map { chapters ->
                    chapters.map { chapter ->
                        SubjectCardUi(
                            id = chapter.id,
                            emoji = chapter.iconKey ?: "📄",
                            title = chapter.title.get(language),
                            badge = "Class ${pair.second}",
                            meta = "Add a question here",
                            palette = SubjectPalette.all[chapter.orderIndex % SubjectPalette.all.size]
                        )
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectSubjectAndClass(subjectId: String, classLevel: Int) {
        subjectAndClass.value = subjectId to classLevel
    }

    private val _saveState = MutableStateFlow(SaveQuestionUiState())
    val saveState: StateFlow<SaveQuestionUiState> = _saveState.asStateFlow()

    fun saveQuestion(chapterId: String, input: QuestionDraftInput) {
        if (_saveState.value.isSubmitting) return
        _saveState.value = SaveQuestionUiState(isSubmitting = true)
        viewModelScope.launch {
            runCatching {
                contentRepository.saveQuestion(
                    chapterId = chapterId,
                    authorUserId = authorUserId,
                    prompt = LocalizedText(input.promptEn),
                    options = input.optionsEn.mapIndexed { index, text ->
                        OptionDraft(text = LocalizedText(text), isCorrect = index == input.correctOptionIndex)
                    }
                )
            }.fold(
                onSuccess = { _saveState.value = SaveQuestionUiState(success = "Question saved.") },
                onFailure = { e -> _saveState.value = SaveQuestionUiState(error = e.message ?: "Couldn't save. Try again.") }
            )
        }
    }

    fun consumeSaveResult() {
        _saveState.value = SaveQuestionUiState()
    }
}
