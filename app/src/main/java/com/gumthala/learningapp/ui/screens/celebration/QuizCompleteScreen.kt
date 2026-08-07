package com.gumthala.learningapp.ui.screens.celebration

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.domain.BadgeCode
import com.gumthala.learningapp.domain.CelebrationTier
import com.gumthala.learningapp.ui.components.PrimaryFullButton
import com.gumthala.learningapp.ui.components.PurpleGradient
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

/**
 * Original mascots per spec — no Dora/Masha/licensed characters, just an emoji
 * standing in for the app's four possible companions. Picked deterministically
 * from the chapter/attempt id so the same quiz doesn't reshuffle mascots on
 * every recomposition, but different chapters see variety.
 */
private val mascots = listOf("🐯", "🦉", "🐵", "🐻")

fun mascotFor(seed: String): String = mascots[(seed.hashCode().let { if (it < 0) -it else it }) % mascots.size]

private data class TierCopy(val headline: String, val sub: String)

private fun copyFor(tier: CelebrationTier): TierCopy = when (tier) {
    CelebrationTier.SPECTACULAR -> TierCopy("Incredible! 🌟", "You crushed every question!")
    CelebrationTier.GREAT -> TierCopy("Great job! 🎉", "You're really getting the hang of this.")
    CelebrationTier.GOOD -> TierCopy("Nice work! 👍", "A solid attempt — keep going.")
    CelebrationTier.ENCOURAGE -> TierCopy("Good try! 🌱", "Every attempt makes you stronger. Try again?")
}

private fun badgeLabel(code: BadgeCode): String = when (code) {
    BadgeCode.FIRST_ADVENTURE -> "🚀 First Adventure"
    BadgeCode.THREE_STAR -> "⭐ Perfect Stars"
    BadgeCode.FIVE_CHAPTERS -> "📚 5 Chapters Done"
    BadgeCode.TEN_CHAPTERS -> "🏆 10 Chapters Done"
    BadgeCode.TWENTY_FIVE_STARS -> "✨ 25 Stars"
    BadgeCode.FIFTY_STARS -> "💫 50 Stars"
    BadgeCode.SUBJECT_EXPLORER -> "🧭 Subject Explorer"
    BadgeCode.PERFECT_RUN -> "🎯 Perfect Run"
}

/**
 * Shown right after QuizViewModel.finishQuiz persists an attempt. [celebration]
 * drives both the copy and whether the Candy Burst plays at all — ENCOURAGE
 * (a rough attempt) skips the burst so a low score doesn't get an over-the-top
 * celebration, matching the "play, not pressure" tone from the spec.
 */
@Composable
fun QuizCompleteScreen(
    mascotSeed: String,
    correctCount: Int,
    totalCount: Int,
    starsEarned: Int,
    celebration: CelebrationTier,
    newBadges: List<BadgeCode>,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val copy = copyFor(celebration)
    val mascot = mascotFor(mascotSeed)

    Box(modifier = modifier.fillMaxSize().background(PurpleGradient)) {
        if (celebration != CelebrationTier.ENCOURAGE) {
            CandyBurstAnimation(modifier = Modifier.fillMaxSize())
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val bounce by animateFloatAsState(
                targetValue = 1f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
                label = "mascotBounce"
            )
            Text(
                mascot,
                style = display(56.sp),
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .then(Modifier.graphicsLayerScale(bounce))
            )

            Text(
                copy.headline,
                style = display(TextSize.Screen, FontWeight.ExtraBold),
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                copy.sub,
                style = body(TextSize.Small, FontWeight.SemiBold),
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                repeat(3) { index ->
                    Text(
                        if (index < starsEarned) "⭐" else "☆",
                        style = display(TextSize.Screen)
                    )
                }
            }

            Text(
                "$correctCount / $totalCount correct",
                style = body(TextSize.Body, FontWeight.Bold),
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp, bottom = if (newBadges.isEmpty()) 24.dp else 12.dp)
            )

            if (newBadges.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Radius.Soft))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "New badge${if (newBadges.size > 1) "s" else ""}!",
                        style = display(TextSize.Small, FontWeight.Bold),
                        color = Color.White
                    )
                    newBadges.forEach { badge ->
                        Text(
                            badgeLabel(badge),
                            style = body(TextSize.Small, FontWeight.SemiBold),
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
                androidx.compose.foundation.layout.Spacer(Modifier.size(20.dp))
            }

            PrimaryFullButton(text = "Continue", onClick = onContinue)
        }
    }
}

/** Scales a composable uniformly — small local helper so the mascot has a pop-in feel. */
private fun Modifier.graphicsLayerScale(scale: Float): Modifier = this.then(
    androidx.compose.ui.draw.scale(scale)
)
