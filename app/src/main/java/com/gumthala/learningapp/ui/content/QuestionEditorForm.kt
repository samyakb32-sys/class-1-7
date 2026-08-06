package com.gumthala.learningapp.ui.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.data.local.entity.ChapterEntity
import com.gumthala.learningapp.data.local.entity.QuestionEntity
import com.gumthala.learningapp.data.repository.ContentRepository
import com.gumthala.learningapp.domain.model.TrilingualText
import kotlinx.coroutines.launch

/** Plain, unstyled placeholder pending a mockup — one MCQ, all 3 languages, 4 options. */
@Composable
fun QuestionEditorForm(
    contentRepository: ContentRepository,
    chapter: ChapterEntity,
    existing: QuestionEntity?,
    teacherId: String?,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    var textEn by remember { mutableStateOf(existing?.text?.en.orEmpty()) }
    var textMr by remember { mutableStateOf(existing?.text?.mr.orEmpty()) }
    var textHi by remember { mutableStateOf(existing?.text?.hi.orEmpty()) }
    val optionsEn = remember { mutableStateListOf(*fourOrBlank(existing?.optionsEn)) }
    val optionsMr = remember { mutableStateListOf(*fourOrBlank(existing?.optionsMr)) }
    val optionsHi = remember { mutableStateListOf(*fourOrBlank(existing?.optionsHi)) }
    var correctIndex by remember { mutableIntStateOf(existing?.correctIndex ?: 0) }
    var difficulty by remember { mutableStateOf(existing?.difficulty ?: "medium") }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existing == null) "New Question" else "Edit Question") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp)) {
            item {
                Text("Question text", modifier = Modifier.padding(top = 12.dp))
                OutlinedTextField(textEn, { textEn = it }, label = { Text("English") }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
                OutlinedTextField(textMr, { textMr = it }, label = { Text("Marathi") }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
                OutlinedTextField(textHi, { textHi = it }, label = { Text("Hindi") }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp))
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                Text("Options (pick the correct one below)")
            }
            items(4) { i ->
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        FilterChip(
                            selected = correctIndex == i,
                            onClick = { correctIndex = i },
                            label = { Text("Correct") }
                        )
                        Text(" Option ${i + 1}", modifier = Modifier.padding(start = 8.dp))
                    }
                    OutlinedTextField(optionsEn[i], { optionsEn[i] = it }, label = { Text("English") }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                    OutlinedTextField(optionsMr[i], { optionsMr[i] = it }, label = { Text("Marathi") }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                    OutlinedTextField(optionsHi[i], { optionsHi[i] = it }, label = { Text("Hindi") }, modifier = Modifier.fillMaxWidth().padding(top = 4.dp))
                }
            }
            item {
                Text("Difficulty", modifier = Modifier.padding(top = 16.dp, bottom = 8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("easy", "medium", "hard").forEach { d ->
                        FilterChip(selected = difficulty == d, onClick = { difficulty = d }, label = { Text(d) })
                    }
                }
                if (error != null) {
                    Text(error.orEmpty(), modifier = Modifier.padding(top = 12.dp))
                }
                Button(
                    onClick = {
                        val allOptionsFilled = optionsEn.all { it.isNotBlank() } && optionsMr.all { it.isNotBlank() } && optionsHi.all { it.isNotBlank() }
                        if (textEn.isBlank() || !allOptionsFilled) {
                            error = "Fill in the question text and all 4 options in every language"
                        } else {
                            scope.launch {
                                val id = existing?.id ?: contentRepository.newQuestionId()
                                contentRepository.saveQuestion(
                                    QuestionEntity(
                                        id = id,
                                        chapterId = chapter.id,
                                        orderIndex = existing?.orderIndex ?: 0,
                                        text = TrilingualText(textEn, textMr, textHi),
                                        optionsEn = optionsEn.toList(),
                                        optionsMr = optionsMr.toList(),
                                        optionsHi = optionsHi.toList(),
                                        correctIndex = correctIndex,
                                        difficulty = difficulty,
                                        createdByTeacherId = existing?.createdByTeacherId ?: teacherId
                                    )
                                )
                                onSaved()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 32.dp)
                ) { Text("Save") }
            }
        }
    }
}

private fun fourOrBlank(existing: List<String>?): Array<String> =
    if (existing != null && existing.size == 4) existing.toTypedArray() else arrayOf("", "", "", "")
