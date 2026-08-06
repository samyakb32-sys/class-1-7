package com.gumthala.learningapp.ui.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.data.local.entity.ChapterEntity
import com.gumthala.learningapp.data.local.entity.QuestionEntity
import com.gumthala.learningapp.data.local.entity.SubjectEntity
import com.gumthala.learningapp.data.repository.ContentRepository

private sealed interface ContentStep {
    data object PickClass : ContentStep
    data class PickSubject(val classLevel: Int) : ContentStep
    data class PickChapter(val subject: SubjectEntity) : ContentStep
    data class QuestionList(val subject: SubjectEntity, val chapter: ChapterEntity) : ContentStep
    data class EditQuestion(val subject: SubjectEntity, val chapter: ChapterEntity, val existing: QuestionEntity?) : ContentStep
}

/**
 * Plain, unstyled placeholder pending a mockup — lets a Teacher (their own questions,
 * [teacherId] non-null) or Admin ([teacherId] null, any question) author/edit quiz
 * questions: pick class → subject → chapter → add/edit a question.
 */
@Composable
fun TeacherContentScreen(
    contentRepository: ContentRepository,
    teacherId: String?,
    availableClasses: List<Int>,
    onBack: () -> Unit
) {
    var step by remember { mutableStateOf<ContentStep>(ContentStep.PickClass) }

    when (val s = step) {
        is ContentStep.PickClass -> Scaffold(
            topBar = { BackBar("Pick a Class", onBack) }
        ) { padding ->
            LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                items(availableClasses) { level ->
                    ListItem(headlineContent = { Text("Class $level") }, modifier = Modifier.fillMaxWidth())
                    Button(
                        onClick = { step = ContentStep.PickSubject(level) },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                    ) { Text("Open") }
                }
            }
        }

        is ContentStep.PickSubject -> {
            val subjects by contentRepository.observeSubjects(s.classLevel).collectAsState(initial = emptyList<SubjectEntity>())
            Scaffold(topBar = { BackBar("Class ${s.classLevel} · Pick a Subject", { step = ContentStep.PickClass }) }) { padding ->
                LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                    items(subjects) { subject ->
                        ListItem(headlineContent = { Text(subject.name.en) }, modifier = Modifier.fillMaxWidth())
                        Button(
                            onClick = { step = ContentStep.PickChapter(subject) },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                        ) { Text("Open") }
                    }
                }
            }
        }

        is ContentStep.PickChapter -> {
            val chapters by contentRepository.observeChapters(s.subject.id).collectAsState(initial = emptyList<ChapterEntity>())
            Scaffold(topBar = { BackBar("${s.subject.name.en} · Pick a Chapter", { step = ContentStep.PickSubject(s.subject.classLevel) }) }) { padding ->
                LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                    items(chapters) { chapter ->
                        ListItem(headlineContent = { Text(chapter.title.en) }, modifier = Modifier.fillMaxWidth())
                        Button(
                            onClick = { step = ContentStep.QuestionList(s.subject, chapter) },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                        ) { Text("Open") }
                    }
                }
            }
        }

        is ContentStep.QuestionList -> {
            val questions by contentRepository.observeQuestions(s.chapter.id).collectAsState(initial = emptyList<QuestionEntity>())
            Scaffold(topBar = { BackBar(s.chapter.title.en, { step = ContentStep.PickChapter(s.subject) }) }) { padding ->
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    Button(
                        onClick = { step = ContentStep.EditQuestion(s.subject, s.chapter, null) },
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) { Text("+ New Question") }
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(questions) { q ->
                            ListItem(
                                headlineContent = { Text(q.text.en) },
                                supportingContent = { Text(if (q.createdByTeacherId != null) "Teacher-authored" else "Built-in") }
                            )
                            Button(
                                onClick = { step = ContentStep.EditQuestion(s.subject, s.chapter, q) },
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
                            ) { Text("Edit") }
                            HorizontalDivider()
                        }
                    }
                }
            }
        }

        is ContentStep.EditQuestion -> {
            QuestionEditorForm(
                contentRepository = contentRepository,
                chapter = s.chapter,
                existing = s.existing,
                teacherId = teacherId,
                onBack = { step = ContentStep.QuestionList(s.subject, s.chapter) },
                onSaved = { step = ContentStep.QuestionList(s.subject, s.chapter) }
            )
        }
    }
}

@Composable
private fun BackBar(title: String, onBack: () -> Unit) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
        }
    )
}
