package com.gumthala.learningapp.tv.ui.visuals

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.tv.content.*
import com.gumthala.learningapp.tv.ui.components.AutoText
import com.gumthala.learningapp.tv.ui.theme.Tv

private val groupA = Color(0xFFDCEBFF)
private val groupB = Color(0xFFFFE8D6)

@Composable
internal fun CountersView(v: Counters, p: Int) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (v.count == 0) {
            EmptyBox()
            return@BoxWithConstraints
        }
        val cols = when {
            v.cols > 0 -> v.cols
            v.count <= 5 -> v.count
            v.count <= 10 -> 5
            else -> 10
        }.coerceAtLeast(1)
        val rows = (v.count + cols - 1) / cols
        val showBadge = v.mode == CounterMode.COUNT && v.count <= 20
        val total = v.showTotal && v.mode == CounterMode.COUNT
        val reserve = (if (v.label != null) 30.dp else 0.dp) + (if (total) 56.dp else 0.dp) + (if (showBadge) 10.dp else 0.dp)
        val cell = minOf(maxWidth / cols, (maxHeight - reserve) / rows, 96.dp).coerceAtLeast(18.dp)

        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            for (r in 0 until rows) {
                Row(horizontalArrangement = Arrangement.Center) {
                    for (c in 0 until cols) {
                        val idx = r * cols + c
                        if (idx >= v.count) break
                        val counted = v.mode == CounterMode.COUNT && idx < p
                        val crossed = v.mode == CounterMode.CROSS_OUT && idx >= v.count - p.coerceAtMost(v.crossed) && p > 0
                        val tint = when {
                            v.split < 0 -> null
                            idx < v.split -> groupA
                            else -> groupB
                        }
                        CounterItem(v.emoji, cell, counted, if (showBadge) idx + 1 else null, crossed, tint, latest = counted && idx == p - 1)
                    }
                }
            }
            if (total) {
                val done = p >= v.count
                Box(Modifier.height(56.dp), contentAlignment = Alignment.Center) {
                    if (done) {
                        Box(
                            Modifier.size(50.dp).background(Tv.Green, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) { AutoText("${v.count}", Modifier.padding(4.dp), maxSize = 30.sp, color = Color.White) }
                    }
                }
            }
            if (v.label != null) BasicText(v.label, style = Tv.text(22.sp, Tv.Muted))
        }
    }
}

@Composable
private fun CounterItem(emoji: String, size: Dp, counted: Boolean, number: Int?, crossed: Boolean, tint: Color?, latest: Boolean) {
    val pop = remember { Animatable(1f) }
    LaunchedEffect(counted, latest) {
        if (counted && latest) {
            pop.snapTo(1.4f)
            pop.animateTo(1f, spring(dampingRatio = 0.45f, stiffness = 380f))
        }
    }
    val pad = size * 0.06f
    Box(Modifier.size(size).padding(pad), contentAlignment = Alignment.Center) {
        val bg = when {
            counted -> Color(0xFFFFE08A)
            tint != null -> tint
            else -> Color.Transparent
        }
        Box(
            Modifier.fillMaxSize().graphicsLayer { scaleX = pop.value; scaleY = pop.value; alpha = if (crossed) 0.35f else 1f }
                .background(bg, RoundedCornerShape(size * 0.28f)),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(emoji, style = Tv.text((size.value * 0.6f).sp))
        }
        if (crossed) {
            Canvas(Modifier.fillMaxSize()) {
                val m = this.size.minDimension * 0.12f
                drawLine(Tv.Red, Offset(m, m), Offset(this.size.width - m, this.size.height - m), this.size.minDimension * 0.09f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                drawLine(Tv.Red, Offset(this.size.width - m, m), Offset(m, this.size.height - m), this.size.minDimension * 0.09f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
            }
        }
        if (number != null && counted) {
            Box(
                Modifier.align(Alignment.BottomCenter).size(size * 0.36f).background(Tv.Purple, CircleShape),
                contentAlignment = Alignment.Center,
            ) { AutoText("$number", Modifier.padding(1.dp), maxSize = (size.value * 0.24f).sp, minSize = 8.sp, color = Color.White) }
        }
    }
}

@Composable
private fun EmptyBox() {
    Box(
        Modifier.size(width = 240.dp, height = 120.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawRoundRect(
                Tv.Muted, cornerRadius = CornerRadius(28f, 28f),
                style = Stroke(5f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(22f, 16f))),
            )
        }
        BasicText("nothing here", style = Tv.text(24.sp, Tv.Muted))
    }
}

@Composable
internal fun TenFrameView(v: TenFrame, p: Int) {
    val density = LocalDensity.current
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val frames = v.frames.coerceIn(1, 2)
        val gap = 0.7f
        val cell = minOf(maxWidth / (5f * frames + gap * (frames - 1)), maxHeight / 2.2f, 78.dp)
        val cellPx = with(density) { cell.toPx() }
        val w = cell * (5 * frames + gap * (frames - 1))
        Box(Modifier.size(w, cell * 2)) {
            Canvas(Modifier.fillMaxSize()) {
                for (f in 0 until frames) {
                    val x0 = f * (5 + gap) * cellPx
                    for (i in 0 until 10) {
                        val col = i % 5
                        val row = i / 5
                        val topLeft = Offset(x0 + col * cellPx + cellPx * 0.04f, row * cellPx + cellPx * 0.04f)
                        drawRoundRect(Tv.PaperSoft, topLeft, Size(cellPx * 0.92f, cellPx * 0.92f), CornerRadius(cellPx * 0.14f))
                        drawRoundRect(Tv.Line, topLeft, Size(cellPx * 0.92f, cellPx * 0.92f), CornerRadius(cellPx * 0.14f), style = Stroke(cellPx * 0.05f))
                        val n = f * 10 + i
                        if (n < p && v.emoji == null) {
                            drawCircle(if (f == 0) Tv.Blue else Tv.Orange, cellPx * 0.32f, Offset(topLeft.x + cellPx * 0.46f, topLeft.y + cellPx * 0.46f))
                        }
                    }
                }
            }
            if (v.emoji != null) {
                for (n in 0 until minOf(p, frames * 10)) {
                    val f = n / 10
                    val i = n % 10
                    val x = (f * (5 + gap) + i % 5 + 0.5f) * cellPx
                    val y = (i / 5 + 0.5f) * cellPx
                    PlaceAt(x, y) { BasicText(v.emoji, style = Tv.text((cell.value * 0.6f).sp)) }
                }
            }
        }
    }
}

@Composable
internal fun ArrayView(v: ArrayGrid, p: Int) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val cell = minOf(maxWidth / (v.cols + 2.6f), maxHeight / v.rows, 76.dp).coerceAtLeast(16.dp)
        Column(verticalArrangement = Arrangement.Center) {
            for (r in 0 until v.rows) {
                val shown = r < p
                val a by animateFloatAsState(if (shown) 1f else 0f, label = "row")
                Row(Modifier.graphicsLayer { alpha = a }, verticalAlignment = Alignment.CenterVertically) {
                    for (c in 0 until v.cols) {
                        Box(Modifier.size(cell), contentAlignment = Alignment.Center) {
                            BasicText(v.emoji, style = Tv.text((cell.value * 0.62f).sp))
                        }
                    }
                    Spacer(Modifier.width(cell * 0.35f))
                    Box(Modifier.width(cell * 1.6f), contentAlignment = Alignment.CenterStart) {
                        BasicText("${(r + 1) * v.cols}", style = Tv.text((cell.value * 0.42f).sp, Tv.Purple))
                    }
                }
            }
        }
    }
}

private fun moneyColor(value: Int): Color = when (value) {
    1 -> Color(0xFFCBD5E1)
    2 -> Color(0xFFD6D3D1)
    5 -> Color(0xFFF5C451)
    10 -> Color(0xFFE6B655)
    20 -> Color(0xFFCDE08A)
    50 -> Color(0xFF8EDBE0)
    100 -> Color(0xFFC9B8F0)
    200 -> Color(0xFFFFC083)
    else -> Color(0xFFD6D3C4)
}

@Composable
internal fun MoneyView(v: Money, p: Int) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val perRow = if (v.items.size > 5) (v.items.size + 1) / 2 else v.items.size
        val rows = (v.items.size + perRow - 1) / perRow
        val unit = minOf(maxWidth / (perRow * 1.5f), maxHeight / (rows * 1.15f), 84.dp)
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(unit * 0.15f)) {
            for (r in 0 until rows) {
                Row(horizontalArrangement = Arrangement.spacedBy(unit * 0.2f), verticalAlignment = Alignment.CenterVertically) {
                    for (c in 0 until perRow) {
                        val i = r * perRow + c
                        if (i >= v.items.size) break
                        val value = v.items[i]
                        val a by animateFloatAsState(if (i < p) 1f else 0f, label = "coin")
                        val isNote = value >= 20
                        Box(Modifier.graphicsLayer { alpha = a; scaleX = 0.6f + 0.4f * a; scaleY = 0.6f + 0.4f * a }) {
                            if (isNote) {
                                Box(
                                    Modifier.size(unit * 1.55f, unit * 0.8f).background(moneyColor(value), RoundedCornerShape(unit * 0.1f))
                                        .border(unit * 0.04f, Color(0x55000000), RoundedCornerShape(unit * 0.1f)),
                                    contentAlignment = Alignment.Center,
                                ) { BasicText("₹$value", style = Tv.text((unit.value * 0.36f).sp, Tv.Ink)) }
                            } else {
                                Box(
                                    Modifier.size(unit).background(moneyColor(value), CircleShape).border(unit * 0.06f, Color(0x66000000), CircleShape),
                                    contentAlignment = Alignment.Center,
                                ) { BasicText("₹$value", style = Tv.text((unit.value * 0.36f).sp, Tv.Ink)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun BlocksView(v: Blocks, p: Int) {
    val density = LocalDensity.current
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Unit cube size so that every group fits side by side.
        val wideUnits = v.hundreds * 10.6f + (if (v.hundreds > 0) 1f else 0f) +
            v.tens * 1.5f + (if (v.tens > 0) 1.2f else 0f) + v.ones * 1.5f
        val u = minOf(maxWidth / (wideUnits.coerceAtLeast(3f) + 1f), (maxHeight - 40.dp) / 10.6f, 14.dp)
        val uPx = with(density) { u.toPx() }
        // Which group is revealed at which progress step.
        var step = 0
        val hStep = if (v.hundreds > 0) ++step else 0
        val tStep = if (v.tens > 0) ++step else 0
        val oStep = if (v.ones > 0) ++step else 0
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(u * 1.6f)) {
            if (v.hundreds > 0) BlockGroup(p >= hStep, "${v.hundreds} hundred${if (v.hundreds > 1) "s" else ""}", Tv.Blue) {
                Row(horizontalArrangement = Arrangement.spacedBy(u * 0.6f)) {
                    repeat(v.hundreds) { Canvas(Modifier.size(u * 10f)) { flat(uPx, Tv.Blue) } }
                }
            }
            if (v.tens > 0) BlockGroup(p >= tStep, "${v.tens} ten${if (v.tens > 1) "s" else ""}", Tv.Green) {
                Row(horizontalArrangement = Arrangement.spacedBy(u * 0.5f)) {
                    repeat(v.tens) { Canvas(Modifier.size(u, u * 10f)) { rod(uPx, Tv.Green) } }
                }
            }
            if (v.ones > 0) BlockGroup(p >= oStep, "${v.ones} one${if (v.ones > 1) "s" else ""}", Tv.Orange) {
                val perCol = 5
                Row(horizontalArrangement = Arrangement.spacedBy(u * 0.5f), verticalAlignment = Alignment.Bottom) {
                    var left = v.ones
                    while (left > 0) {
                        val n = minOf(perCol, left)
                        Column(verticalArrangement = Arrangement.spacedBy(u * 0.5f)) {
                            repeat(n) { Canvas(Modifier.size(u)) { drawRoundRect(Tv.Orange, cornerRadius = CornerRadius(uPx * 0.2f)) } }
                        }
                        left -= n
                    }
                }
            }
        }
    }
}

@Composable
private fun BlockGroup(show: Boolean, label: String, color: Color, content: @Composable () -> Unit) {
    val a by animateFloatAsState(if (show) 1f else 0f, label = "blk")
    Column(Modifier.graphicsLayer { alpha = a }, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomCenter) { content() }
        Spacer(Modifier.height(6.dp))
        BasicText(label, style = Tv.text(22.sp, color), maxLines = 1, softWrap = false)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.flat(u: Float, color: Color) {
    drawRoundRect(color.copy(alpha = 0.22f), cornerRadius = CornerRadius(u * 0.4f))
    for (i in 0..10) {
        drawLine(color, Offset(i * u, 0f), Offset(i * u, 10 * u), u * 0.08f)
        drawLine(color, Offset(0f, i * u), Offset(10 * u, i * u), u * 0.08f)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.rod(u: Float, color: Color) {
    drawRoundRect(color.copy(alpha = 0.3f), cornerRadius = CornerRadius(u * 0.3f))
    for (i in 0..10) drawLine(color, Offset(0f, i * u), Offset(u, i * u), u * 0.1f)
    drawRoundRect(color, cornerRadius = CornerRadius(u * 0.3f), style = Stroke(u * 0.12f))
}
