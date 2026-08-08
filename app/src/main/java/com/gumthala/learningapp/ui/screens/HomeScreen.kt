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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.R
import com.gumthala.learningapp.ui.components.EmojiTile
import com.gumthala.learningapp.ui.components.SectionHeader
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.FeaturedPalette
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.ScreenPadding
import com.gumthala.learningapp.ui.theme.SubjectPalette
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

data class HomeStat(val value: String, val label: String)
data class FeaturedSubject(val emoji: String, val name: String, val palette: SubjectPalette)

data class HomeUiState(
    val greeting: String = "Good Morning,",
    val name: String = "Student!",
    val showCrown: Boolean = true,
    val stats: List<HomeStat> = listOf(
        HomeStat("🔥12", "Learning Streak"),
        HomeStat("⭐1,250", "XP Points"),
        HomeStat("🪙320", "Coins")
    ),
    val featured: List<FeaturedSubject> = listOf(
        FeaturedSubject("🔢", "Maths", FeaturedPalette.One),
        FeaturedSubject("📖", "English", FeaturedPalette.Two),
        FeaturedSubject("✍️", "Marathi", FeaturedPalette.Three),
        FeaturedSubject("अ", "Hindi", FeaturedPalette.Four)
    ),
    val challengeTitle: String = "Daily Challenge",
    val challengeSubtitle: String = "Solve 5 Maths Qs · +50 XP",
    val weeklyProgress: String = "78%",
    val upcomingQuiz: String = "Maths"
)

@Composable
fun HomeScreen(
    state: HomeUiState = HomeUiState(),
    onSeeAllSubjects: () -> Unit = {},
    onFeaturedClick: (FeaturedSubject) -> Unit = {},
    onStartChallenge: () -> Unit = {},
    onContinueLearning: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = ScreenPadding)
    ) {
        GreetingRow(state)

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            state.stats.forEach { stat -> StatChip(stat, Modifier.weight(1f)) }
        }

        // `.banner` — the artwork carries its own headline and CTA, so it is the card.
        Image(
            painter = painterResource(R.drawable.img_banner_keep_learning),
            contentDescription = "Keep learning, keep growing",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(Radius.Banner))
                .clickable(onClick = onContinueLearning)
        )

        Spacer(Modifier.height(20.dp))

        SectionHeader("Featured Subjects", action = "See All", onActionClick = onSeeAllSubjects)

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            state.featured.forEach { subject ->
                FeaturedTile(subject, Modifier.weight(1f)) { onFeaturedClick(subject) }
            }
        }

        ChallengeCard(state.challengeTitle, state.challengeSubtitle, onStartChallenge)

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MiniCard("Weekly Progress", state.weeklyProgress, Modifier.weight(1f))
            MiniCard("Upcoming Quiz", state.upcomingQuiz, Modifier.weight(1f))
        }
    }
}

@Composable
private fun GreetingRow(state: HomeUiState) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.img_avatar_student),
            contentDescription = "Your avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCEEFF))
        )
        Column(Modifier.weight(1f)) {
            Text(state.greeting, style = body(TextSize.Small, FontWeight.Bold), color = AppColors.Muted)
            Text(state.name, style = display(TextSize.Screen, FontWeight.Bold), color = AppColors.Ink)
        }
        if (state.showCrown) Text("👑", style = body(TextSize.Screen))
    }
}

@Composable
private fun StatChip(stat: HomeStat, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.Card))
            .background(AppColors.SurfaceSoft)
            .padding(vertical = 10.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stat.value, style = display(TextSize.Title, FontWeight.ExtraBold), color = AppColors.Ink)
        Text(
            stat.label,
            style = body(TextSize.Micro, FontWeight.Bold),
            color = AppColors.Muted,
            maxLines = 1
        )
    }
}

@Composable
private fun FeaturedTile(subject: FeaturedSubject, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.Soft))
            .background(subject.palette.background)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmojiTile(
            emoji = subject.emoji,
            background = subject.palette.iconTile,
            size = 40.dp,
            cornerRadius = 11.dp
        )
        Text(
            subject.name,
            style = display(TextSize.Small, FontWeight.Bold),
            color = subject.palette.foreground,
            modifier = Modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun ChallengeCard(title: String, subtitle: String, onStart: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Radius.Soft))
            .background(AppColors.YellowBg)
            .padding(horizontal = 15.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("🏆", style = body(TextSize.Display))
        Column(Modifier.weight(1f)) {
            Text(title, style = display(TextSize.Body, FontWeight.Bold), color = AppColors.ChallengeTitle)
            Text(subtitle, style = body(TextSize.Tiny, FontWeight.Bold), color = AppColors.ChallengeSub)
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(11.dp))
                .background(AppColors.Purple)
                .clickable(onClick = onStart)
                .padding(horizontal = 15.dp, vertical = 8.dp)
        ) {
            Text("Start", style = display(TextSize.Small, FontWeight.ExtraBold), color = Color.White)
        }
    }
    Spacer(Modifier.height(18.dp))
}

@Composable
private fun MiniCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(Radius.Soft))
            .background(AppColors.SurfaceSoft)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(label, style = body(TextSize.Micro, FontWeight.Bold), color = AppColors.Muted)
        Text(value, style = display(TextSize.Title, FontWeight.ExtraBold), color = AppColors.Ink)
    }
}
