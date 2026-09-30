package com.gumthala.learningapp.tv

import com.gumthala.learningapp.tv.content.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

/**
 * Runs every lesson and every question generator many times with different random seeds.
 * The curriculum is code, so this is what stands between a typo and a child seeing a wrong
 * answer key, an empty button, or a caption too long to fit on the TV.
 */
class ContentTest {

    private val seeds = 60

    private fun fail(where: String, msg: String): Nothing = throw AssertionError("$where: $msg")

    private fun walk(v: Visual, inQuestion: Boolean, where: String) {
        when (v) {
            is Counters -> {
                if (v.count !in 0..30) fail(where, "Counters count ${v.count}")
                if (inQuestion && v.mode != CounterMode.STATIC) fail(where, "question Counters must be STATIC (would spoil the answer)")
                if (v.mode == CounterMode.CROSS_OUT && v.crossed > v.count) fail(where, "crossed > count")
                if (v.split > v.count) fail(where, "split > count")
            }
            is Side -> {
                if (v.parts.isEmpty()) fail(where, "empty Side")
                if (v.ops.isNotEmpty() && v.ops.size != v.parts.size - 1) fail(where, "Side ops ${v.ops.size} vs parts ${v.parts.size}")
                v.parts.forEach { walk(it, inQuestion, where) }
            }
            is Stack -> {
                if (v.parts.isEmpty()) fail(where, "empty Stack")
                v.parts.forEach { walk(it, inQuestion, where) }
            }
            is Equation -> if (v.tokens.isEmpty()) fail(where, "empty Equation")
            is Strip -> {
                if (v.items.isEmpty() || v.items.size > 12) fail(where, "Strip size ${v.items.size}")
                if (v.blankAt >= v.items.size) fail(where, "Strip blankAt out of range")
            }
            is NumberLine -> {
                if (v.from >= v.to) fail(where, "NumberLine range")
                val pts = listOfNotNull(v.start, v.mark) + v.hops
                if (pts.any { it < v.from || it > v.to }) fail(where, "NumberLine point outside ${v.from}..${v.to}: $pts")
            }
            is TenFrame -> if (v.filled !in 0..(v.frames * 10)) fail(where, "TenFrame ${v.filled}/${v.frames}")
            is Blocks -> if (v.hundreds > 9 || v.tens > 9 || v.ones > 9) fail(where, "Blocks digits")
            is ArrayGrid -> if (v.rows !in 1..12 || v.cols !in 1..12) fail(where, "ArrayGrid ${v.rows}x${v.cols}")
            is Pie -> if (v.parts < 1 || v.filled !in 0..v.parts) fail(where, "Pie ${v.filled}/${v.parts}")
            is FracBars -> v.bars.forEach { (p, f) -> if (p < 1 || f !in 0..p) fail(where, "FracBars $f/$p") }
            is Clock -> if (v.hour !in 1..12 || v.minute !in 0..59) fail(where, "Clock ${v.hour}:${v.minute}")
            is RectGrid -> if (v.w !in 1..14 || v.h !in 1..10) fail(where, "RectGrid ${v.w}x${v.h}")
            is Bars -> if (v.labels.size != v.values.size || v.values.isEmpty()) fail(where, "Bars size")
            is Money -> if (v.items.isEmpty() || v.items.any { it !in listOf(1, 2, 5, 10, 20, 50, 100, 200, 500) }) fail(where, "Money ${v.items}")
            is Board -> if (v.lines.isEmpty() || v.lines.size > 8) fail(where, "Board lines ${v.lines.size}")
            is Grid100 -> if (v.filled !in 0..100) fail(where, "Grid100 ${v.filled}")
            is ColumnMath -> if (v.a < 0 || v.b < 0 || (v.op == '-' && v.b > v.a) || (v.op == 'x' && v.b !in 0..9)) fail(where, "ColumnMath ${v.a}${v.op}${v.b}")
            is PlaceChart -> if (v.number < 0) fail(where, "PlaceChart")
            is Shape, is Solid, is AngleShape, is Balance, is BigText, is Ruler, is Empty -> Unit
        }
    }

    private fun checkStep(s: Step, where: String, inQuestion: Boolean = false) {
        if (s.say.isBlank()) fail(where, "blank narration")
        if (s.say.length > 150) fail(where, "narration too long for the caption (${s.say.length}): ${s.say}")
        walk(s.visual, inQuestion, where)
        if (s.from < 0) fail(where, "negative from")
        if (s.to < s.from && s.from != Step.ALL) fail(where, "to < from")
        if (s.counting && s.visual.maxProgress == 0) fail(where, "counting step with nothing to count: ${s.say}")
        if (s.counting && (s.from + 1..minOf(s.to, s.visual.maxProgress)).none { s.visual.spokenAt(it) != null })
            fail(where, "counting step that would never speak a number: ${s.say}")
    }

    @Test
    fun ids_are_unique() {
        val ids = Curriculum.lessons.map { it.id }
        assertEquals("duplicate lesson ids", ids.size, ids.toSet().size)
        val wids = Curriculum.worlds.map { it.id }
        assertEquals("duplicate world ids", wids.size, wids.toSet().size)
        assertTrue(Curriculum.lessons.isNotEmpty())
    }

    @Test
    fun every_teaching_step_is_well_formed() {
        for (l in Curriculum.lessons) {
            val where = "lesson ${l.id} teach"
            assertTrue("$where: ${l.teach.size} steps", l.teach.size in 3..16)
            assertTrue("$where: title", l.title.isNotBlank() && l.title.length <= 28)
            assertTrue("$where: blurb", l.blurb.isNotBlank() && l.blurb.length <= 48)
            l.teach.forEachIndexed { i, s -> checkStep(s, "$where[$i]") }
        }
    }

    @Test
    fun every_question_generator_produces_valid_questions() {
        for (l in Curriculum.lessons) {
            for (seed in 0 until seeds) {
                val round = try {
                    l.buildRound(Random(seed))
                } catch (e: Throwable) {
                    throw AssertionError("lesson ${l.id} seed $seed threw $e", e)
                }
                assertEquals("${l.id}: round size", l.questionCount, round.size)
                round.forEachIndexed { i, q ->
                    val where = "lesson ${l.id} seed $seed q$i (\"${q.prompt}\")"
                    if (q.prompt.isBlank() || q.prompt.length > 120) fail(where, "prompt length ${q.prompt.length}")
                    if (q.hint.isBlank() || q.hint.length > 150) fail(where, "hint length ${q.hint.length}")
                    if (q.choices.size !in 2..4) fail(where, "${q.choices.size} choices")
                    if (q.answer !in q.choices.indices) fail(where, "answer index ${q.answer}")
                    val texts = q.choices.map { it.text }
                    if (texts.toSet().size != texts.size) fail(where, "duplicate choices $texts")
                    if (texts.any { it.isBlank() }) fail(where, "blank choice text $texts")
                    if (texts.any { it.length > 34 }) fail(where, "choice too long for a button $texts")
                    q.choices.forEach { c -> c.visual?.let { walk(it, true, where) } }
                    q.visual?.let { walk(it, true, where) }
                    if (q.solution.isEmpty() || q.solution.size > 8) fail(where, "solution steps ${q.solution.size}")
                    q.solution.forEachIndexed { k, s -> checkStep(s, "$where solution[$k]") }
                }
            }
        }
    }

    @Test
    fun no_nonsense_answers_like_zero_degrees_or_negative_lengths() {
        // A measurement of "0°" or "−5 cm" means a generator produced an impossible shape. Only the
        // integers lessons may have negative answers.
        val negativeOk = setOf("negative-intro", "integer-add", "integer-sub")
        val zeroWithUnit = Regex("^0\\s?(°|[a-zA-Zμ²³]).*")
        for (l in Curriculum.lessons) {
            for (seed in 0 until seeds) {
                l.buildRound(Random(seed)).forEachIndexed { i, q ->
                    val a = q.choices[q.answer].text
                    val where = "lesson ${l.id} seed $seed q$i (\"${q.prompt}\") answer \"$a\""
                    if (zeroWithUnit.matches(a)) fail(where, "a zero measurement")
                    if (l.id !in negativeOk && (a.startsWith("-") || a.startsWith("−"))) fail(where, "negative answer outside the integers lessons")
                }
            }
        }
    }

    /** Emoji added after Unicode 8 show as an empty box on the older Android TV boxes we support. */
    private fun riskyEmoji(text: String): List<String> {
        val ok8 = (0x1F910..0x1F918).toSet() + (0x1F980..0x1F984).toSet() + 0x1F9C0
        val out = ArrayList<String>()
        var i = 0
        while (i < text.length) {
            val cp = text.codePointAt(i)
            i += Character.charCount(cp)
            val newer = when {
                cp == 0x200D -> true // joined emoji (families, people with jobs) fall apart on old fonts
                cp in 0x1F900..0x1F9FF -> cp !in ok8
                cp >= 0x1FA00 -> true
                cp in 0x1F6D1..0x1F6D7 || cp in 0x1F6F4..0x1F6FF -> true
                else -> false
            }
            if (newer) out.add("U+%X".format(cp))
        }
        return out
    }

    @Test
    fun emoji_are_safe_on_old_tv_boxes() {
        for (w in Curriculum.worlds) {
            assertTrue("world ${w.id} emoji ${w.emoji}", riskyEmoji(w.emoji).isEmpty())
            for (l in w.lessons) {
                assertTrue("lesson ${l.id} icon ${l.icon}", riskyEmoji(l.icon).isEmpty())
                l.teach.forEach { st -> assertTrue("lesson ${l.id} teach: ${riskyEmoji(st.toString())}", riskyEmoji(st.toString()).isEmpty()) }
                for (seed in 0 until 12) {
                    l.buildRound(Random(seed)).forEach { q ->
                        val all = q.toString()
                        assertTrue("lesson ${l.id} question \"${q.prompt}\": ${riskyEmoji(all)}", riskyEmoji(all).isEmpty())
                    }
                }
            }
        }
        assertTrue(Things.all.all { riskyEmoji(it.emoji).isEmpty() })
    }

    @Test
    fun rounds_get_easier_to_harder_not_identical() {
        // The same lesson asked twice must not always give the same round (practice is endless).
        for (l in Curriculum.lessons) {
            val a = l.buildRound(Random(1)).map { it.fingerprint() }
            val b = l.buildRound(Random(2)).map { it.fingerprint() }
            val distinct = (a + b).toSet().size
            assertTrue("${l.id}: two rounds were nearly identical ($distinct distinct questions)", distinct >= 4)
        }
    }

    @Test
    fun speakable_turns_symbols_into_words() {
        assertEquals("2 plus 3 equals 5", Words.speakable("2 + 3 = 5"))
        assertEquals("7 minus 2", Words.speakable("7 − 2"))
        assertEquals("4 times 5", Words.speakable("4 × 5"))
        assertEquals("12 divided by 4", Words.speakable("12 ÷ 4"))
        assertEquals("50 percent", Words.speakable("50%"))
        assertEquals("5 rupees", Words.speakable("₹5"))
        assertEquals("the ratio is 2 to 3", Words.speakable("the ratio is 2 : 3"))
        assertEquals("the time is 3:30", Words.speakable("the time is 3:30"))
        assertEquals("x plus 3 equals 8", Words.speakable("x + 3 = 8"))
        assertFalse(Words.speakable("Five apples 🍎🍎").contains("🍎"))
        assertEquals("twenty one", Words.number(21))
        assertEquals("one hundred five", Words.number(105))
        assertEquals("1,23,456", Words.indian(123456))
        assertEquals("12,34,56,789", Words.indian(123456789))
    }
}
