package com.gumthala.learningapp.tv.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.gumthala.learningapp.tv.content.Mood
import com.gumthala.learningapp.tv.ui.theme.Tv
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Sunny, the sun who teaches. Drawn, not an image: it costs no space, scales to any TV, and can
 * change face. Rays turn slowly and the face bobs, so the screen never feels frozen.
 */
@Composable
fun Sunny(mood: Mood, size: Dp, modifier: Modifier = Modifier, animated: Boolean = true) {
    val transition = rememberInfiniteTransition()
    val spin by transition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(24000, easing = LinearEasing)),
    )
    val bob by transition.animateFloat(
        initialValue = -1f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), RepeatMode.Reverse),
    )
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension
        val lift = if (animated) bob * s * 0.018f else 0f
        drawSun(s, if (animated) spin else 0f, lift, mood)
    }
}

private fun DrawScope.drawSun(s: Float, spin: Float, lift: Float, mood: Mood) {
    val c = Offset(s / 2f, s / 2f + lift)
    val r = s * 0.31f
    val ink = Color(0xFF3A2600)

    // rays
    val rays = 12
    for (i in 0 until rays) {
        val a = (spin + i * 360f / rays) * PI.toFloat() / 180f
        val r1 = r * 1.22f
        val r2 = r * (if (i % 2 == 0) 1.52f else 1.40f)
        drawLine(
            color = Tv.SunDeep,
            start = Offset(c.x + cos(a) * r1, c.y + sin(a) * r1),
            end = Offset(c.x + cos(a) * r2, c.y + sin(a) * r2),
            strokeWidth = s * 0.05f, cap = StrokeCap.Round,
        )
    }
    // face
    drawCircle(Brush.radialGradient(listOf(Color(0xFFFFE27A), Tv.Sun, Tv.SunDeep), center = Offset(c.x - r * 0.25f, c.y - r * 0.3f), radius = r * 1.5f), r, c)
    drawCircle(Tv.SunDeep, r, c, style = Stroke(width = s * 0.018f))
    // cheeks
    val cheek = Color(0x66FF5C8A)
    drawCircle(cheek, r * 0.17f, Offset(c.x - r * 0.58f, c.y + r * 0.22f))
    drawCircle(cheek, r * 0.17f, Offset(c.x + r * 0.58f, c.y + r * 0.22f))

    val ex = r * 0.36f
    val ey = c.y - r * 0.12f
    val eyeR = r * 0.105f
    val sw = s * 0.022f
    when (mood) {
        Mood.HAPPY -> {
            eye(Offset(c.x - ex, ey), eyeR, ink)
            eye(Offset(c.x + ex, ey), eyeR, ink)
            smile(c, r, ink, sw, 0.34f, 0.26f)
        }
        Mood.CHEER -> {
            happyEye(Offset(c.x - ex, ey), eyeR * 1.3f, ink, sw)
            happyEye(Offset(c.x + ex, ey), eyeR * 1.3f, ink, sw)
            val w = r * 0.56f
            val top = c.y + r * 0.08f
            drawArc(Color(0xFF7A1F1F), 0f, 180f, true, Offset(c.x - w / 2, top - w * 0.3f), Size(w, w * 0.78f))
            drawArc(Color(0xFFFF8FA3), 10f, 160f, true, Offset(c.x - w * 0.3f, top + w * 0.08f), Size(w * 0.6f, w * 0.42f))
            // sparkles
            sparkle(Offset(c.x + r * 1.1f, c.y - r * 0.85f), s * 0.05f)
            sparkle(Offset(c.x - r * 1.15f, c.y - r * 0.55f), s * 0.035f)
        }
        Mood.THINK -> {
            eye(Offset(c.x - ex + eyeR * 0.5f, ey - eyeR * 0.5f), eyeR, ink)
            eye(Offset(c.x + ex + eyeR * 0.5f, ey - eyeR * 0.5f), eyeR, ink)
            drawLine(ink, Offset(c.x + ex - eyeR * 1.3f, ey - eyeR * 2.6f), Offset(c.x + ex + eyeR * 1.6f, ey - eyeR * 3.2f), sw, StrokeCap.Round)
            drawCircle(ink, r * 0.07f, Offset(c.x + r * 0.12f, c.y + r * 0.42f))
        }
        Mood.OOPS -> {
            eye(Offset(c.x - ex, ey), eyeR, ink)
            eye(Offset(c.x + ex, ey), eyeR, ink)
            drawLine(ink, Offset(c.x - ex - eyeR * 1.6f, ey - eyeR * 2.6f), Offset(c.x - ex + eyeR * 1.2f, ey - eyeR * 3.4f), sw, StrokeCap.Round)
            drawLine(ink, Offset(c.x + ex + eyeR * 1.6f, ey - eyeR * 2.6f), Offset(c.x + ex - eyeR * 1.2f, ey - eyeR * 3.4f), sw, StrokeCap.Round)
            drawArc(ink, 200f, 140f, false, Offset(c.x - r * 0.26f, c.y + r * 0.36f), Size(r * 0.52f, r * 0.36f), style = Stroke(sw, cap = StrokeCap.Round))
            // a little sweat drop
            drawCircle(Color(0xFF7DD3FC), r * 0.09f, Offset(c.x + r * 0.95f, c.y - r * 0.2f))
        }
    }
}

private fun DrawScope.eye(at: Offset, radius: Float, ink: Color) {
    drawCircle(ink, radius, at)
    drawCircle(Color.White, radius * 0.38f, Offset(at.x - radius * 0.3f, at.y - radius * 0.35f))
}

private fun DrawScope.happyEye(at: Offset, radius: Float, ink: Color, sw: Float) {
    drawArc(ink, 200f, 140f, false, Offset(at.x - radius, at.y - radius * 0.6f), Size(radius * 2, radius * 1.4f), style = Stroke(sw * 1.2f, cap = StrokeCap.Round))
}

private fun DrawScope.smile(c: Offset, r: Float, ink: Color, sw: Float, wFrac: Float, hFrac: Float) {
    drawArc(ink, 25f, 130f, false, Offset(c.x - r * wFrac, c.y + r * 0.04f), Size(r * wFrac * 2, r * hFrac * 2), style = Stroke(sw, cap = StrokeCap.Round))
}

private fun DrawScope.sparkle(at: Offset, size: Float) {
    val col = Color.White
    drawLine(col, Offset(at.x - size, at.y), Offset(at.x + size, at.y), size * 0.4f, StrokeCap.Round)
    drawLine(col, Offset(at.x, at.y - size), Offset(at.x, at.y + size), size * 0.4f, StrokeCap.Round)
}
