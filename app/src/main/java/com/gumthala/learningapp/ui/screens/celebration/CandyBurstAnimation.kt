package com.gumthala.learningapp.ui.screens.celebration

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/**
 * "Candy Burst" — an original particle-burst celebration for quiz completion.
 * Deliberately NOT modeled on Candy Crush: no gem/candy sprites, no matching-
 * game visuals, just round confetti-like "sweets" in the app's own palette
 * radiating from the center and falling with gravity, drawn entirely with
 * Compose Canvas (no image assets, no third-party IP).
 *
 * [trigger] flips (e.g. true -> a new value) each time a burst should replay —
 * simplest is to key this composable on the trigger value from the caller.
 */
@Composable
fun CandyBurstAnimation(
    modifier: Modifier = Modifier,
    particleCount: Int = 42,
    colors: List<Color> = defaultCandyColors
) {
    val particles = remember {
        List(particleCount) { i ->
            val angle = Random.nextDouble(0.0, Math.PI) // upward-ish half circle
            CandyParticle(
                angleRad = angle,
                speed = Random.nextFloat() * 0.55f + 0.45f,
                size = Random.nextFloat() * 10f + 6f,
                color = colors[i % colors.size],
                spin = Random.nextFloat() * 720f - 360f
            )
        }
    }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { progress.animateTo(1f, animationSpec = tween(durationMillis = 1400, easing = LinearOutSlowInEasing)) }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val originX = size.width / 2f
        val originY = size.height * 0.38f
        val t = progress.value
        particles.forEach { p ->
            val travel = t * size.minDimension * 0.55f * p.speed
            val gravity = t * t * size.height * 0.35f
            val x = originX + cos(p.angleRad).toFloat() * travel
            val y = originY - sin(p.angleRad).toFloat() * travel + gravity
            val alpha = (1f - t).coerceIn(0f, 1f)
            drawCandy(Offset(x, y), p.size * (1f - 0.3f * t), p.color.copy(alpha = alpha))
        }
    }
}

private data class CandyParticle(
    val angleRad: Double,
    val speed: Float,
    val size: Float,
    val color: Color,
    val spin: Float
)

private fun DrawScope.drawCandy(center: Offset, radius: Float, color: Color) {
    drawCircle(color = color, radius = radius, center = center)
}

private val defaultCandyColors = listOf(
    Color(0xFF5B4FE8), Color(0xFFFF5C8A), Color(0xFF22C55E),
    Color(0xFFF5A623), Color(0xFF8B5CF6), Color(0xFFFB923C)
)
