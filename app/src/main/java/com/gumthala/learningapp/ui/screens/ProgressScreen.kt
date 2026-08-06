package com.gumthala.learningapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.ui.components.BleedHeader
import com.gumthala.learningapp.ui.components.PurpleGradient
import com.gumthala.learningapp.ui.components.SectionHeader
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.ScreenPadding
import com.gumthala.learningapp.ui.theme.SubjectPalette
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.WeeklyBarColors
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

data class SubjectScoreUi(val label: String, val percent: Int, val palette: SubjectPalette)

data class ProgressUiState(
    val overallPercent: Int = 78,
    val overallLabel: String = "Overall Progress",
    val overallSub: String = "124 / 160 Lessons",
    val subjectScores: List<SubjectScoreUi> = listOf(
        SubjectScoreUi("Maths", 85, SubjectPalette.Blue),
        SubjectScoreUi("English", 78, SubjectPalette.Pink),
        SubjectScoreUi("Science", 82, SubjectPalette.Green),
        SubjectScoreUi("EVS", 76, SubjectPalette.Yellow)
    ),
    /** Seven daily values, 0f..1f, matching the mockup's bar heights. */
    val weeklyBars: List<Float> = listOf(0.40f, 0.60f, 0.75f, 0.50f, 0.90f, 0.65f, 0.80f)
)

@Composable
fun ProgressScreen(
    state: ProgressUiState = ProgressUiState(),
    onSettings: () -> Unit = {},
    onSeeAllSubjects: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(Color.White)) {

        BleedHeader(gradient = PurpleGradient) {
            Text("📈 My Progress", style = display(TextSize.Header, FontWeight.Bold), color = Color.White)
            Text(
                "⚙️",
                style = body(TextSize.Body),
                modifier = Modifier.clickable(onClick = onSettings)
            )
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenPadding)
        ) {
            Spacer(Modifier.height(15.dp))

            // `.circle-wrap` with the conic-gradient `.ring`
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.Large))
                    .background(AppColors.SurfaceSoft)
                    .padding(17.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(17.dp)
            ) {
                ProgressRing(percent = state.overallPercent)
                Column {
                    Text(
                        state.overallLabel,
                        style = display(TextSize.Label, FontWeight.Bold),
                        color = AppColors.Ink
                    )
                    Text(
                        state.overallSub,
                        style = body(TextSize.Small, FontWeight.Bold),
                        color = AppColors.Muted
                    )
                }
            }

            Spacer(Modifier.height(15.dp))
            SectionHeader("Subject Performance", action = "See All", onActionClick = onSeeAllSubjects)

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.subjectScores.forEach { score ->
                    SubjectChip(score, Modifier.weight(1f))
                }
            }

            SectionHeader("Weekly Report")
            WeeklyBars(state.weeklyBars)
            Spacer(Modifier.height(20.dp))
        }
    }
}

/** `.ring` — 78% purple sweep over #E4E1FA, with a white hole and centred label. */
@Composable
private fun ProgressRing(percent: Int) {
    Box(contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(80.dp)) {
            drawArc(
                color = AppColors.TrackIdle,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = true,
                size = Size(size.width, size.height)
            )
            drawArc(
                color = AppColors.Purple,
                startAngle = -90f,
                sweepAngle = 360f * (percent / 100f),
                useCenter = true,
                size = Size(size.width, size.height)
            )
        }
        Box(
            modifier = Modifier.size(60.dp).clip(CircleShape).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "$percent%",
                style = display(TextSize.Label, FontWeight.ExtraBold),
                color = AppColors.Ink
            )
        }
    }
}

@Composable
private fun SubjectChip(score: SubjectScoreUi, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.Card))
            .background(score.palette.background)
            .padding(vertical = 11.dp, horizontal = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "${score.percent}%",
            style = display(TextSize.Body, FontWeight.ExtraBold),
            color = score.palette.foreground
        )
        Text(
            score.label,
            style = body(TextSize.Micro, FontWeight.Bold),
            color = score.palette.foreground
        )
    }
}

/** `.bars` — seven flex bars on a soft rounded tray. */
@Composable
private fun WeeklyBars(values: List<Float>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .clip(RoundedCornerShape(Radius.Card))
            .background(AppColors.SurfaceSoft)
            .padding(12.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        values.forEachIndexed { index, fraction ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(fraction.coerceIn(0f, 1f))
                    .clip(RoundedCornerShape(5.dp))
                    .background(WeeklyBarColors[index % WeeklyBarColors.size])
            )
        }
    }
}
