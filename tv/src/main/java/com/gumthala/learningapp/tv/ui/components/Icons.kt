package com.gumthala.learningapp.tv.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/*
 * Icons are drawn, not typed. Characters like the play triangle are rendered by whatever font a
 * given TV has (sometimes as a coloured emoji, sometimes a thin outline, sometimes a blank box).
 */

@Composable
fun PlayIcon(color: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension
        val p = Path().apply {
            moveTo(s * 0.22f, s * 0.12f); lineTo(s * 0.88f, s * 0.5f); lineTo(s * 0.22f, s * 0.88f); close()
        }
        drawPath(p, color)
        drawPath(p, color, style = Stroke(s * 0.12f, join = StrokeJoin.Round))
    }
}

@Composable
fun ChevronIcon(color: Color, size: Dp = 24.dp, pointLeft: Boolean = true, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension
        val w = s * 0.14f
        val x1 = if (pointLeft) s * 0.62f else s * 0.38f
        val x2 = if (pointLeft) s * 0.36f else s * 0.64f
        drawLine(color, Offset(x1, s * 0.2f), Offset(x2, s * 0.5f), w, StrokeCap.Round)
        drawLine(color, Offset(x2, s * 0.5f), Offset(x1, s * 0.8f), w, StrokeCap.Round)
    }
}

@Composable
fun CheckIcon(color: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension
        val w = s * 0.14f
        drawLine(color, Offset(s * 0.2f, s * 0.54f), Offset(s * 0.42f, s * 0.76f), w, StrokeCap.Round)
        drawLine(color, Offset(s * 0.42f, s * 0.76f), Offset(s * 0.82f, s * 0.26f), w, StrokeCap.Round)
    }
}

@Composable
fun CrossIcon(color: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension
        val w = s * 0.14f
        drawLine(color, Offset(s * 0.24f, s * 0.24f), Offset(s * 0.76f, s * 0.76f), w, StrokeCap.Round)
        drawLine(color, Offset(s * 0.76f, s * 0.24f), Offset(s * 0.24f, s * 0.76f), w, StrokeCap.Round)
    }
}

@Composable
fun ReplayIcon(color: Color, size: Dp = 24.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(size)) {
        val s = this.size.minDimension
        val w = s * 0.12f
        drawArc(color, 40f, 280f, false, Offset(s * 0.16f, s * 0.16f), androidx.compose.ui.geometry.Size(s * 0.68f, s * 0.68f), style = Stroke(w, cap = StrokeCap.Round))
        val p = Path().apply { moveTo(s * 0.62f, s * 0.08f); lineTo(s * 0.86f, s * 0.26f); lineTo(s * 0.56f, s * 0.36f); close() }
        drawPath(p, color)
    }
}
