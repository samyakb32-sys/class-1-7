package com.gumthala.learningapp.tv.ui.visuals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.tv.content.*
import com.gumthala.learningapp.tv.ui.components.AutoText
import com.gumthala.learningapp.tv.ui.theme.Tv
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
private fun StackedFraction(n: Int, d: Int, size: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        BasicText("$n", style = Tv.text(size.sp, Tv.Pink, FontWeight.ExtraBold))
        Box(Modifier.width((size * 1.1f).dp).height((size * 0.08f).dp).background(Tv.Ink))
        BasicText("$d", style = Tv.text(size.sp, Tv.Blue, FontWeight.ExtraBold))
    }
}

@Composable
internal fun PieView(v: Pie, p: Int) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val d = min(maxWidth.value * (if (v.label) 0.55f else 0.9f), maxHeight.value * 0.94f)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(28.dp)) {
            Canvas(Modifier.size(d.dp)) {
                val s = size.minDimension
                val slice = 360f / v.parts
                val inset = s * 0.03f
                for (i in 0 until v.parts) {
                    val on = i < p
                    drawArc(if (on) Tv.Pink else Tv.PaperSoft, -90f + i * slice, slice, true, Offset(inset, inset), Size(s - 2 * inset, s - 2 * inset))
                }
                for (i in 0 until v.parts) {
                    if (v.parts == 1) break
                    val a = Math.toRadians((-90f + i * slice).toDouble())
                    drawLine(Tv.Purple, Offset(s / 2f, s / 2f), Offset(s / 2f + cos(a).toFloat() * (s / 2f - inset), s / 2f + sin(a).toFloat() * (s / 2f - inset)), s * 0.014f, StrokeCap.Round)
                }
                drawCircle(Tv.Purple, s / 2f - inset, Offset(s / 2f, s / 2f), style = Stroke(s * 0.022f))
            }
            if (v.label) StackedFraction(v.filled, v.parts, min(72f, d * 0.3f))
        }
    }
}

@Composable
internal fun FracBarsView(v: FracBars, p: Int) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val n = v.bars.size
        val boxW = maxWidth
        val barH = min(64f, maxHeight.value / (n * 1.35f)).dp
        val labelW = if (v.labels.isNotEmpty()) 120.dp else 0.dp
        Column(verticalArrangement = Arrangement.spacedBy(barH * 0.3f), horizontalAlignment = Alignment.CenterHorizontally) {
            v.bars.forEachIndexed { i, (parts, filled) ->
                val a by animateFloatAsState(if (i < p) 1f else 0f, label = "bar")
                Row(Modifier.graphicsLayer { alpha = a }, verticalAlignment = Alignment.CenterVertically) {
                    if (v.labels.isNotEmpty()) {
                        Box(Modifier.width(labelW), contentAlignment = Alignment.CenterEnd) {
                            AutoText(v.labels.getOrElse(i) { "" }, Modifier.padding(end = 14.dp), maxSize = 34.sp, color = Tv.Purple, weight = FontWeight.ExtraBold)
                        }
                    }
                    Row(Modifier.width(boxW - labelW - 30.dp).height(barH)) {
                        for (k in 0 until parts) {
                            Box(
                                Modifier.weight(1f).fillMaxSize().padding(horizontal = 2.dp)
                                    .background(if (k < filled) Tv.Pink else Tv.PaperSoft, androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                            ) {
                                if (k >= filled) Box(Modifier.fillMaxSize().background(Color.Transparent))
                            }
                        }
                    }
                }
            }
        }
    }
}

private val barColors = listOf(Tv.Blue, Tv.Pink, Tv.Green, Tv.Orange, Tv.Violet, Tv.Teal, Tv.Red)

@Composable
internal fun BarsView(v: Bars, p: Int) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        val density = LocalDensity.current.density
        val n = v.values.size
        val maxV = max(v.values.max(), 1)
        // round the axis top up to a friendly number
        val top = when {
            maxV <= 5 -> 5
            maxV <= 10 -> 10
            else -> ((maxV + 9) / 10) * 10
        }
        val left = 54f * density
        val bottom = hPx - 52f * density
        val topY = 26f * density
        val slot = (wPx - left - 10f * density) / n
        val barW = slot * 0.6f
        Canvas(Modifier.fillMaxSize()) {
            val ticks = 5
            for (i in 0..ticks) {
                val y = bottom - (bottom - topY) * i / ticks
                drawLine(Tv.Line, Offset(left, y), Offset(wPx - 8f * density, y), 2f * density)
            }
            drawLine(Tv.Ink, Offset(left, topY), Offset(left, bottom), 4f * density, StrokeCap.Round)
            drawLine(Tv.Ink, Offset(left, bottom), Offset(wPx - 8f * density, bottom), 4f * density, StrokeCap.Round)
            for (i in 0 until min(p, n)) {
                val h = (bottom - topY) * v.values[i] / top
                val x = left + slot * i + (slot - barW) / 2f
                val c = if (i == v.highlight) Tv.SunDeep else barColors[i % barColors.size]
                drawRoundRect(c, Offset(x, bottom - h), Size(barW, h), CornerRadius(10f * density))
            }
        }
        for (i in 0..5) {
            val y = bottom - (bottom - topY) * i / 5
            PlaceAt(left - 22f * density, y) { BasicText("${top * i / 5}", style = Tv.text(18.sp, Tv.Muted)) }
        }
        for (i in 0 until n) {
            PlaceAt(left + slot * i + slot / 2f, bottom + 24f * density) {
                AutoText(v.labels[i], Modifier.width((slot / density).dp), maxSize = 22.sp, minSize = 10.sp, color = Tv.Ink)
            }
        }
        for (i in 0 until min(p, n)) {
            val h = (bottom - topY) * v.values[i] / top
            PlaceAt(left + slot * i + slot / 2f, bottom - h - 18f * density) {
                BasicText("${v.values[i]}${if (v.unit.isNotEmpty()) " ${v.unit}" else ""}", style = Tv.text(22.sp, Tv.Ink, FontWeight.ExtraBold))
            }
        }
    }
}
