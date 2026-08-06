package com.gumthala.learningapp.ui.screens.admin

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
import com.gumthala.learningapp.core.SupportContact
import com.gumthala.learningapp.ui.components.BleedHeader
import com.gumthala.learningapp.ui.components.PurpleGradient
import com.gumthala.learningapp.ui.screens.teacher.ActionRow
import com.gumthala.learningapp.ui.screens.teacher.StatBox
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.AppColors2
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.display

data class AdminOverview(
    val adminName: String,
    val studentCount: Int,
    val teacherCount: Int,
    val avgProgressPct: Int,
    val questionCount: Int
)

/** `Admin Dashboard` — school-wide stats plus the four management entry points. */
@Composable
fun AdminDashboardScreen(
    overview: AdminOverview,
    onManageStudents: () -> Unit,
    onManageTeachers: () -> Unit,
    onManageContent: () -> Unit,
    onHelpAndSupport: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item {
            BleedHeader(gradient = PurpleGradient) {
                Column {
                    Text(
                        "ADMIN",
                        style = display(TextSize.Micro, FontWeight.Bold),
                        color = Color.White.copy(alpha = 0.75f)
                    )
                    Text(
                        "Good morning, ${overview.adminName} 👋",
                        style = display(TextSize.Screen, FontWeight.ExtraBold),
                        color = Color.White
                    )
                }
            }
        }
        item {
            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatBox(overview.studentCount.toString(), "Students", AppColors.VioletBg, AppColors.Violet, Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    StatBox(overview.teacherCount.toString(), "Teachers", AppColors.PinkBg, AppColors2.OnPinkBox, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    StatBox("${overview.avgProgressPct}%", "Avg Progress", AppColors.GreenBg, AppColors2.OnGreenBox, Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    StatBox(overview.questionCount.toString(), "Questions", AppColors.YellowBg, AppColors2.OnYellowBox, Modifier.weight(1f))
                }
                Spacer(Modifier.height(4.dp))

                ActionRow("👧", AppColors.VioletBg, "Manage Students", "Register, edit, remove", onManageStudents)
                ActionRow("🍎", AppColors.PinkBg, "Manage Teachers", "Assign classes, permissions", onManageTeachers)
                ActionRow("📚", AppColors.YellowBg, "Manage Content", "Subjects, chapters, questions", onManageContent)
                ActionRow("💬", AppColors.GreenBg, "Help & Support", SupportContact.EMAIL, onHelpAndSupport)
            }
        }
    }
}
