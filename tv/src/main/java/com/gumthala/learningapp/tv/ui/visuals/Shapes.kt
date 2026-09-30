package com.gumthala.learningapp.tv.ui.visuals

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.tv.content.*
import com.gumthala.learningapp.tv.ui.components.AutoText
import com.gumthala.learningapp.tv.ui.theme.Tv
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

private data class Pt(val x: Float, val y: Float)

private fun regular(n: Int, startDeg: Float, r: Float): List<Pt> = List(n) {
    val a = Math.toRadians((startDeg + it * 360.0 / n))
    Pt((cos(a) * r).toFloat(), (sin(a) * r).toFloat())
}

/** Corner points in a roughly [-1, 1] box (y down). */
private fun outline(kind: ShapeKind): List<Pt> = when (kind) {
    ShapeKind.SQUARE -> listOf(Pt(-0.8f, -0.8f), Pt(0.8f, -0.8f), Pt(0.8f, 0.8f), Pt(-0.8f, 0.8f))
    ShapeKind.RECTANGLE -> listOf(Pt(-1.25f, -0.7f), Pt(1.25f, -0.7f), Pt(1.25f, 0.7f), Pt(-1.25f, 0.7f))
    ShapeKind.TRIANGLE, ShapeKind.EQUILATERAL -> listOf(Pt(0f, -0.9f), Pt(1.04f, 0.9f), Pt(-1.04f, 0.9f))
    ShapeKind.ISOSCELES -> listOf(Pt(0f, -1f), Pt(0.6f, 0.85f), Pt(-0.6f, 0.85f))
    ShapeKind.SCALENE -> listOf(Pt(-1.1f, 0.85f), Pt(0.95f, 0.85f), Pt(0.35f, -0.85f))
    ShapeKind.RIGHT_TRIANGLE -> listOf(Pt(-0.85f, -0.85f), Pt(-0.85f, 0.85f), Pt(1.1f, 0.85f))
    ShapeKind.RHOMBUS -> listOf(Pt(0f, -1f), Pt(0.8f, 0f), Pt(0f, 1f), Pt(-0.8f, 0f))
    ShapeKind.PARALLELOGRAM -> listOf(Pt(-0.6f, -0.65f), Pt(1.3f, -0.65f), Pt(0.6f, 0.65f), Pt(-1.3f, 0.65f))
    ShapeKind.TRAPEZIUM -> listOf(Pt(-0.6f, -0.65f), Pt(0.6f, -0.65f), Pt(1.2f, 0.65f), Pt(-1.2f, 0.65f))
    ShapeKind.PENTAGON -> regular(5, -90f, 1f)
    ShapeKind.HEXAGON -> regular(6, 0f, 1f)
    ShapeKind.OCTAGON -> regular(8, 22.5f, 1f)
    ShapeKind.CIRCLE, ShapeKind.OVAL -> emptyList()
}

/** Directions (degrees from the x axis) of the lines of symmetry. */
private fun axisAngles(kind: ShapeKind): List<Float> = when (kind) {
    ShapeKind.SQUARE, ShapeKind.CIRCLE -> listOf(90f, 0f, 45f, 135f)
    ShapeKind.RECTANGLE, ShapeKind.RHOMBUS, ShapeKind.OVAL -> listOf(90f, 0f)
    ShapeKind.TRIANGLE, ShapeKind.EQUILATERAL -> listOf(90f, 30f, 150f)
    ShapeKind.ISOSCELES -> listOf(90f)
    ShapeKind.PENTAGON -> listOf(90f, 18f, 162f, 54f, 126f)
    ShapeKind.HEXAGON -> listOf(0f, 60f, 120f, 30f, 90f, 150f)
    ShapeKind.OCTAGON -> listOf(0f, 45f, 90f, 135f, 22.5f, 67.5f, 112.5f, 157.5f)
    else -> emptyList()
}

private val shapeFills = listOf(Color(0xFFDCEBFF), Color(0xFFFFE3EC), Color(0xFFDCFCE7), Color(0xFFFEF3D6), Color(0xFFEDE4FF), Color(0xFFFFE8D6))
private val shapeEdges = listOf(Tv.Blue, Tv.Pink, Tv.Green, Tv.SunDeep, Tv.Violet, Tv.Orange)

@Composable
internal fun ShapeView(v: Shape, p: Int) {
    // Top-start (the default) matters: PlaceAt labels are positioned from the top-left corner.
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        val density = LocalDensity.current.density
        val labelH = if (v.label != null) 40f * density else 0f
        val cx = wPx / 2f
        val cy = (hPx - labelH) / 2f
        val pts = outline(v.kind)
        val isRound = pts.isEmpty()
        val boxW = if (isRound) (if (v.kind == ShapeKind.OVAL) 2.4f else 2f) else (pts.maxOf { it.x } - pts.minOf { it.x })
        val boxH = if (isRound) (if (v.kind == ShapeKind.OVAL) 1.5f else 2f) else (pts.maxOf { it.y } - pts.minOf { it.y })
        val scale = min(wPx * 0.72f / boxW, (hPx - labelH) * 0.68f / boxH)
        val midX = if (isRound) 0f else (pts.maxOf { it.x } + pts.minOf { it.x }) / 2f
        val midY = if (isRound) 0f else (pts.maxOf { it.y } + pts.minOf { it.y }) / 2f
        val pp = pts.map { Offset(cx + (it.x - midX) * scale, cy + (it.y - midY) * scale) }
        val fill = shapeFills[v.kind.ordinal % shapeFills.size]
        val edge = shapeEdges[v.kind.ordinal % shapeEdges.size]
        val sides = v.kind.sides
        val sidesShown = if (v.markSides) min(p, sides) else 0
        val cornersShown = if (v.markCorners) max(0, min(p - (if (v.markSides) sides else 0), sides)) else 0
        val axesShown = max(0, p - (if (v.markSides) sides else 0) - (if (v.markCorners) sides else 0))
        val centre = if (pp.isEmpty()) Offset(cx, cy) else Offset(pp.sumOf { it.x.toDouble() }.toFloat() / pp.size, pp.sumOf { it.y.toDouble() }.toFloat() / pp.size)

        Canvas(Modifier.fillMaxSize()) {
            if (isRound) {
                val rw = boxW * scale / 2f
                val rh = boxH * scale / 2f
                drawOval(fill, Offset(cx - rw, cy - rh), Size(rw * 2, rh * 2))
                drawOval(edge, Offset(cx - rw, cy - rh), Size(rw * 2, rh * 2), style = Stroke(7f * density))
            } else {
                val path = Path().apply { pp.forEachIndexed { i, o -> if (i == 0) moveTo(o.x, o.y) else lineTo(o.x, o.y) }; close() }
                drawPath(path, fill)
                drawPath(path, edge, style = Stroke(7f * density, join = androidx.compose.ui.graphics.StrokeJoin.Round))
                for (i in 0 until sidesShown) {
                    drawLine(Tv.Orange, pp[i], pp[(i + 1) % sides], 11f * density, StrokeCap.Round)
                }
                for (i in 0 until cornersShown) drawCircle(Tv.Pink, 13f * density, pp[i])
            }
            val dash = PathEffect.dashPathEffect(floatArrayOf(18f * density, 12f * density))
            val margin = 14f * density
            val rw = boxW * scale / 2f
            val rh = boxH * scale / 2f
            for (i in 0 until min(axesShown, axisAngles(v.kind).size)) {
                val a = Math.toRadians(axisAngles(v.kind)[i].toDouble())
                val dx = cos(a).toFloat()
                val dy = -sin(a).toFloat()
                // how far the line runs before it leaves the shape, each way, plus a little extra
                fun reach(sx: Float, sy: Float): Float =
                    if (isRound) (rw * rh / kotlin.math.sqrt((rh * sx) * (rh * sx) + (rw * sy) * (rw * sy))) + margin
                    else rayDistance(centre, sx, sy, pp) + margin
                val fwd = reach(dx, dy)
                val back = reach(-dx, -dy)
                drawLine(Tv.Red, Offset(centre.x - dx * back, centre.y - dy * back), Offset(centre.x + dx * fwd, centre.y + dy * fwd), 4f * density, pathEffect = dash)
            }
        }
        // numbered badges on sides / corners
        for (i in 0 until sidesShown) {
            val a = pp[i]; val b = pp[(i + 1) % sides]
            val mx = (a.x + b.x) / 2f; val my = (a.y + b.y) / 2f
            var ox = mx - centre.x; var oy = my - centre.y
            val len = kotlin.math.sqrt(ox * ox + oy * oy).coerceAtLeast(1f)
            ox = ox / len * 26f * density; oy = oy / len * 26f * density
            PlaceAt(mx + ox, my + oy) { Badge("${i + 1}", Tv.Orange) }
        }
        for (i in 0 until cornersShown) {
            val o = pp[i]
            var ox = o.x - centre.x; var oy = o.y - centre.y
            val len = kotlin.math.sqrt(ox * ox + oy * oy).coerceAtLeast(1f)
            ox = ox / len * 28f * density; oy = oy / len * 28f * density
            PlaceAt(o.x + ox, o.y + oy) { Badge("${i + 1}", Tv.Pink) }
        }
        if (v.label != null) {
            PlaceAt(cx, hPx - labelH / 2f) { BasicText(v.label, style = Tv.text(28.sp, Tv.Purple)) }
        }
    }
}

/** Distance from [c] along (dx, dy) to where it leaves the convex polygon [pts]. */
private fun rayDistance(c: Offset, dx: Float, dy: Float, pts: List<Offset>): Float {
    var best = 0f
    for (i in pts.indices) {
        val a = pts[i]
        val b = pts[(i + 1) % pts.size]
        val ex = b.x - a.x
        val ey = b.y - a.y
        val det = -dx * ey + dy * ex
        if (kotlin.math.abs(det) < 1e-6f) continue
        val rx = a.x - c.x
        val ry = a.y - c.y
        val t = (-rx * ey + ry * ex) / det
        val u = (dx * ry - dy * rx) / det
        if (t > 0f && u >= -0.001f && u <= 1.001f) best = max(best, t)
    }
    return best
}

@Composable
private fun Badge(text: String, color: Color) {
    Box(Modifier.size(32.dp).background(color, CircleShape), contentAlignment = Alignment.Center) {
        BasicText(text, style = Tv.text(20.sp, Color.White))
    }
}

@Composable
internal fun SolidView(v: Solid) {
    BoxWithConstraints(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val labelH = if (v.label != null) 44f else 0f
        val side = min(maxWidth.value, maxHeight.value - labelH) * 0.86f
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Canvas(Modifier.size(side.dp)) { drawSolid(v.kind, size.minDimension) }
            if (v.label != null) BasicText(v.label, style = Tv.text(28.sp, Tv.Purple))
        }
    }
}

private fun DrawScope.drawSolid(kind: SolidKind, s: Float) {
    val edge = Color(0x55000000)
    fun poly(p: List<Offset>): Path = Path().apply { moveTo(p[0].x, p[0].y); for (i in 1 until p.size) lineTo(p[i].x, p[i].y); close() }
    fun face(path: Path, c: Color) { drawPath(path, c); drawPath(path, edge, style = Stroke(s * 0.012f)) }
    when (kind) {
        SolidKind.CUBE, SolidKind.CUBOID -> {
            val w = if (kind == SolidKind.CUBE) s * 0.52f else s * 0.72f
            val h = s * 0.5f
            val dx = s * 0.22f; val dy = -s * 0.2f
            val x0 = (s - w - dx) / 2f; val y0 = (s - h - dy) / 2f + (-dy) * 0f + s * 0.05f
            face(poly(listOf(Offset(x0, y0), Offset(x0 + dx, y0 + dy), Offset(x0 + w + dx, y0 + dy), Offset(x0 + w, y0))), Color(0xFF93C5FD))
            face(poly(listOf(Offset(x0 + w, y0), Offset(x0 + w + dx, y0 + dy), Offset(x0 + w + dx, y0 + h + dy), Offset(x0 + w, y0 + h))), Color(0xFF2563EB))
            face(poly(listOf(Offset(x0, y0), Offset(x0 + w, y0), Offset(x0 + w, y0 + h), Offset(x0, y0 + h))), Tv.Blue)
        }
        SolidKind.SPHERE -> {
            val r = s * 0.36f
            val c = Offset(s / 2, s / 2)
            drawCircle(Brush.radialGradient(listOf(Color(0xFFFFB3C7), Tv.Pink, Color(0xFFC2185B)), Offset(c.x - r * 0.35f, c.y - r * 0.4f), r * 1.5f), r, c)
            drawCircle(Color(0x66FFFFFF), r * 0.18f, Offset(c.x - r * 0.4f, c.y - r * 0.45f))
        }
        SolidKind.CYLINDER -> {
            val w = s * 0.46f; val h = s * 0.5f; val eh = s * 0.16f
            val x0 = (s - w) / 2f; val y0 = (s - h) / 2f
            drawOval(Color(0xFF16A34A), Offset(x0, y0 + h - eh / 2), Size(w, eh))
            drawRect(Tv.Green, Offset(x0, y0 + eh / 2), Size(w, h - eh / 2 + 1f))
            drawOval(Color(0xFF16A34A), Offset(x0, y0 + h - eh / 2), Size(w, eh))
            drawRect(Tv.Green, Offset(x0, y0 + eh / 2), Size(w, h - eh))
            drawOval(Color(0xFF86EFAC), Offset(x0, y0), Size(w, eh))
            drawOval(edge, Offset(x0, y0), Size(w, eh), style = Stroke(s * 0.012f))
        }
        SolidKind.CONE -> {
            val w = s * 0.5f; val h = s * 0.55f; val eh = s * 0.17f
            val x0 = (s - w) / 2f; val y0 = (s - h) / 2f
            face(poly(listOf(Offset(s / 2, y0), Offset(x0 + w, y0 + h), Offset(x0, y0 + h))), Tv.Orange)
            // the curved bottom edge: lower half of an ellipse, in the same colour
            drawArc(Tv.Orange, 0f, 180f, true, Offset(x0, y0 + h - eh / 2), Size(w, eh))
            drawArc(edge, 0f, 180f, false, Offset(x0, y0 + h - eh / 2), Size(w, eh), style = Stroke(s * 0.012f))
        }
        SolidKind.PYRAMID -> {
            val w = s * 0.56f; val h = s * 0.52f
            val x0 = (s - w) / 2f - s * 0.03f; val y0 = (s - h) / 2f + s * 0.06f
            val apex = Offset(x0 + w * 0.55f, y0 - h * 0.05f)
            val a = Offset(x0, y0 + h); val b = Offset(x0 + w, y0 + h)
            val c = Offset(x0 + w + s * 0.13f, y0 + h - s * 0.1f)
            face(poly(listOf(apex, b, c)), Color(0xFFB45309))
            face(poly(listOf(apex, a, b)), Tv.SunDeep)
        }
    }
}

@Composable
internal fun AngleView(v: AngleShape, p: Int) {
    val t by animateFloatAsState(if (p >= 1) 1f else 0f, tween(1100), label = "angle")
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        val density = LocalDensity.current.density
        val len = min(wPx * 0.5f, hPx * 0.78f)
        val vx = wPx / 2f - len * 0.42f
        val vy = hPx * 0.82f
        val deg = 8f + (v.degrees - 8f) * t
        val rad = Math.toRadians(deg.toDouble())
        val ex = vx + len * cos(rad).toFloat()
        val ey = vy - len * sin(rad).toFloat()
        Canvas(Modifier.fillMaxSize()) {
            val arcR = len * 0.26f
            if (v.degrees == 90 && t > 0.98f) {
                val q = arcR * 0.7f
                drawRect(Tv.Sun.copy(alpha = 0.5f), Offset(vx, vy - q), Size(q, q))
                drawRect(Tv.SunDeep, Offset(vx, vy - q), Size(q, q), style = Stroke(4f * density))
            } else {
                drawArc(Tv.Sun.copy(alpha = 0.55f), -deg, deg, true, Offset(vx - arcR, vy - arcR), Size(arcR * 2, arcR * 2))
                drawArc(Tv.SunDeep, -deg, deg, false, Offset(vx - arcR, vy - arcR), Size(arcR * 2, arcR * 2), style = Stroke(5f * density))
            }
            drawLine(Tv.Ink, Offset(vx, vy), Offset(vx + len, vy), 9f * density, StrokeCap.Round)
            drawLine(Tv.Purple, Offset(vx, vy), Offset(ex, ey), 9f * density, StrokeCap.Round)
            drawCircle(Tv.Pink, 11f * density, Offset(vx, vy))
        }
        if (v.label != null && t > 0.98f) {
            val mid = Math.toRadians((deg / 2f).toDouble())
            val r = len * (if (v.degrees < 40) 0.55f else 0.42f)
            PlaceAt(vx + r * cos(mid).toFloat() + 10f * density, vy - r * sin(mid).toFloat()) {
                BasicText(v.label, style = Tv.text(32.sp, Tv.Purple, FontWeight.ExtraBold))
            }
        }
    }
}

@Composable
internal fun RectGridView(v: RectGrid, p: Int) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        val density = LocalDensity.current.density
        val cell = min(wPx * 0.8f / v.w, hPx * 0.68f / v.h).coerceAtMost(72f * density)
        val gw = cell * v.w
        val gh = cell * v.h
        val x0 = (wPx - gw) / 2f
        val y0 = (hPx - gh) / 2f + 14f * density
        Canvas(Modifier.fillMaxSize()) {
            for (r in 0 until v.h) for (c in 0 until v.w) {
                val filled = v.mode == GridMode.AREA && r < p
                drawRoundRect(if (filled) Tv.Green.copy(alpha = 0.75f) else Tv.PaperSoft, Offset(x0 + c * cell + 2, y0 + r * cell + 2), Size(cell - 4, cell - 4), CornerRadius(cell * 0.1f))
                drawRoundRect(if (filled) Tv.GreenDark else Tv.Line, Offset(x0 + c * cell + 2, y0 + r * cell + 2), Size(cell - 4, cell - 4), CornerRadius(cell * 0.1f), style = Stroke(3f))
            }
            if (v.mode == GridMode.PERIMETER) {
                val w = 12f * density
                if (p >= 1) drawLine(Tv.Orange, Offset(x0, y0), Offset(x0 + gw, y0), w, StrokeCap.Round)
                if (p >= 2) drawLine(Tv.Orange, Offset(x0 + gw, y0), Offset(x0 + gw, y0 + gh), w, StrokeCap.Round)
                if (p >= 3) drawLine(Tv.Orange, Offset(x0 + gw, y0 + gh), Offset(x0, y0 + gh), w, StrokeCap.Round)
                if (p >= 4) drawLine(Tv.Orange, Offset(x0, y0 + gh), Offset(x0, y0), w, StrokeCap.Round)
            }
        }
        val u = if (v.unit.isEmpty()) "" else " ${v.unit}"
        PlaceAt(x0 + gw / 2f, y0 - 22f * density) { BasicText("${v.w}$u", style = Tv.text(28.sp, Tv.Blue)) }
        PlaceAt(x0 - (if (v.unit.isEmpty()) 30f else 52f) * density, y0 + gh / 2f) { BasicText("${v.h}$u", style = Tv.text(28.sp, Tv.Pink)) }
    }
}

@Composable
internal fun ClockView(v: Clock, p: Int) {
    val t by animateFloatAsState(if (p >= 1) 1f else 0f, tween(1300), label = "clock")
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        val density = LocalDensity.current.density
        val digitalH = if (v.digital) 52f * density else 0f
        val d = min(wPx, hPx - digitalH) * 0.94f
        val r = d / 2f
        val cx = wPx / 2f
        val cy = (hPx - digitalH) / 2f
        val minuteDeg = v.minute * 6f * t
        val hourDeg = ((v.hour % 12) * 30f + v.minute * 0.5f) * t
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(Color.White, r, Offset(cx, cy))
            drawCircle(Tv.Purple, r, Offset(cx, cy), style = Stroke(10f * density))
            for (i in 0 until 60) {
                val a = Math.toRadians(i * 6.0 - 90.0)
                val major = i % 5 == 0
                val r1 = r * (if (major) 0.86f else 0.91f)
                drawLine(Tv.Ink, Offset(cx + cos(a).toFloat() * r1, cy + sin(a).toFloat() * r1), Offset(cx + cos(a).toFloat() * r * 0.96f, cy + sin(a).toFloat() * r * 0.96f), (if (major) 4f else 2f) * density, StrokeCap.Round)
            }
            fun hand(deg: Float, length: Float, w: Float, c: Color) {
                val a = Math.toRadians(deg - 90.0)
                drawLine(c, Offset(cx, cy), Offset(cx + cos(a).toFloat() * length, cy + sin(a).toFloat() * length), w, StrokeCap.Round)
            }
            hand(hourDeg, r * 0.40f, 12f * density, Tv.Ink)
            hand(minuteDeg, r * 0.58f, 8f * density, Tv.Pink)
            drawCircle(Tv.Sun, 11f * density, Offset(cx, cy))
        }
        val numeralSp = (r / density * 0.17f).coerceIn(14f, 34f)
        for (n in 1..12) {
            val a = Math.toRadians(n * 30.0 - 90.0)
            PlaceAt(cx + cos(a).toFloat() * r * 0.73f, cy + sin(a).toFloat() * r * 0.73f) {
                BasicText("$n", style = Tv.text(numeralSp.sp, Tv.Ink))
            }
        }
        if (v.digital && p >= 1) {
            PlaceAt(cx, hPx - digitalH / 2f) {
                Box(Modifier.background(Tv.Ink, androidx.compose.foundation.shape.RoundedCornerShape(12.dp)).padding(horizontal = 18.dp, vertical = 4.dp)) {
                    BasicText("${v.hour}:${v.minute.toString().padStart(2, '0')}", style = Tv.text(34.sp, Tv.Sun, FontWeight.ExtraBold, androidx.compose.ui.text.font.FontFamily.Monospace))
                }
            }
        }
    }
}

@Composable
internal fun RulerView(v: Ruler) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val wPx = constraints.maxWidth.toFloat()
        val hPx = constraints.maxHeight.toFloat()
        val density = LocalDensity.current.density
        val margin = 30f * density
        val unit = (wPx - 2 * margin) / v.maxCm
        val rulerTop = hPx * 0.56f
        val rulerH = 62f * density
        Canvas(Modifier.fillMaxSize()) {
            drawRoundRect(Color(0xFFFDE68A), Offset(margin - 14 * density, rulerTop), Size(wPx - 2 * margin + 28 * density, rulerH), CornerRadius(10f * density))
            drawRoundRect(Tv.SunDeep, Offset(margin - 14 * density, rulerTop), Size(wPx - 2 * margin + 28 * density, rulerH), CornerRadius(10f * density), style = Stroke(3f * density))
            for (i in 0..v.maxCm) {
                drawLine(Tv.Ink, Offset(margin + i * unit, rulerTop), Offset(margin + i * unit, rulerTop + 26f * density), 3f * density)
                if (i < v.maxCm) drawLine(Tv.Ink, Offset(margin + (i + 0.5f) * unit, rulerTop), Offset(margin + (i + 0.5f) * unit, rulerTop + 14f * density), 2f * density)
            }
            val barH = 46f * density
            drawRoundRect(Tv.Pink, Offset(margin, rulerTop - barH - 8f * density), Size(v.cm * unit, barH), CornerRadius(barH / 2f))
        }
        for (i in 0..v.maxCm) PlaceAt(margin + i * unit, rulerTop + 42f * density) { BasicText("$i", style = Tv.text(22.sp, Tv.Ink)) }
        PlaceAt(margin + v.cm * unit / 2f, rulerTop - 31f * density) { BasicText(v.emoji, style = Tv.text(34.sp)) }
        PlaceAt(wPx / 2f, rulerTop + rulerH + 26f * density) { BasicText("cm", style = Tv.text(24.sp, Tv.Muted)) }
    }
}
