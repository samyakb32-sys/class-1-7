package com.gumthala.learningapp.tv.content

import kotlin.random.Random

/*
 * The whole curriculum is plain data + small generator functions, with NO Android or Compose
 * types in this package. That is deliberate: it lets every lesson and every question generator
 * run in a JVM unit test (see ContentTest) so a wrong answer key or a broken generator is caught
 * in CI, not by a child in front of a TV.
 */

/** Everything that can be drawn on the teaching board. The UI maps each one to a renderer. */
sealed interface Visual {
    /**
     * How many reveal steps this visual animates through (items counted, rows filled, tokens
     * shown...). 0 means it is simply drawn, fully visible, and does not take part in animation.
     */
    val maxProgress: Int
}

enum class CounterMode {
    /** All items are drawn plainly. */
    STATIC,

    /** Items light up one by one with a number under each (progress = how many are counted). */
    COUNT,

    /** The last [Counters.crossed] items get crossed out one by one (take-away). */
    CROSS_OUT,
}

/** A group of emoji objects, e.g. five apples. */
data class Counters(
    val emoji: String,
    val count: Int,
    val mode: CounterMode = CounterMode.COUNT,
    /** Items before this index are "group A", the rest "group B" (tinted differently). -1 = one group. */
    val split: Int = -1,
    val crossed: Int = 0,
    /** Items per row. 0 = choose automatically. */
    val cols: Int = 0,
    /** Show a big total under the group once every item has been counted. */
    val showTotal: Boolean = false,
    val label: String? = null,
) : Visual {
    override val maxProgress: Int
        get() = when (mode) {
            CounterMode.STATIC -> 0
            CounterMode.COUNT -> count
            CounterMode.CROSS_OUT -> crossed
        }
}

/** Several visuals in a row with an operator between each pair: [3 apples] + [2 apples] = ... */
data class Side(val parts: List<Visual>, val ops: List<String> = emptyList()) : Visual {
    override val maxProgress: Int get() = parts.sumOf { it.maxProgress }
}

/** Several visuals stacked top to bottom. Progress runs through the children in order. */
data class Stack(val parts: List<Visual>) : Visual {
    constructor(vararg parts: Visual) : this(parts.toList())

    override val maxProgress: Int get() = parts.sumOf { it.maxProgress }
}

/** A big equation. Tokens appear one by one. "?" is drawn as a question box. */
data class Equation(val tokens: List<String>) : Visual {
    constructor(vararg tokens: String) : this(tokens.toList())

    override val maxProgress: Int get() = tokens.size
}

/** A row of big tokens (numbers or emoji); [blankAt] is drawn as a "?" box. */
data class Strip(
    val items: List<String>,
    val blankAt: Int = -1,
    val arrows: Boolean = true,
    /** Label drawn under each item (e.g. "+2"), optional. */
    val note: String? = null,
) : Visual {
    override val maxProgress: Int get() = items.size
}

/**
 * A number line. [hops] are the numbers landed on; hop 0 starts at [start]. Progress = hops drawn.
 * [mark] gets a ring (e.g. "where are we?").
 */
data class NumberLine(
    val from: Int,
    val to: Int,
    val start: Int? = null,
    val hops: List<Int> = emptyList(),
    val mark: Int? = null,
    val labelEvery: Int = 1,
) : Visual {
    override val maxProgress: Int get() = hops.size
}

/** Two rows of five boxes (or four rows for up to 20). Progress = counters placed. */
data class TenFrame(val filled: Int, val frames: Int = 1, val emoji: String? = null) : Visual {
    override val maxProgress: Int get() = filled
}

/** Base-ten blocks. Progress reveals hundreds, then tens, then ones (skipping any that are 0). */
data class Blocks(val hundreds: Int = 0, val tens: Int = 0, val ones: Int = 0) : Visual {
    override val maxProgress: Int
        get() = (if (hundreds > 0) 1 else 0) + (if (tens > 0) 1 else 0) + (if (ones > 0) 1 else 0)
}

/** Rows x columns of objects. Progress = rows revealed. */
data class ArrayGrid(val rows: Int, val cols: Int, val emoji: String) : Visual {
    override val maxProgress: Int get() = rows
}

/** A fraction circle cut into [parts] slices, [filled] of them coloured. Progress = slices coloured. */
data class Pie(val parts: Int, val filled: Int, val label: Boolean = false) : Visual {
    override val maxProgress: Int get() = filled
}

/** One or more fraction bars stacked (for comparing / equivalent fractions). Progress = bars shown. */
data class FracBars(val bars: List<Pair<Int, Int>>, val labels: List<String> = emptyList()) : Visual {
    override val maxProgress: Int get() = bars.size
}

/** An analogue clock. Progress 0 -> 1 swings the hands to the time. */
data class Clock(val hour: Int, val minute: Int, val digital: Boolean = true) : Visual {
    override val maxProgress: Int get() = 1
}

enum class ShapeKind(val sides: Int) {
    CIRCLE(0), OVAL(0),
    TRIANGLE(3), EQUILATERAL(3), ISOSCELES(3), SCALENE(3), RIGHT_TRIANGLE(3),
    SQUARE(4), RECTANGLE(4), RHOMBUS(4), PARALLELOGRAM(4), TRAPEZIUM(4),
    PENTAGON(5), HEXAGON(6), OCTAGON(8),
}

/**
 * A flat shape. With [markSides] progress numbers the sides, then with [markCorners] the corners,
 * then draws [axes] lines of symmetry (when > 0).
 */
data class Shape(
    val kind: ShapeKind,
    val label: String? = null,
    val markSides: Boolean = false,
    val markCorners: Boolean = false,
    val axes: Int = 0,
) : Visual {
    override val maxProgress: Int
        get() = (if (markSides) kind.sides else 0) + (if (markCorners) kind.sides else 0) + axes
}

enum class SolidKind { CUBE, CUBOID, SPHERE, CYLINDER, CONE, PYRAMID }

data class Solid(val kind: SolidKind, val label: String? = null) : Visual {
    override val maxProgress: Int get() = 0
}

/** Two rays and an arc. Progress 0 -> 1 swings the second ray open to [degrees]. */
data class AngleShape(val degrees: Int, val label: String? = null) : Visual {
    override val maxProgress: Int get() = 1
}

enum class GridMode { STATIC, AREA, PERIMETER }

/** A [w] x [h] rectangle of unit squares. AREA fills a row per step; PERIMETER walks the 4 sides. */
data class RectGrid(val w: Int, val h: Int, val mode: GridMode = GridMode.STATIC, val unit: String = "") : Visual {
    override val maxProgress: Int
        get() = when (mode) {
            GridMode.STATIC -> 0
            GridMode.AREA -> h
            GridMode.PERIMETER -> 4
        }
}

/** A bar chart. Progress = bars grown. */
data class Bars(
    val labels: List<String>,
    val values: List<Int>,
    val highlight: Int = -1,
    val unit: String = "",
) : Visual {
    override val maxProgress: Int get() = values.size
}

/** Indian coins / notes by value (1, 2, 5, 10 coins; 20, 50, 100, 200, 500 notes). Progress = items shown. */
data class Money(val items: List<Int>) : Visual {
    override val maxProgress: Int get() = items.size
}

enum class Tone { NORMAL, GOOD, HIGHLIGHT, DIM }

data class BoardLine(val text: String, val tone: Tone = Tone.NORMAL)

/** Lines of writing on a board, revealed one at a time. The general-purpose "show the working". */
data class Board(val lines: List<BoardLine>) : Visual {
    override val maxProgress: Int get() = lines.size
}

/** Vertical arithmetic (op is '+', '-' or 'x'; for 'x', [b] is one digit). Progress = result digits. */
data class ColumnMath(val a: Int, val b: Int, val op: Char) : Visual {
    override val maxProgress: Int
        get() {
            val r = when (op) {
                '+' -> a + b
                '-' -> a - b
                else -> a * b
            }
            return r.toString().length
        }
}

/** A balance scale with text on each pan. [tilt]: -1 left pan lower, 0 level, +1 right pan lower. */
data class Balance(val left: String, val right: String, val tilt: Int = 0) : Visual {
    override val maxProgress: Int get() = 0
}

/** A 10 x 10 grid (100 squares) with [filled] coloured, revealed [chunk] squares at a time. */
data class Grid100(val filled: Int, val chunk: Int = 5) : Visual {
    override val maxProgress: Int get() = (filled + chunk - 1) / chunk
}

/** A big word/number, optionally with an emoji and a smaller line under it. */
data class BigText(val text: String, val sub: String? = null, val emoji: String? = null) : Visual {
    override val maxProgress: Int get() = 0
}

/** Place-value table (Indian system). Progress reveals digits left to right. */
data class PlaceChart(val number: Long) : Visual {
    override val maxProgress: Int get() = number.toString().length
}

/** A ruler with an object lined up at 0. */
data class Ruler(val cm: Int, val emoji: String = "✏️", val maxCm: Int = 12) : Visual {
    override val maxProgress: Int get() = 0
}

/** Nothing to draw (used where the narration alone carries the moment). */
data object Empty : Visual {
    override val maxProgress: Int get() = 0
}

/** Face the mascot pulls during a step. */
enum class Mood { HAPPY, CHEER, THINK, OOPS }

/**
 * One moment in a lesson: the narrator says [say] (it is also shown as the caption) while [visual]
 * animates. [counting] makes the narrator speak each number as items light up.
 * [from] is the progress the animation starts at ([ALL] means "already fully revealed") and [to] where it stops.
 */
data class Step(
    val say: String,
    val visual: Visual,
    val counting: Boolean = false,
    val from: Int = 0,
    val mood: Mood = Mood.HAPPY,
    /** Progress the animation stops at (default: the end). Lets one sum be taught column by column. */
    val to: Int = Int.MAX_VALUE,
) {
    companion object {
        const val ALL = Int.MAX_VALUE
    }
}

/**
 * What the narrator says when reveal step [p] (1-based) of this visual lands, for a `counting` step.
 * Counters say 1, 2, 3 (restarting for each group); a Strip reads its items (skip counting);
 * a NumberLine reads each number it lands on; an array says the running total. Null = stay quiet.
 */
fun Visual.spokenAt(p: Int): String? = when (this) {
    is Counters -> if (mode == CounterMode.COUNT && p in 1..count) Words.number(p) else null
    is TenFrame -> if (p in 1..filled) Words.number(p) else null
    is Strip -> items.getOrNull(p - 1)?.takeIf { it != "?" && it.isNotBlank() }
    is NumberLine -> hops.getOrNull(p - 1)?.let { Words.number(it) }
    is ArrayGrid -> if (p in 1..rows) Words.number(p * cols) else null
    is Shape -> {
        val sides = kind.sides
        val cornerStep = p - (if (markSides) sides else 0)
        when {
            markSides && p in 1..sides -> Words.number(p)
            markCorners && cornerStep in 1..sides -> Words.number(cornerStep)
            else -> null
        }
    }
    is Money -> null
    is Side -> childSpoken(parts, p)
    is Stack -> childSpoken(parts, p)
    else -> null
}

private fun childSpoken(parts: List<Visual>, p: Int): String? {
    var left = p
    for (c in parts) {
        val m = c.maxProgress
        if (left <= m) return if (m == 0) null else c.spokenAt(left)
        left -= m
    }
    return null
}

/** One answer button: text and/or a small picture. */
data class Choice(val text: String, val visual: Visual? = null)

data class Question(
    val prompt: String,
    val visual: Visual?,
    val choices: List<Choice>,
    val answer: Int,
    /** Shown after a first wrong try: a nudge, not the answer. */
    val hint: String,
    /** Played after a second wrong try (or on "Show me"): the worked answer. */
    val solution: List<Step>,
)

/** Identifies a question by what the child sees (prompt + picture + answer), not the prompt alone. */
fun Question.fingerprint(): String = "$prompt|$visual|${choices[answer]}"

/** A lesson: a guided walk-through, then endless generated practice. */
class Lesson(
    val id: String,
    val title: String,
    val blurb: String,
    val icon: String,
    val teach: List<Step>,
    val questionCount: Int = 8,
    /** Build question number [index] (0-based) of a practice round. Earlier ones should be easier. */
    val quiz: (Random, Int) -> Question,
) {
    /**
     * A fresh round of practice: no question twice, and (where the generator allows) never the same
     * answer twice in a row, so a child can't get by on "it's always seven".
     */
    fun buildRound(rnd: Random = Random.Default): List<Question> {
        val seen = HashSet<String>()
        val out = ArrayList<Question>(questionCount)
        var previousAnswer: String? = null
        for (i in 0 until questionCount) {
            var q = quiz(rnd, i)
            var tries = 0
            while ((q.fingerprint() in seen || q.choices[q.answer].text == previousAnswer) && tries < 16) {
                q = quiz(rnd, i)
                tries++
            }
            seen.add(q.fingerprint())
            previousAnswer = q.choices[q.answer].text
            out.add(q)
        }
        return out
    }
}

/** A themed group of lessons on the map. [color] is 0xAARRGGBB. */
class World(
    val id: String,
    val title: String,
    val tagline: String,
    val emoji: String,
    val color: Long,
    val level: String,
    val lessons: List<Lesson>,
)
