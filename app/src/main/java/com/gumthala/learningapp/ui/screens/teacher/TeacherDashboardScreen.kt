package com.gumthala.learningapp.ui.screens.teacher

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.item
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.components.BleedHeader
import com.gumthala.learningapp.ui.components.PurpleGradient
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.display

data class TeacherOverview(
    val teacherName: String,
    val myStudentCount: Int,
    val avgProgressPct: Int
)

/**
 * `Teacher Dashboard` — a sub-admin view scoped to the teacher's own classes:
 * register students into their classes, author questions, run teaching slides,
 * check progress. Nothing here lets a teacher touch another teacher's roster —
 * that scoping lives in AuthRepository.registerStudent, not just this screen.
 */
@Composable
fun TeacherDashboardScreen(
    overview: TeacherOverview,
    onRegisterStudent: () -> Unit,
    onAddEditQuestions: () -> Unit,
    onTeachingSlides: () -> Unit,
    onStudentProgress: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            BleedHeader(gradient = PurpleGradient) {
                Column {
                    Text(
                        "TEACHER",
                        style = display(TextSize.Micro, FontWeight.Bold),
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Text(
                        "Hi ${overview.teacherName} 👋",
                        style = display(TextSize.Screen, FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
            }
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatBox(overview.myStudentCount.toString(), "My Students", AppColors.VioletBg, AppColors.Violet, Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    StatBox("${overview.avgProgressPct}%", "Avg Progress", AppColors.GreenBg, com.gumthala.learningapp.ui.theme.AppColors2.OnGreenBox, Modifier.weight(1f))
                }
                Spacer(Modifier.height(4.dp))

                ActionRow("👧", AppColors.VioletBg, "Register New Student", "Add to your class", onRegisterStudent)
                ActionRow("✏️", AppColors.PinkBg, "Add / Edit Questions", "Maths, English, Marathi, Hindi", onAddEditQuestions)
                ActionRow("🖼️", AppColors.YellowBg, "Teaching Slides", "A–Z, Tables, Barakhadi", onTeachingSlides)
                ActionRow("📈", AppColors.GreenBg, "Student Progress", "View all my students", onStudentProgress)
            }
        }
    }
}
