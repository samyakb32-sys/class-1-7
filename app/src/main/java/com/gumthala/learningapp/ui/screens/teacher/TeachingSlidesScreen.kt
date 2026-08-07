package com.gumthala.learningapp.ui.screens.teacher

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.ui.components.PurpleGradient
import com.gumthala.learningapp.ui.theme.AppColors
import com.gumthala.learningapp.ui.theme.Radius
import com.gumthala.learningapp.ui.theme.TextSize
import com.gumthala.learningapp.ui.theme.body
import com.gumthala.learningapp.ui.theme.display

data class TeachingSlide(val headline: String, val caption: String)

/**
 * `Teaching Slides` — arrow-navigable deck viewer. Works for both the default
 * decks (A–Z, tables 1–10, barakhadi) seeded by [com.gumthala.learningapp.data.seed.DefaultSlides]
 * and a teacher's custom deck; only the "＋" (add slide) affordance is
 * meaningful on a teacher's own deck.
 */
@Composable
fun TeachingSlidesScreen(
    deckTitle: String,
    slides: List<TeachingSlide>,
    currentIndex: Int,
    onBack: () -> Unit,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onAddSlide: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    val slide = slides.getOrNull(currentIndex)

    Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("‹", style = display(TextSize.Header, FontWeight.Bold), color = AppColors.Ink,
                modifier = Modifier.clickable(onClick = onBack))
            Text(
                deckTitle,
                style = display(TextSize.Header, FontWeight.ExtraBold),
                color = AppColors.Ink,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            if (onAddSlide != null) {
                Text("＋", style = display(TextSize.Header, FontWeight.Bold), color = AppColors.Ink,
                    modifier = Modifier.clickable(onClick = onAddSlide))
            } else {
                androidx.compose.foundation.layout.Spacer(Modifier.size(20.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .clip(RoundedCornerShape(Radius.Large))
                .background(PurpleGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                slide?.headline.orEmpty(),
                style = display(48.sp, FontWeight.ExtraBold),
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(16.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavCircle("‹", enabled = currentIndex > 0, onClick = onPrevious)
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                slides.indices.forEach { index ->
                    SlideDot(active = index == currentIndex)
                }
            }
            NavCircle("›", enabled = currentIndex < slides.lastIndex, onClick = onNext)
        }

        if (slide != null) {
            Text(
                "Slide ${currentIndex + 1} of ${slides.size} · ${slide.caption}",
                style = display(TextSize.Small, FontWeight.Bold),
                color = AppColors.Muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun NavCircle(glyph: String, enabled: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(AppColors.SurfaceSoft)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            glyph,
            style = display(TextSize.Label, FontWeight.Bold),
            color = if (enabled) AppColors.Purple else AppColors.TrackIdle
        )
    }
}

@Composable
private fun SlideDot(active: Boolean) {
    Box(
        modifier = Modifier
            .height(6.dp)
            .width(if (active) 16.dp else 6.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (active) AppColors.Purple else AppColors.TrackIdle)
    )
}
