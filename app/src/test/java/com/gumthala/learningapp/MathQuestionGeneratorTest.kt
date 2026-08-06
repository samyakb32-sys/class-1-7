package com.gumthala.learningapp

import com.gumthala.learningapp.data.seed.GeneratedQuestion
import com.gumthala.learningapp.data.seed.MathQuestionGenerator
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

private const val ITERATIONS = 300

/** Structural invariant every generated question must satisfy, regardless of template. */
private fun assertWellFormed(q: GeneratedQuestion) {
    assertEquals(4, q.optionsEn.size, "expected 4 options, got: $q")
    assertEquals(4, q.optionsEn.toSet().size, "options must be distinct: $q")
    assertTrue(q.correctIndex in 0..3, "correctIndex out of range: $q")
    assertTrue(q.textEn.isNotBlank() && q.textMr.isNotBlank() && q.textHi.isNotBlank(), "all three languages must be non-blank: $q")
}

class MathQuestionGeneratorTest {

    @Test
    fun `addition is well-formed and arithmetically correct`() {
        val regex = Regex("""What is (\d+) \+ (\d+)\?""")
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.addition(Random(seed), maxOperand = 16)
            assertWellFormed(q)
            val (a, b) = regex.find(q.textEn)?.destructured ?: fail("unexpected format: ${q.textEn}")
            assertEquals((a.toInt() + b.toInt()).toString(), q.optionsEn[q.correctIndex])
        }
    }

    @Test
    fun `subtraction is well-formed and arithmetically correct`() {
        val regex = Regex("""What is (\d+) - (\d+)\?""")
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.subtraction(Random(seed), maxOperand = 100)
            assertWellFormed(q)
            val (a, b) = regex.find(q.textEn)?.destructured ?: fail("unexpected format: ${q.textEn}")
            assertEquals((a.toInt() - b.toInt()).toString(), q.optionsEn[q.correctIndex])
            assertTrue(a.toInt() - b.toInt() >= 0, "subtraction should never go negative: ${q.textEn}")
        }
    }

    @Test
    fun `multiplication is well-formed and arithmetically correct`() {
        val regex = Regex("""What is (\d+) × (\d+)\?""")
        repeat(ITERATIONS) { seed ->
            // table=1 is intentionally excluded: ContentSeeder/MathChapterPlan never generates a
            // "multiply by 1" chapter (ranges always start at 2), and numericOptions' effective
            // minimum spread of 3 (see MathQuestionGenerator) covers it defensively either way.
            val q = MathQuestionGenerator.multiplication(Random(seed), table = (seed % 11) + 2)
            assertWellFormed(q)
            val (a, b) = regex.find(q.textEn)?.destructured ?: fail("unexpected format: ${q.textEn}")
            assertEquals((a.toInt() * b.toInt()).toString(), q.optionsEn[q.correctIndex])
        }
    }

    @Test
    fun `division is well-formed and arithmetically correct`() {
        val regex = Regex("""What is (\d+) ÷ (\d+)\?""")
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.division(Random(seed), maxDivisor = 12)
            assertWellFormed(q)
            val (dividend, divisor) = regex.find(q.textEn)?.destructured ?: fail("unexpected format: ${q.textEn}")
            assertEquals(0, dividend.toInt() % divisor.toInt(), "dividend must divide evenly: ${q.textEn}")
            assertEquals((dividend.toInt() / divisor.toInt()).toString(), q.optionsEn[q.correctIndex])
        }
    }

    @Test
    fun `counting is well-formed and matches the number of pictures shown`() {
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.counting(Random(seed), maxCount = 20, emoji = "⭐")
            assertWellFormed(q)
            // The template is "How many ⭐ are there? ⭐⭐⭐..." - one ⭐ in the prompt itself,
            // then the repeated picture row after "? ". Only count the picture row.
            val pictureRow = q.textEn.substringAfter("there? ")
            val picturesShown = pictureRow.count { it == '⭐' }
            assertEquals(picturesShown.toString(), q.optionsEn[q.correctIndex])
            assertTrue(picturesShown in 3..20)
        }
    }

    @Test
    fun `comparison picks the actual greater or smaller of the two numbers shown`() {
        val regex = Regex("""Which number is (greater|smaller): (\d+) or (\d+)\?""")
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.comparison(Random(seed), maxValue = 500)
            assertWellFormed(q)
            val m = regex.find(q.textEn) ?: fail("unexpected format: ${q.textEn}")
            val (mode, aStr, bStr) = m.destructured
            val a = aStr.toInt(); val b = bStr.toInt()
            val expected = if (mode == "greater") maxOf(a, b) else minOf(a, b)
            assertEquals(expected.toString(), q.optionsEn[q.correctIndex])
            val other = if (expected == a) b else a
            assertTrue(other.toString() in q.optionsEn, "the non-answer number from the question should still appear as a distractor")
        }
    }

    @Test
    fun `placeValue reports the actual tens or hundreds digit`() {
        val regex = Regex("""What is the (tens|hundreds) digit in (\d+)\?""")
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.placeValue(Random(seed), maxValue = 5000)
            assertWellFormed(q)
            val m = regex.find(q.textEn) ?: fail("unexpected format: ${q.textEn}")
            val (place, nStr) = m.destructured
            val n = nStr.toInt()
            val expected = if (place == "tens") (n / 10) % 10 else (n / 100) % 10
            assertEquals(expected.toString(), q.optionsEn[q.correctIndex])
        }
    }

    @Test
    fun `fractionShaded label matches shaded-over-total shown in the picture`() {
        val regex = Regex("""(\d+)/(\d+)""")
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.fractionShaded(Random(seed))
            assertWellFormed(q)
            val greenCount = q.textEn.codePoints().toArray().count { it == 0x1F7E9 } // 🟩
            val (shaded, total) = regex.find(q.optionsEn[q.correctIndex])?.destructured ?: fail("bad option format")
            assertEquals(greenCount, shaded.toInt())
            assertTrue(shaded.toInt() < total.toInt())
        }
    }

    @Test
    fun `percentage is arithmetically correct`() {
        val regex = Regex("""What is (\d+)% of (\d+)\?""")
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.percentage(Random(seed))
            assertWellFormed(q)
            val (p, n) = regex.find(q.textEn)?.destructured ?: fail("unexpected format: ${q.textEn}")
            assertEquals((n.toInt() * p.toInt() / 100).toString(), q.optionsEn[q.correctIndex])
        }
    }

    @Test
    fun `ratioSimplify reduces to lowest terms`() {
        val regex = Regex("""Simplify the ratio (\d+):(\d+)""")
        fun gcd(x: Int, y: Int): Int = if (y == 0) x else gcd(y, x % y)
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.ratioSimplify(Random(seed))
            assertWellFormed(q)
            val (aStr, bStr) = regex.find(q.textEn)?.destructured ?: fail("unexpected format: ${q.textEn}")
            val a = aStr.toInt(); val b = bStr.toInt()
            val g = gcd(a, b)
            assertEquals("${a / g}:${b / g}", q.optionsEn[q.correctIndex])
        }
    }

    @Test
    fun `linearEquation solves for x correctly`() {
        val regex = Regex("""Solve for x: x \+ (\d+) = (\d+)""")
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.linearEquation(Random(seed))
            assertWellFormed(q)
            val (a, b) = regex.find(q.textEn)?.destructured ?: fail("unexpected format: ${q.textEn}")
            assertEquals((b.toInt() - a.toInt()).toString(), q.optionsEn[q.correctIndex])
        }
    }

    @Test
    fun `rectanglePerimeter is arithmetically correct`() {
        val regex = Regex("""length (\d+) and width (\d+)""")
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.rectanglePerimeter(Random(seed))
            assertWellFormed(q)
            val (l, w) = regex.find(q.textEn)?.destructured ?: fail("unexpected format: ${q.textEn}")
            assertEquals((2 * (l.toInt() + w.toInt())).toString(), q.optionsEn[q.correctIndex])
        }
    }

    @Test
    fun `rectangleArea is arithmetically correct`() {
        val regex = Regex("""length (\d+) and width (\d+)""")
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.rectangleArea(Random(seed))
            assertWellFormed(q)
            val (l, w) = regex.find(q.textEn)?.destructured ?: fail("unexpected format: ${q.textEn}")
            assertEquals((l.toInt() * w.toInt()).toString(), q.optionsEn[q.correctIndex])
        }
    }

    @Test
    fun `average is arithmetically correct (integer division matches Kotlin semantics)`() {
        val regex = Regex("""Find the average of (\d+), (\d+), and (\d+)\.""")
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.average(Random(seed))
            assertWellFormed(q)
            val (a, b, c) = regex.find(q.textEn)?.destructured ?: fail("unexpected format: ${q.textEn}")
            assertEquals(((a.toInt() + b.toInt() + c.toInt()) / 3).toString(), q.optionsEn[q.correctIndex])
        }
    }

    @Test
    fun `moneySum is arithmetically correct`() {
        val regex = Regex("""costs ₹(\d+) and a book costs ₹(\d+)""")
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.moneySum(Random(seed))
            assertWellFormed(q)
            val (a, b) = regex.find(q.textEn)?.destructured ?: fail("unexpected format: ${q.textEn}")
            assertEquals("₹${a.toInt() + b.toInt()}", q.optionsEn[q.correctIndex])
        }
    }

    @Test
    fun `shapeSides matches a known geometric fact`() {
        val known = mapOf("triangle" to 3, "square" to 4, "rectangle" to 4, "pentagon" to 5, "hexagon" to 6)
        val regex = Regex("""How many sides does a (\w+) have\?""")
        repeat(ITERATIONS) { seed ->
            val q = MathQuestionGenerator.shapeSides(Random(seed))
            assertWellFormed(q)
            val shape = regex.find(q.textEn)?.destructured?.component1() ?: fail("unexpected format: ${q.textEn}")
            assertEquals(known.getValue(shape).toString(), q.optionsEn[q.correctIndex])
        }
    }
}
