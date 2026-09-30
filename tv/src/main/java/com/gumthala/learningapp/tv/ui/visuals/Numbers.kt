package com.gumthala.learningapp.tv.ui.visuals

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.tv.content.*
import com.gumthala.learningapp.tv.ui.components.AutoText
import com.gumthala.learningapp.tv.ui.theme.Tv
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.sqrt

private val operators = setOf("+", "−", "-", "×", "÷", "=", "<", ">", "≠", "x", "/", "(", ")", "or", "of", "→")
private val numberColors = listOf(Tv.Blue, Tv.Pink, Tv.Green, Tv.Orange, Tv.Violet)

@Composable
private fun QuestionBox(size: androidx.compose.ui.unit.TextUnit) {
    Box(
        Modifier.size((size.value * 1.05f).dp).background(Tv.Sun, RoundedCornerShape((size.value * 0.22f).dp)),
        contentAlignment = Alignment.Center,
    ) { BasicText("?", style = Tv.text((size.value * 0.78f).sp, Tv.Ink)) }
}

@Composable
internal fun EquationView(v: Equation, p: Int) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val em = v.tokens.sumOf { t -> if (t == "?") 1.15 else max(0.7, t.length * 0.6 + 0.2) }.toFloat() + 0.22f * (v.tokens.size - 1)
        val size = minOf(maxWidth.value / em, maxHeight.value * 0.82f, 88f).sp
        Row(horizontalArrangement = Arrangement.spacedBy((size.value * 0.2f).dp), verticalAlignment = Alignment.CenterVertically) {
            var numberIndex = 0
            v.tokens.forEachIndexed { i, t ->
                val isOp = t in operators
                val color = when {
                    isOp -> Tv.Purple
                    i > 0 && v.tokens[i - 1] == "=" -> Tv.Green
                    else -> numberColors[(numberIndex++) % numberColors.size]
                }
                val a by animateFloatAsState(if (i < p) 1f else 0f, label = "tok")
                Box(Modifier.graphicsLayer { alpha = a; scaleX = 0.7f + 0.3f * a; scaleY = 0.7f + 0.3f * a }) {
                    if (t == "?") QuestionBox(size) else BasicText(t, style = Tv.text(size, color, FontWeight.ExtraBold), maxLines = 1, softWrap = false)
                }
            }
        }
    }
}

@Composable
internal fun StripView(v: Strip, p: Int) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val n = v.items.size
        val gap = if (v.arrows) 0.55f else 0.22f
        val card = minOf(maxWidth / (n + gap * (n - 1)), maxHeight * 0.9f, 118.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            v.items.forEachIndexed { i, item ->
                if (i > 0) {
                    Box(Modifier.width(card * gap), contentAlignment = Alignment.Center) {
                        if (v.arrows) BasicText("›", style = Tv.text((card.value * 0.42f).sp, Tv.Purple))
                    }
                }
                val a by animateFloatAsState(if (i < p) 1f else 0f, label = "strip")
                val blank = i == v.blankAt || item == "?"
                Box(
                    Modifier.size(card).graphicsLayer { alpha = a; scaleX = 0.75f + 0.25f * a; scaleY = 0.75f + 0.25f * a }
                        .background(if (blank) Tv.Sun else Tv.PaperSoft, RoundedCornerShape(card * 0.22f))
                        .border(card * 0.03f, if (blank) Tv.SunDeep else Tv.Line, RoundedCornerShape(card * 0.22f)),
                    contentAlignment = Alignment.Center,
                ) {
                    val isEmoji = item.firstOrNull()?.let { !it.isLetterOrDigit() && it != '-' && it != '−' && it != '?' } == true
                    AutoText(
                        if (blank) "?" else item, Modifier.padding(card * 0.06f),
                        maxSize = (card.value * (if (isEmoji) 0.56f else 0.5f)).sp, minSize = 10.sp,
                        color = if (blank) Tv.Ink else Tv.Purple, weight = FontWeight.ExtraBold,
                    )
                }
            }
        }
    }
}

@Composable
internal fun NumberLineView(v: NumberLine, p: Int) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        val density = androidx.compose.ui.platform.LocalDensity.current.density
        val margin = 34f * density
        val span = (v.to - v.from).coerceAtLeast(1)
        val step = (wPx - 2 * margin) / span
        fun xOf(n: Int) = margin + (n - v.from) * step
        val lineY = hPx * 0.66f
        val start = v.start ?: v.from
        val pos = if (p == 0) start else v.hops[p - 1]
        val tickH = 14f * density
        val big = step > 26f * density
        Canvas(Modifier.fillMaxSize()) {
            drawLine(Tv.Ink, Offset(margin - 12 * density, lineY), Offset(wPx - margin + 12 * density, lineY), 5f * density, StrokeCap.Round)
            for (n in v.from..v.to) {
                val major = (n - v.from) % v.labelEvery == 0
                val h = if (major) tickH else tickH * 0.6f
                drawLine(Tv.Ink, Offset(xOf(n), lineY - h), Offset(xOf(n), lineY + h), (if (major) 4f else 2.5f) * density, StrokeCap.Round)
            }
            v.mark?.let { drawCircle(Tv.Sun, 20f * density, Offset(xOf(it), lineY), style = Stroke(6f * density)) }
            // the hops drawn so far
            var from = start
            for (i in 0 until p) {
                val to = v.hops[i]
                val x0 = xOf(from)
                val x1 = xOf(to)
                val ah = minOf(hPx * 0.3f, 22f * density + abs(x1 - x0) * 0.18f)
                val path = Path().apply {
                    moveTo(x0, lineY - tickH * 0.4f)
                    quadraticBezierTo((x0 + x1) / 2f, lineY - tickH * 0.4f - 2f * ah, x1, lineY - tickH * 0.4f)
                }
                drawPath(path, Tv.Pink, style = Stroke(5f * density, cap = StrokeCap.Round))
                // arrow head pointing along the end of the arc
                val dirX = (x1 - x0) / 2f
                val dirY = 2f * ah
                val len = sqrt(dirX * dirX + dirY * dirY).coerceAtLeast(1f)
                val ux = dirX / len
                val uy = dirY / len
                val hx = x1
                val hy = lineY - tickH * 0.4f
                val ah1 = 13f * density
                drawLine(Tv.Pink, Offset(hx, hy), Offset(hx - ux * ah1 - uy * ah1 * 0.6f, hy - uy * ah1 + ux * ah1 * 0.6f), 5f * density, StrokeCap.Round)
                drawLine(Tv.Pink, Offset(hx, hy), Offset(hx - ux * ah1 + uy * ah1 * 0.6f, hy - uy * ah1 - ux * ah1 * 0.6f), 5f * density, StrokeCap.Round)
                from = to
            }
            if (v.start != null) drawCircle(Tv.Green, 9f * density, Offset(xOf(start), lineY))
        }
        // number labels
        for (n in v.from..v.to) {
            if ((n - v.from) % v.labelEvery != 0) continue
            PlaceAt(xOf(n), lineY + tickH + 20f * density) {
                BasicText(if (n < 0) "−${-n}" else "$n", style = Tv.text((if (big) 22 else 17).sp, Tv.Ink))
            }
        }
        // "+3" style hop labels
        var from = start
        for (i in 0 until p) {
            val to = v.hops[i]
            val d = to - from
            if (abs(d) >= 2 || v.hops.size <= 5) {
                val ah = minOf(hPx * 0.3f, 22f * density + abs(xOf(to) - xOf(from)) * 0.18f)
                PlaceAt((xOf(from) + xOf(to)) / 2f, lineY - tickH * 0.4f - ah - 16f * density) {
                    BasicText(if (d >= 0) "+$d" else "−${-d}", style = Tv.text(22.sp, Tv.Pink, FontWeight.ExtraBold))
                }
            }
            from = to
        }
        // the frog sits on the number we are at
        if (v.start != null) {
            PlaceAt(xOf(pos), lineY - tickH - 34f * density) { BasicText("🐸", style = Tv.text(40.sp)) }
        }
    }
}

@Composable
internal fun BigTextView(v: BigText) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val hasEmoji = v.emoji != null
        val hasSub = v.sub != null
        val boxH = maxHeight.value
        val textMax = minOf(
            boxH * (if (hasEmoji || hasSub) 0.52f else 0.7f),
            if (v.text.length <= 2) 150f else if (v.text.length <= 6) 110f else 76f,
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            if (hasEmoji) BasicText(v.emoji!!, style = Tv.text(minOf(boxH * 0.3f, 76f).sp))
            AutoText(v.text, Modifier.fillMaxWidth().padding(horizontal = 12.dp), maxSize = textMax.sp, color = Tv.Purple, weight = FontWeight.ExtraBold)
            if (hasSub) AutoText(v.sub!!, Modifier.fillMaxWidth().padding(horizontal = 12.dp), maxSize = minOf(boxH * 0.16f, 34f).sp, color = Tv.Muted)
        }
    }
}

// Explicit line breaks + no soft wrap: the label then shrinks to fit instead of splitting mid-word.
private val placeNames = listOf("Ones", "Tens", "Hundreds", "Thousands", "Ten\nThousands", "Lakhs", "Ten\nLakhs", "Crores", "Ten\nCrores")

@Composable
internal fun PlaceChartView(v: PlaceChart, p: Int) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val s = v.number.toString()
        val n = s.length
        val col = minOf(maxWidth / n, 104.dp)
        val boxH = maxHeight.value
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AutoText(Words.indian(v.number), Modifier.width(col * n), maxSize = minOf(boxH * 0.26f, 64f).sp, color = Tv.Purple, weight = FontWeight.ExtraBold)
            Row {
                for (i in 0 until n) {
                    val place = n - 1 - i
                    val shown by animateFloatAsState(if (i < p) 1f else 0f, label = "place")
                    val tone = listOf(Tv.Blue, Tv.Pink, Tv.Green, Tv.Orange, Tv.Violet, Tv.Teal, Tv.Red, Tv.Blue, Tv.Pink)[place % 9]
                    Column(Modifier.width(col).padding(horizontal = 3.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(Modifier.height(40.dp), contentAlignment = Alignment.Center) {
                            AutoText(placeNames.getOrElse(place) { "" }, Modifier.fillMaxWidth(), maxSize = 17.sp, minSize = 9.sp, color = tone, maxLines = 2, softWrap = false)
                        }
                        Box(
                            Modifier.fillMaxWidth().height(col * 0.85f).background(tone.copy(alpha = 0.14f), RoundedCornerShape(14.dp))
                                .border(3.dp, tone, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            BasicText(s[i].toString(), Modifier.graphicsLayer { alpha = shown }, style = Tv.text((col.value * 0.6f).sp, tone, FontWeight.ExtraBold))
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun ColumnMathView(v: ColumnMath, p: Int) {
    val aS = v.a.toString()
    val bS = v.b.toString()
    val result = when (v.op) { '+' -> v.a + v.b; '-' -> v.a - v.b; else -> v.a * v.b }
    val rS = result.toString()
    val n = maxOf(aS.length, bS.length, rS.length)
    fun digitAt(s: String, fromRight: Int): Int = if (fromRight < s.length) s[s.length - 1 - fromRight] - '0' else 0

    // Carry (or borrow) per column, worked out the way a child would do the sum.
    val carryInto = IntArray(n + 2)            // "+"/"x": carry that lands ABOVE column c
    val borrowHere = BooleanArray(n + 2)       // "-": column c had to borrow ten
    when (v.op) {
        '+' -> { var c = 0; for (i in 0 until n) { val sum = digitAt(aS, i) + digitAt(bS, i) + c; c = sum / 10; carryInto[i + 1] = c } }
        'x' -> { var c = 0; for (i in 0 until n) { val prod = digitAt(aS, i) * v.b + c; c = prod / 10; carryInto[i + 1] = c } }
        '-' -> { var br = 0; for (i in 0 until n) { val top = digitAt(aS, i) - br; if (top < digitAt(bS, i)) { borrowHere[i] = true; br = 1 } else br = 0 } }
    }
    /** Small orange note above column [col] (0 = ones), once the column that caused it has been worked. */
    fun markFor(col: Int): String? {
        if (v.op == '-') {
            val delta = (if (borrowHere[col] && col < p) 10 else 0) - (if (col >= 1 && borrowHere[col - 1] && col - 1 < p) 1 else 0)
            return when {
                delta > 0 -> "+$delta"
                delta < 0 -> "−${-delta}"
                else -> null
            }
        }
        return if (col >= 1 && carryInto[col] > 0 && col - 1 < p) "${carryInto[col]}" else null
    }
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val size = minOf(maxHeight.value / 5.4f, maxWidth.value / ((n + 1.4f) * 0.64f), 62f)
        val cw = (size * 0.64f).dp
        val mono = FontFamily.Monospace
        val opSymbol = when (v.op) { 'x' -> "×"; '-' -> "−"; else -> "+" }
        @Composable
        fun row(text: String, opCell: Int = -1, color: Color = Tv.Ink, reveal: Boolean = false, marks: Boolean = false) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // cell 0 is the operator column; cell i holds the digit i places from the right: n - i
                for (cell in 0..n) {
                    val col = n - cell
                    Box(Modifier.width(cw).height((size * 1.12f).dp), contentAlignment = Alignment.Center) {
                        when {
                            cell == opCell -> BasicText(opSymbol, style = Tv.text(size.sp, Tv.Purple, family = mono))
                            marks && col < n -> markFor(col)?.let {
                                Box(
                                    Modifier.wrapContentWidth(unbounded = true).background(Tv.OrangeSoft, RoundedCornerShape(8.dp)).padding(horizontal = 4.dp, vertical = 1.dp),
                                ) {
                                    BasicText(it, style = Tv.text((size * 0.34f).sp, Color(0xFFC2410C), FontWeight.ExtraBold, mono), maxLines = 1, softWrap = false)
                                }
                            }
                            !marks && col < n && col < text.length && (!reveal || col < p) ->
                                BasicText(text[text.length - 1 - col].toString(), style = Tv.text(size.sp, color, FontWeight.ExtraBold, mono))
                        }
                    }
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            row("", marks = true)
            row(aS)
            row(bS, opCell = n - bS.length, color = Tv.Pink)
            Box(Modifier.width(cw * (n + 1)).height(4.dp).background(Tv.Ink, RoundedCornerShape(2.dp)))
            row(rS, color = Tv.Green, reveal = true)
        }
    }
}

@Composable
internal fun BoardView(v: Board, p: Int) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val n = v.lines.size
        val size = minOf(46f, maxHeight.value / (n * 1.5f))
        Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            v.lines.forEachIndexed { i, l ->
                val a by animateFloatAsState(if (i < p) 1f else 0f, label = "line")
                val color = when (l.tone) {
                    Tone.NORMAL -> Tv.Ink
                    Tone.GOOD -> Tv.GreenDark
                    Tone.HIGHLIGHT -> Tv.Pink
                    Tone.DIM -> Tv.Muted
                }
                if (l.text.isEmpty()) {
                    Spacer(Modifier.height((size * 0.6f).dp))
                } else {
                    Box(
                        Modifier.graphicsLayer { alpha = a; translationY = (1f - a) * 14f }.padding(vertical = (size * 0.12f).dp)
                            .then(if (l.tone == Tone.GOOD) Modifier.background(Tv.GreenSoft, RoundedCornerShape(14.dp)).padding(horizontal = 18.dp, vertical = 2.dp) else Modifier),
                        contentAlignment = Alignment.Center,
                    ) {
                        AutoText(l.text, maxSize = size.sp, minSize = 14.sp, color = color, weight = if (l.tone == Tone.DIM) FontWeight.SemiBold else FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
internal fun BalanceView(v: Balance) {
    val tilt by animateFloatAsState(v.tilt.toFloat(), label = "tilt")
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        val density = androidx.compose.ui.platform.LocalDensity.current.density
        val cx = wPx / 2f
        val pivotY = hPx * 0.3f
        val half = minOf(wPx * 0.34f, hPx * 0.95f)
        val a = Math.toRadians((-tilt * 7.0))
        val lx = cx - half * cos(a).toFloat()
        val ly = pivotY + half * sin(a).toFloat()
        val rx = cx + half * cos(a).toFloat()
        val ry = pivotY - half * sin(a).toFloat()
        val drop = hPx * 0.26f
        Canvas(Modifier.fillMaxSize()) {
            // stand
            val stand = Path().apply {
                moveTo(cx, pivotY); lineTo(cx - 40f * density, hPx * 0.97f); lineTo(cx + 40f * density, hPx * 0.97f); close()
            }
            drawPath(stand, Tv.Purple.copy(alpha = 0.25f))
            drawLine(Tv.Purple, Offset(lx, ly), Offset(rx, ry), 9f * density, StrokeCap.Round)
            drawCircle(Tv.Sun, 14f * density, Offset(cx, pivotY))
            for ((x, y) in listOf(lx to ly, rx to ry)) {
                drawLine(Tv.Muted, Offset(x, y), Offset(x - 60f * density, y + drop), 3f * density)
                drawLine(Tv.Muted, Offset(x, y), Offset(x + 60f * density, y + drop), 3f * density)
                drawRoundRect(Tv.Purple, Offset(x - 74f * density, y + drop), Size(148f * density, 12f * density), CornerRadius(6f * density))
            }
        }
        for ((x, y, label, tone, fill) in listOf(Quad(lx, ly, v.left, Tv.Blue, Tv.BlueSoft), Quad(rx, ry, v.right, Tv.Green, Tv.GreenSoft))) {
            PlaceAt(x, y + drop - 30f * density) {
                Box(
                    Modifier.width(150.dp).height(52.dp).background(fill, RoundedCornerShape(14.dp)).border(3.dp, tone, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center,
                ) { AutoText(label, Modifier.padding(horizontal = 6.dp), maxSize = 30.sp, minSize = 12.sp, color = tone) }
            }
        }
    }
}

private data class Quad(val x: Float, val y: Float, val label: String, val tone: Color, val fill: Color)

@Composable
internal fun Grid100View(v: Grid100, p: Int) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val side = minOf(maxWidth, maxHeight)
        val filled = minOf(p * v.chunk, v.filled)
        Canvas(Modifier.size(side)) {
            val cell = size.minDimension / 10f
            for (i in 0 until 100) {
                val r = i / 10
                val c = i % 10
                val on = i < filled
                drawRoundRect(
                    if (on) Tv.Pink else Tv.PaperSoft,
                    Offset(c * cell + cell * 0.06f, r * cell + cell * 0.06f), Size(cell * 0.88f, cell * 0.88f), CornerRadius(cell * 0.14f),
                )
                if (!on) drawRoundRect(Tv.Line, Offset(c * cell + cell * 0.06f, r * cell + cell * 0.06f), Size(cell * 0.88f, cell * 0.88f), CornerRadius(cell * 0.14f), style = Stroke(cell * 0.05f))
            }
        }
    }
}
