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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.AppColors2
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
 * `Manage Students` — admin's roster with a search affordance and a floating
 * "+" that opens registration. Teachers see the same row shape scoped to their
 * own classes via [TeacherStudentsScreen] rather than duplicating this list.
 */
@Composable
fun ManageStudentsScreen(
    students: List<ManagedStudentRow>,
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onStudentClick: (ManagedStudentRow) -> Unit,
    onAddStudent: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                Text("🔍", style = body(TextSize.Body), modifier = Modifier.clickable(onClick = onSearch))
            }

            LazyColumn {
                items(students, key = { it.id }) { student ->
                    StudentRow(student, onClick = { onStudentClick(student) })
                }
            }
        }

        FloatingAddButton(onClick = onAddStudent, modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp))
    }
}

/** `.stud-row` — shared between Manage Students and the Teacher's roster. */
@Composable
fun StudentRow(student: ManagedStudentRow, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .clip(RoundedCornerShape(Radius.Soft))
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
            .clip(CircleShape)
            .background(AppColors.Yellow)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text("＋", style = display(TextSize.Title, FontWeight.Bold), color = AppColors.OnYellowButton)
    }
}
