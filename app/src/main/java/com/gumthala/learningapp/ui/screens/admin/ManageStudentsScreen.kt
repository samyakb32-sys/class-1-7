package com.gumthala.learningapp.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.AppColors2
import com.gumthala.learningapp.ui.components.softCard
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

data class ManagedStudentRow(
    val id: String,
    val avatarEmoji: String,
    val fullName: String,
    val classLabel: String,
    val progressPct: Int
)

/**
 * `Manage Students` — roster with working search and a floating "+" that opens
 * registration. Used by both Admin (all classes) and Teacher (their own).
 *
 * Search filters the list already in memory rather than round-tripping to Room:
 * a single school's roster is small, and the caller's Flow already has every
 * row. ponytail: in-memory filter, move to a DAO query if a roster ever gets
 * big enough to stutter.
 */
@Composable
fun ManageStudentsScreen(
    students: List<ManagedStudentRow>,
    onBack: () -> Unit,
    onStudentClick: (ManagedStudentRow) -> Unit,
    onAddStudent: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val visible = remember(students, query) {
        if (query.isBlank()) students
        else students.filter {
            it.fullName.contains(query.trim(), ignoreCase = true) ||
                it.classLabel.contains(query.trim(), ignoreCase = true)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("‹", style = display(TextSize.Header, FontWeight.Bold), color = AppColors.Ink,
                    modifier = Modifier.clickable(onClick = onBack))
                Text(
                    "Students",
                    style = display(TextSize.Header, FontWeight.ExtraBold),
                    color = AppColors.Ink,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Text("${visible.size}", style = body(TextSize.Small, FontWeight.Bold), color = AppColors.Muted)
            }

            SearchBox(query = query, onQueryChange = { query = it })

            if (visible.isEmpty()) {
                Text(
                    if (students.isEmpty()) "No students registered yet. Tap ＋ to add one."
                    else "No student matches \"$query\".",
                    style = body(TextSize.Small, FontWeight.Bold),
                    color = AppColors.Muted,
                    modifier = Modifier.padding(top = 24.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
                    items(visible, key = { it.id }) { student ->
                        StudentRow(student, onClick = { onStudentClick(student) })
                    }
                }
            }
        }

        FloatingAddButton(onClick = onAddStudent, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp))
    }
}

/** Flat search field matching the app's input styling. */
@Composable
private fun SearchBox(query: String, onQueryChange: (String) -> Unit) {
    val shape = RoundedCornerShape(Radius.Card)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(AppColors.SurfaceSoft)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🔍", style = body(TextSize.Small))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = body(TextSize.Small, FontWeight.Bold).copy(color = AppColors.Ink),
            cursorBrush = SolidColor(AppColors.Purple),
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            decorationBox = { inner ->
                if (query.isEmpty()) {
                    Text(
                        "Search by name or class",
                        style = body(TextSize.Small, FontWeight.Bold),
                        color = AppColors.Muted
                    )
                }
                inner()
            }
        )
        if (query.isNotEmpty()) {
            Text(
                "✕",
                style = body(TextSize.Small, FontWeight.Bold),
                color = AppColors.Muted,
                modifier = Modifier.clickable { onQueryChange("") }
            )
        }
    }
}

/** `.stud-row` — shared between Manage Students and the Teacher's roster. */
@Composable
fun StudentRow(student: ManagedStudentRow, onClick: () -> Unit) {
    val shape = RoundedCornerShape(Radius.Soft)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .softCard(shape, 2.dp)
            .clip(shape)
            .background(AppColors.SurfaceSoft)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(AppColors2.AvatarGradStart, AppColors2.AvatarGradEnd))),
            contentAlignment = Alignment.Center
        ) {
            Text(student.avatarEmoji, style = body(TextSize.Small))
        }
        Column(modifier = Modifier.weight(1f).padding(start = 10.dp)) {
            Text(student.fullName, style = display(TextSize.Small, FontWeight.Bold), color = AppColors.Ink)
            Text(student.classLabel, style = body(TextSize.Micro, FontWeight.Bold), color = AppColors.Muted)
        }
        Text(
            "${student.progressPct}%",
            style = display(TextSize.Small, FontWeight.ExtraBold),
            color = AppColors.Purple
        )
    }
}

/** `.fab` — the floating "+" that opens student registration. */
@Composable
fun FloatingAddButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(44.dp)
            .softCard(CircleShape, 6.dp)
            .clip(CircleShape)
            .background(AppColors.Yellow)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("＋", style = display(TextSize.Title, FontWeight.Bold), color = AppColors.OnYellowButton)
    }
}
