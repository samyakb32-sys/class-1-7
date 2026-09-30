package com.gumthala.learningapp.tv.ui.visuals

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gumthala.learningapp.tv.content.*
import com.gumthala.learningapp.tv.ui.components.AutoText
import com.gumthala.learningapp.tv.ui.theme.Tv
import kotlin.math.roundToInt

/**
 * Draws any [Visual]. [progress] is how many of its reveal steps have happened (0..maxProgress);
 * pass [Visual.maxProgress] to show it complete, as questions do.
 */
@Composable
fun VisualView(v: Visual, progress: Int, modifier: Modifier = Modifier) {
    val p = progress.coerceIn(0, v.maxProgress)
    Box(modifier, contentAlignment = Alignment.Center) {
        when (v) {
            is Counters -> CountersView(v, p)
            is Side -> SideView(v, p)
            is Stack -> StackView(v, p)
            is Equation -> EquationView(v, p)
            is Strip -> StripView(v, p)
            is NumberLine -> NumberLineView(v, p)
            is TenFrame -> TenFrameView(v, p)
            is Blocks -> BlocksView(v, p)
            is ArrayGrid -> ArrayView(v, p)
            is Pie -> PieView(v, p)
            is FracBars -> FracBarsView(v, p)
            is Clock -> ClockView(v, p)
            is Shape -> ShapeView(v, p)
            is Solid -> SolidView(v)
            is AngleShape -> AngleView(v, p)
            is RectGrid -> RectGridView(v, p)
            is Bars -> BarsView(v, p)
            is Money -> MoneyView(v, p)
            is Board -> BoardView(v, p)
            is ColumnMath -> ColumnMathView(v, p)
            is Balance -> BalanceView(v)
            is Grid100 -> Grid100View(v, p)
            is BigText -> BigTextView(v)
            is PlaceChart -> PlaceChartView(v, p)
            is Ruler -> RulerView(v)
            is Empty -> Unit
        }
    }
}

/** Splits one progress number across children in order: the first fills up, then the next... */
internal fun splitProgress(parts: List<Visual>, p: Int): List<Int> {
    var left = p
    return parts.map { c ->
        val take = minOf(left, c.maxProgress)
        left -= take
        take
    }
}

/** Short, wide things (text rows) need less height than pictures when stacked. */
private fun Visual.flex(): Float = when (this) {
    is BigText -> 0.55f
    is Equation -> 0.42f
    is Strip -> 0.5f
    else -> 1f
}

@Composable
private fun SideView(v: Side, p: Int) {
    val ps = splitProgress(v.parts, p)
    Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        v.parts.forEachIndexed { i, part ->
            if (i > 0) {
                val op = v.ops.getOrNull(i - 1)
                Box(Modifier.width(if (op != null) 72.dp else 20.dp).fillMaxHeight(), contentAlignment = Alignment.Center) {
                    if (op != null) AutoText(op, maxSize = 54.sp, color = Tv.Purple)
                }
            }
            VisualView(part, ps[i], Modifier.weight(1f).fillMaxHeight())
        }
    }
}

@Composable
private fun StackView(v: Stack, p: Int) {
    val ps = splitProgress(v.parts, p)
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        v.parts.forEachIndexed { i, part ->
            VisualView(part, ps[i], Modifier.weight(part.flex()).fillMaxSize().padding(vertical = 2.dp))
        }
    }
}

/** Places its content centred on a pixel point, taking no room itself. Used for labels on drawings. */
@Composable
internal fun PlaceAt(x: Float, y: Float, content: @Composable () -> Unit) {
    Layout(content = content) { measurables, _ ->
        val placeables = measurables.map { it.measure(Constraints()) }
        layout(0, 0) {
            placeables.forEach { it.place((x - it.width / 2f).roundToInt(), (y - it.height / 2f).roundToInt()) }
        }
    }
}
