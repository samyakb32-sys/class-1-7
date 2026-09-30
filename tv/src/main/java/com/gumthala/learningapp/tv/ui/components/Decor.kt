package com.gumthala.learningapp.tv.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.tv.ui.theme.Tv
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** The deep purple sky every screen sits on, with a few soft shapes so it isn't flat. */
fun Modifier.tvBackground(): Modifier = this
    .background(Brush.verticalGradient(listOf(Tv.PurpleDark, Tv.Purple)))
    .drawBehind {
        val w = size.width
        val h = size.height
        drawCircle(Color(0x14FFFFFF), h * 0.55f, Offset(w * 0.08f, h * 0.95f))
        drawCircle(Color(0x10FFFFFF), h * 0.42f, Offset(w * 0.95f, h * 0.05f))
        drawCircle(Color(0x0CFFFFFF), h * 0.25f, Offset(w * 0.72f, h * 0.88f))
    }

/** A five-point star path centred at [c] with outer radius [r]. */
fun starPath(c: Offset, r: Float): Path {
    val p = Path()
    val inner = r * 0.48f
    for (i in 0 until 10) {
        val rad = if (i % 2 == 0) r else inner
        val a = (-90f + i * 36f) * PI.toFloat() / 180f
        val x = c.x + cos(a) * rad
        val y = c.y + sin(a) * rad
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close()
    return p
}

@Composable
fun StarIcon(filled: Boolean, size: Dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val r = this.size.minDimension / 2f * 0.92f
        val path = starPath(Offset(this.size.width / 2f, this.size.height / 2f + r * 0.06f), r)
        drawPath(path, if (filled) Tv.Sun else Color(0xFFD9D6EE))
        drawPath(path, if (filled) Tv.SunDeep else Color(0xFFB9B5D9), style = Stroke(r * 0.09f))
    }
}

/** 0..[max] gold stars. [popIn] makes them bounce in one after another (for the result screen). */
@Composable
fun StarBar(stars: Int, size: Dp, modifier: Modifier = Modifier, max: Int = 3, popIn: Boolean = false) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(size * 0.12f), verticalAlignment = Alignment.CenterVertically) {
        for (i in 0 until max) {
            val filled = i < stars
            if (popIn && filled) {
                val scale = remember { Animatable(0f) }
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(300L + i * 380L)
                    scale.animateTo(1f, spring(dampingRatio = 0.35f, stiffness = 260f))
                }
                StarIcon(true, size, Modifier.graphicsLayer { scaleX = scale.value; scaleY = scale.value })
            } else {
                StarIcon(filled, size)
            }
        }
    }
}

private class Piece(val x: Float, val y0: Float, val speed: Float, val sway: Float, val phase: Float, val spin: Float, val color: Color, val w: Float, val h: Float)

/** Falling confetti. Positions come from one looping value, so it costs almost nothing per frame. */
@Composable
fun Confetti(modifier: Modifier = Modifier, count: Int = 60) {
    val colors = listOf(Tv.Sun, Tv.Pink, Tv.Green, Tv.Blue, Tv.Orange, Tv.Violet, Color.White)
    val pieces = remember {
        val r = Random(11)
        List(count) {
            Piece(r.nextFloat(), r.nextFloat(), 0.5f + r.nextFloat() * 0.9f, 10f + r.nextFloat() * 30f, r.nextFloat() * 6.28f,
                (r.nextFloat() - 0.5f) * 3f, colors[r.nextInt(colors.size)], 8f + r.nextFloat() * 10f, 14f + r.nextFloat() * 10f)
        }
    }
    val t by rememberInfiniteTransition().animateFloat(0f, 1f, infiniteRepeatable(tween(5200, easing = LinearEasing)))
    Canvas(modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        for (p in pieces) {
            val y = (((p.y0 + t * p.speed) % 1.15f) - 0.08f) * h
            val x = p.x * w + sin(t * 6.28f * 2f + p.phase) * p.sway
            rotate(t * 360f * p.spin, Offset(x, y)) {
                drawRect(p.color, Offset(x - p.w / 2, y - p.h / 2), Size(p.w, p.h))
            }
        }
    }
}

/** Small "press this key" chips along the bottom so nobody has to guess how the remote works. */
@Composable
fun KeyHints(vararg hints: Pair<String, String>, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        for ((key, what) in hints) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(Modifier.background(Color(0x33FFFFFF), RoundedCornerShape(8.dp)).padding(horizontal = 10.dp, vertical = 3.dp)) {
                    BasicText(key, style = Tv.text(15.sp, Color.White))
                }
                BasicText(what, style = Tv.text(15.sp, Color(0xCCFFFFFF)))
            }
        }
    }
}

/** A soft white rounded card: the "paper" the lesson is drawn on. */
fun Modifier.paper(radius: Dp = 28.dp): Modifier = this
    .background(Tv.Paper, RoundedCornerShape(radius))
