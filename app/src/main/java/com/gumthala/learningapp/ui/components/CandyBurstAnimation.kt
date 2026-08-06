package com.gumthala.learningapp.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import com.gumthala.learningapp.ui.theme.Green
import com.gumthala.learningapp.ui.theme.Pink
import com.gumthala.learningapp.ui.theme.Purple
import com.gumthala.learningapp.ui.theme.Violet
import com.gumthala.learningapp.ui.theme.Yellow
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class Confetto(
    val angleDegrees: Float,
    val distanceFraction: Float,
    val sizeDp: Float,
    val color: Color,
    val isCircle: Boolean,
    val spinSpeed: Float
)

/**
 * Original "Candy Burst" celebration: a ring of colored shapes (not candy-shaped, not a
 * Candy Crush reference) bursts outward from the center and fades as it flies out —
 * plays once and calls [onFinished].
 */
@Composable
fun CandyBurst(modifier: Modifier = Modifier, onFinished: () -> Unit = {}) {
    val palette = listOf(Purple, Pink, Yellow, Green, Violet)
    val confetti = remember {
        val random = Random(System.nanoTime())
        List(36) {
            Confetto(
                angleDegrees = random.nextFloat() * 360f,
                distanceFraction = 0.55f + random.nextFloat() * 0.45f,
                sizeDp = 6f + random.nextFloat() * 8f,
                color = palette.random(random),
                isCircle = random.nextBoolean(),
                spinSpeed = (random.nextFloat() - 0.5f) * 6f
            )
        }
    }
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(durationMillis = 1100, easing = LinearOutSlowInEasing))
        onFinished()
    }
    Canvas(modifier = modifier.fillMaxSize()) {
        val p = progress.value
        val center = Offset(size.width / 2f, size.height / 2f)
        val maxRadius = size.minDimension / 2f
        val alpha = (1f - p).coerceIn(0f, 1f)
        confetti.forEach { c ->
            val radius = maxRadius * c.distanceFraction * p
            val angleRad = Math.toRadians(c.angleDegrees.toDouble())
            val x = center.x + (cos(angleRad) * radius).toFloat()
            val y = center.y + (sin(angleRad) * radius).toFloat() - (maxRadius * 0.35f * p * p) // slight upward arc
            val dotColor = c.color.copy(alpha = alpha)
            val sizePx = c.sizeDp * density
            if (c.isCircle) {
                drawCircle(color = dotColor, radius = sizePx / 2f, center = Offset(x, y))
            } else {
                rotate(degrees = c.angleDegrees * c.spinSpeed * p, pivot = Offset(x, y)) {
                    drawRect(
                        color = dotColor,
                        topLeft = Offset(x - sizePx / 2f, y - sizePx / 2f),
                        size = androidx.compose.ui.geometry.Size(sizePx, sizePx)
                    )
                }
            }
        }
    }
}
