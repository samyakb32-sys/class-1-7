package com.gumthala.learningapp.ui.student.subjects

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.domain.model.AppLanguage
import com.gumthala.learningapp.ui.components.Pill
import com.gumthala.learningapp.ui.components.SubTopBar
import com.gumthala.learningapp.ui.theme.Ink
import com.gumthala.learningapp.ui.theme.Muted
import com.gumthala.learningapp.ui.theme.SubjectColorFamily

@Composable
fun SubjectsScreen(
    viewModel: SubjectsViewModel,
    language: AppLanguage,
    onBack: () -> Unit,
    onOpenSubject: (subjectId: String, firstIncompleteChapterId: String?) -> Unit
) {
    val rows by viewModel.subjects.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SubTopBar(title = "Subjects", onBack = onBack, trailingEmoji = "🔍")
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(rows, key = { it.subject.id }) { row ->
                SubjectCard(
                    row = row,
                    language = language,
                    totalChapters = viewModel.chaptersPerSubject,
                    onClick = {
                        val nextChapter = row.chapters.firstOrNull { it.id !in row.completedChapterIds }
                            ?: row.chapters.firstOrNull()
                        onOpenSubject(row.subject.id, nextChapter?.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun SubjectCard(row: SubjectRow, language: AppLanguage, totalChapters: Int, onClick: () -> Unit) {
    val family = SubjectColorFamily.fromKey(row.subject.colorFamily)
    val percent = if (totalChapters == 0) 0 else (row.completedChapters * 100 / totalChapters)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 9.dp)
            .background(family.cardBg, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(family.chipBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(row.subject.iconEmoji, fontSize = 18.sp)
        }
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = row.subject.name.forLanguage(language),
                    style = MaterialTheme.typography.titleSmall,
                    color = Ink
                )
                Pill(difficultyLabel(row), family.badgeBg, family.ink)
            }
            Text(
                text = "$percent% Complete · ${row.completedChapters}/$totalChapters",
                fontSize = 9.sp,
                color = Muted,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

private fun difficultyLabel(row: SubjectRow): String = when {
    row.completedChapters == 0 -> "Easy-Hard"
    row.completedChapters < row.chapters.size -> "In Progress"
    else -> "Completed"
}
