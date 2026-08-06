package com.gumthala.learningapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.R
import com.gumthala.learningapp.ui.components.BleedHeader
import com.gumthala.learningapp.ui.components.OptionGrid
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.ScreenPadding
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

data class LessonUiState(
    val subjectTitle: String = "Mathematics",
    val progress: Float = 0.40f,
    val heading: String = "Addition",
    val paragraph: String = "Addition means to find the total of two or more numbers.",
    val exampleLabel: String = "EXAMPLE",
    val example: String = "2 + 3 = 5 🍎🍎🍎🍎🍎",
    val tryItQuestion: String = "What is 4 + 6?",
    val tryItOptions: List<String> = listOf("8", "9", "10", "11"),
    val correctOptionIndex: Int = 2,
    val selectedOptionIndex: Int? = 2,
    val nextLabel: String = "Practice Now →"
)

@Composable
fun LessonScreen(
    state: LessonUiState = LessonUiState(),
    onBack: () -> Unit = {},
    onBookmark: () -> Unit = {},
    onReadAloud: () -> Unit = {},
    onSelectOption: (Int) -> Unit = {},
    onPrev: () -> Unit = {},
    onNext: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().background(Color.White)) {

        BleedHeader(
            solid = AppColors.Purple,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 20.dp, vertical = 14.dp
            )
        ) {
            Text(
                "‹",
                style = display(TextSize.Screen, FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.clickable(onClick = onBack)
            )
            Text(state.subjectTitle, style = display(TextSize.Header, FontWeight.Bold), color = Color.White)
            Text("🔖", style = body(TextSize.Body), modifier = Modifier.clickable(onClick = onBookmark))
        }

        // `.prog-track` — 5px rail, yellow fill, edge to edge.
        Box(
            Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(ProgressTrackOnPurple)
        ) {
            Box(
                Modifier
                    .fillMaxWidth(state.progress)
                    .height(6.dp)
                    .background(AppColors.Yellow)
            )
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(horizontal = ScreenPadding)
        ) {
            Spacer(Modifier.height(15.dp))

            Image(
                painter = painterResource(R.drawable.img_lesson_hero),
                contentDescription = "Lesson illustration",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(Radius.Banner))
                    .background(Color(0xFFEDE9FF))
            )

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    state.heading,
                    style = display(TextSize.Display, FontWeight.ExtraBold),
                    color = AppColors.Ink
                )
                ReadAloudButton(onReadAloud)
            }

            Spacer(Modifier.height(5.dp))

            Text(
                state.paragraph,
                style = body(TextSize.Body, FontWeight.SemiBold),
                color = AppColors.Muted,
                lineHeight = TextSize.Body * 1.45f
            )

            Spacer(Modifier.height(15.dp))
            ExampleBox(state.exampleLabel, state.example)
            Spacer(Modifier.height(18.dp))

            Text("Try It", style = display(TextSize.Label, FontWeight.ExtraBold), color = AppColors.Ink)
            Text(
                state.tryItQuestion,
                style = body(TextSize.Body, FontWeight.Bold),
                color = AppColors.Muted,
                modifier = Modifier.padding(top = 2.dp, bottom = 11.dp)
            )

            OptionGrid(
                options = state.tryItOptions,
                correctIndex = state.correctOptionIndex,
                selectedIndex = state.selectedOptionIndex,
                revealCorrect = true,
                showLetters = false,
                onSelect = onSelectOption
            )

            Spacer(Modifier.height(15.dp))
            NavRow(nextLabel = state.nextLabel, onPrev = onPrev, onNext = onNext)
            Spacer(Modifier.height(20.dp))
        }
    }
}

/** `rgba(255,255,255,0.3)` sitting on the purple header, flattened. */
private val ProgressTrackOnPurple = Color(0xFF8C84EF)

@Composable
private fun ReadAloudButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(Radius.Pill))
            .background(AppColors.VioletBg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text("🔊", style = body(TextSize.Small))
        Text("Read Aloud", style = display(TextSize.Small, FontWeight.ExtraBold), color = AppColors.Violet)
    }
}

@Composable
private fun ExampleBox(label: String, example: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.Soft))
            .background(AppColors.YellowBg)
            .height(IntrinsicSize.Min)
    ) {
        // `border-left:4px solid var(--yellow)`
        Box(Modifier.width(5.dp).fillMaxHeight().background(AppColors.Yellow))
        Column(Modifier.padding(horizontal = 16.dp, vertical = 13.dp)) {
            Text(
                label,
                style = body(TextSize.Tiny, FontWeight.ExtraBold).copy(letterSpacing = 0.8.sp),
                color = Color(0xFF946200)
            )
            Text(
                example,
                style = display(TextSize.Display, FontWeight.ExtraBold),
                color = AppColors.Ink,
                modifier = Modifier.padding(top = 3.dp)
            )
        }
    }
}

@Composable
private fun NavRow(nextLabel: String, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Radius.Cell))
                .background(AppColors.PrevButtonBg)
                .clickable(onClick = onPrev)
                .padding(horizontal = 15.dp, vertical = 11.dp)
        ) {
            Text("‹ Prev", style = display(TextSize.Small, FontWeight.Bold), color = AppColors.Muted)
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(Radius.Cell))
                .background(AppColors.Purple)
                .clickable(onClick = onNext)
                .padding(vertical = 11.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(nextLabel, style = display(TextSize.Small, FontWeight.Bold), color = Color.White)
        }
    }
}
