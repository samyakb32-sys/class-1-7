package com.gumthala.learningapp.tv.content

import kotlin.random.Random

/*
 * World 6 - Fraction Bakery. Fractions from "half a cake" to adding and multiplying, then decimals and
 * percentages. Wrong answers are checked so that none of them secretly equals the right one.
 */

private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
private fun fr(n: Int, d: Int) = "$n/$d"
private fun sameValue(a: Int, b: Int, c: Int, d: Int) = a * d == b * c

private val denomWord = mapOf(2 to "half", 3 to "third", 4 to "quarter", 5 to "fifth", 6 to "sixth", 7 to "seventh", 8 to "eighth", 9 to "ninth", 10 to "tenth")

/** Spoken name of a fraction: "3 quarters", "1 half", "2 fifths". */
internal fun fracWords(n: Int, d: Int): String {
    val w = denomWord[d] ?: "part"
    val plural = if (n == 1) w else if (d == 2) "halves" else w + "s"
    return "${Words.number(n)} $plural"
}

/** Up to three fractions that look plausible but are NOT equal to [n]/[d]. */
private fun fractionWrongs(rnd: Random, n: Int, d: Int, maxDen: Int = 12): List<String> {
    val cands = listOf(n + 1 to d, n - 1 to d, n to d + 1, n to d - 1, d - n to d, n + 1 to d + 1, n - 1 to d - 1, n to d * 2)
    val ok = cands.filter { (a, b) -> a >= 1 && b >= 2 && b <= maxDen && a < b && !sameValue(a, b, n, d) }.distinct().shuffled(rnd)
    return ok.take(3).map { fr(it.first, it.second) }
}

private fun fmtDec(hundredths: Int): String = "${hundredths / 100}.${(hundredths % 100).toString().padStart(2, '0')}"

val halfQuarter = lesson(
    "half-quarter", "Halves and quarters", "Sharing things into equal parts", "🍕",
    teach = listOf(
        say("Fractions are parts of a whole. Here is a pizza.", Pie(1, 0)),
        say("Cut it into two equal parts. Each part is one half.", Pie(2, 1, label = true)),
        say("Cut it into four equal parts. Each part is one quarter.", Pie(4, 1, label = true)),
        say("Three quarters of the pizza are left.", Pie(4, 3, label = true)),
        show("Equal parts matter! Both halves must be the same size.", Side(listOf(Pie(2, 1), Pie(4, 2)), listOf("=")), Mood.CHEER),
    ),
) { rnd, i ->
    val parts = rnd.choose(if (band(i) == 0) listOf(2, 4) else listOf(2, 3, 4, 4))
    val filled = if (parts == 2) 1 else rnd.between(1, parts - 1).let { if (parts == 4 && it == 2) 3 else it }   // never 2/4: it equals 1/2
    val correct = fr(filled, parts)
    val food = rnd.choose(listOf("pizza", "cake", "roti", "pancake"))
    val pool = listOf("1/2", "1/3", "2/3", "1/4", "3/4").filter { it != correct }
    mcText(
        rnd, "What fraction of the $food is coloured?", Pie(parts, filled), correct, pool.shuffled(rnd).take(3),
        "Count all the equal parts, then count the coloured ones.",
        listOf(say("There are $parts equal parts, and $filled ${if (filled == 1) "is" else "are"} coloured.", Pie(parts, filled, label = true)), show("So ${fracWords(filled, parts)} ${if (filled == 1) "is" else "are"} coloured: $correct.", Pie(parts, filled, label = true), Mood.CHEER)),
    )
}

val fractionParts = lesson(
    "fraction-parts", "Reading fractions", "Top number and bottom number", "🔤",
    teach = listOf(
        say("A fraction has two numbers. The bottom number says how many equal parts.", Pie(5, 0, label = false)),
        say("The top number says how many parts we have. Here we have three.", Pie(5, 3, label = true)),
        show("Three out of five equal parts: three fifths.", Stack(Pie(5, 3), eq("3", "/", "5")), Mood.CHEER),
        show("Top is the numerator. Bottom is the denominator.", Board(listOf(hot("top: numerator (parts we have)"), line("bottom: denominator (equal parts in all)")))),
    ),
) { rnd, i ->
    val d = rnd.between(3, if (band(i) == 0) 4 else if (band(i) == 1) 6 else 8)
    val n = rnd.between(1, d - 1)
    mcText(
        rnd, "What fraction is coloured?", Pie(d, n), fr(n, d), fractionWrongs(rnd, n, d),
        "Bottom: how many equal parts in all. Top: how many are coloured.",
        listOf(say("There are $d equal parts in all, and $n ${if (n == 1) "is" else "are"} coloured.", Pie(d, n, label = true)), show("So the fraction is $n over $d: ${fracWords(n, d)}.", Pie(d, n, label = true), Mood.CHEER)),
    )
}

val fractionSet = lesson(
    "fraction-set", "Fractions of a group", "Part of a set of things", "🍓",
    teach = listOf(
        say("Fractions work for groups too. Here are four strawberries, and three are in the basket.", Counters("🍓", 4, CounterMode.STATIC, split = 3)),
        show("Three out of four. That is three quarters of the strawberries.", Stack(Counters("🍓", 4, CounterMode.STATIC, split = 3), eq("3", "/", "4")), Mood.CHEER),
        show("The total number goes on the bottom. The ones we talk about go on top.", Board(listOf(line("3 in the basket"), line("4 altogether"), good("3/4")))),
    ),
) { rnd, i ->
    val t = Things.pickEasy(rnd)
    val total = rnd.between(4, if (band(i) == 0) 6 else 10)
    val part = rnd.between(1, total - 1)
    mcText(
        rnd, "What fraction of the ${t.many} are in the blue boxes?", Counters(t.emoji, total, CounterMode.STATIC, split = part, cols = 5), fr(part, total), fractionWrongs(rnd, part, total),
        "Count the blue ones for the top and count them all for the bottom.",
        listOf(show("$part of the $total are blue, so the fraction is $part/$total.", Stack(Counters(t.emoji, total, CounterMode.STATIC, split = part, cols = 5), eq("$part", "/", "$total")), Mood.CHEER)),
    )
}

val compareFractions = lesson(
    "compare-fractions", "Which fraction is bigger?", "Comparing parts", "⚖️",
    teach = listOf(
        say("Same bottom number? Then just compare the top numbers.", FracBars(listOf(8 to 3, 8 to 5), listOf("3/8", "5/8"))),
        show("Five eighths is more than three eighths.", Stack(FracBars(listOf(8 to 3, 8 to 5), listOf("3/8", "5/8")), eq("3/8", "<", "5/8")), Mood.CHEER),
        say("Same top number? Then look at the bottom. Bigger bottom means smaller pieces!", FracBars(listOf(3 to 1, 5 to 1), listOf("1/3", "1/5"))),
        show("One third is bigger than one fifth.", eq("1/3", ">", "1/5"), Mood.CHEER),
    ),
) { rnd, i ->
    if (band(i) < 2 || i % 2 == 0) {
        val d = rnd.between(4, 10)
        val a = rnd.between(1, d - 1)
        var b = rnd.between(1, d - 1)
        if (b == a) b = if (a < d - 1) a + 1 else a - 1
        val big = maxOf(a, b)
        mcText(
            rnd, "Which is bigger: ${fr(a, d)} or ${fr(b, d)}?", if (band(i) == 0) FracBars(listOf(d to a, d to b), listOf(fr(a, d), fr(b, d))) else null,
            fr(big, d), listOf(fr(minOf(a, b), d)), "The bottoms are the same, so compare the tops: $a and $b.",
            listOf(show("$big is more than ${minOf(a, b)}, so ${fr(big, d)} is bigger.", FracBars(listOf(d to a, d to b), listOf(fr(a, d), fr(b, d))), Mood.CHEER)),
        )
    } else {
        val n = rnd.between(1, 2)
        val d1 = rnd.between(n + 1, 8)
        var d2 = rnd.between(n + 1, 9)
        if (d2 == d1) d2 = if (d1 < 9) d1 + 1 else d1 - 1
        val bigger = if (d1 < d2) fr(n, d1) else fr(n, d2)
        val smaller = if (d1 < d2) fr(n, d2) else fr(n, d1)
        mcText(
            rnd, "Which is bigger: ${fr(n, d1)} or ${fr(n, d2)}?", null, bigger, listOf(smaller),
            "Same top number: the smaller bottom number means bigger pieces.",
            listOf(show("Cut into fewer parts, each part is bigger. So $bigger is bigger.", FracBars(listOf(d1 to n, d2 to n), listOf(fr(n, d1), fr(n, d2))), Mood.CHEER)),
        )
    }
}

val equivFractions = lesson(
    "equiv-fractions", "Equivalent fractions", "Same amount, different name", "🟰",
    teach = listOf(
        say("Look at these bars. Half of a bar...", FracBars(listOf(2 to 1), listOf("1/2"))),
        say("...is the same as two quarters...", FracBars(listOf(2 to 1, 4 to 2), listOf("1/2", "2/4"))),
        say("...and the same as four eighths.", FracBars(listOf(2 to 1, 4 to 2, 8 to 4), listOf("1/2", "2/4", "4/8")), mood = Mood.CHEER),
        show("Multiply the top and the bottom by the same number and the fraction stays equal.", Board(listOf(line("1/2"), dim("× 2 on top and bottom"), line("= 2/4"), dim("× 2 again"), good("= 4/8")))),
    ),
) { rnd, i ->
    val q = rnd.choose(listOf(2, 3, 4, 5))
    val p = rnd.choose((1 until q).filter { gcd(it, q) == 1 })
    val k = rnd.between(2, if (band(i) == 0) 3 else 4)
    val bars = FracBars(listOf(q to p, q * k to p * k), listOf(fr(p, q), fr(p * k, q * k)))
    val solution = listOf(
        say("Multiply the top and the bottom by the same number: $k.", Board(listOf(line(fr(p, q)), dim("× $k on top and bottom"), good("= ${fr(p * k, q * k)}")))),
        show("${fr(p, q)} is equal to ${fr(p * k, q * k)}.", bars, Mood.CHEER),
    )
    if (i % 2 == 0) {
        mcNumber(rnd, "${fr(p, q)} = ?/${q * k}", if (band(i) == 0) bars else null, p * k, "The bottom was multiplied by $k. Do the same to the top.", solution, spread = 2, min = 1, max = 20, extra = listOf(p + k, p, p * k + 1))
    } else {
        mcNumber(rnd, "${fr(p, q)} = ${p * k}/?", null, q * k, "The top was multiplied by $k. Do the same to the bottom.", solution, spread = 2, min = 2, max = 24, extra = listOf(q + k, q, q * k + 1))
    }
}

val addFractions = lesson(
    "add-fractions", "Adding fractions", "Same bottom: add the tops", "➕",
    teach = listOf(
        say("Adding fractions with the same bottom is easy. One fifth plus two fifths.", Side(listOf(Pie(5, 1), Pie(5, 2)), listOf("+"))),
        say("The pieces are the same size, so just count them: one plus two is three.", Pie(5, 3, label = true)),
        show("One fifth plus two fifths is three fifths. The bottom stays the same!", eq("1/5", "+", "2/5", "=", "3/5"), Mood.CHEER),
        show("Never add the bottoms. The pieces stay the same size.", Board(listOf(good("1/5 + 2/5 = 3/5"), hot("NOT 3/10")))),
    ),
) { rnd, i ->
    val d = rnd.between(4, if (band(i) == 0) 6 else 10)
    val a = rnd.between(1, d - 2)
    val b = rnd.between(1, d - 1 - a)
    val c = a + b
    val wrongs = listOf(fr(c, d * 2), fr(c + 1, d), fr(c - 1, d), fr(a, d + b)).filter { w -> w != fr(c, d) && !w.startsWith("0/") }.distinct().shuffled(rnd).take(3)
    mcText(
        rnd, "What is ${fr(a, d)} + ${fr(b, d)}?", Side(listOf(Pie(d, a), Pie(d, b)), listOf("+")), fr(c, d), wrongs,
        "The bottoms are the same. Add the tops and keep the bottom.",
        listOf(say("Same size pieces: $a plus $b is $c pieces.", Pie(d, c, label = true)), show("${fr(a, d)} + ${fr(b, d)} = ${fr(c, d)}.", eq(fr(a, d), "+", fr(b, d), "=", fr(c, d)), Mood.CHEER)),
    )
}

val subFractions = lesson(
    "sub-fractions", "Subtracting fractions", "Same bottom: take away the tops", "➖",
    teach = listOf(
        say("Subtracting is the same idea. Five eighths take away two eighths.", Pie(8, 5, label = true)),
        say("Take away two of the coloured pieces.", Pie(8, 3, label = true)),
        show("Five eighths minus two eighths is three eighths.", eq("5/8", "−", "2/8", "=", "3/8"), Mood.CHEER),
    ),
) { rnd, i ->
    val d = rnd.between(4, if (band(i) == 0) 6 else 10)
    val a = rnd.between(3, d - 1)
    val b = rnd.between(1, a - 1)
    val c = a - b
    val wrongs = listOf(fr(c, d * 2), fr(c + 1, d), fr((c - 1).coerceAtLeast(1), d), fr(a + b, d)).filter { w -> w != fr(c, d) && w.substringBefore('/').toInt() <= w.substringAfter('/').toInt() }.distinct().shuffled(rnd).take(3)
    mcText(
        rnd, "What is ${fr(a, d)} − ${fr(b, d)}?", Pie(d, a), fr(c, d), wrongs,
        "Same bottom: take away the tops, keep the bottom.",
        listOf(say("$a pieces, take away $b, leaves $c.", Pie(d, c, label = true)), show("${fr(a, d)} − ${fr(b, d)} = ${fr(c, d)}.", eq(fr(a, d), "−", fr(b, d), "=", fr(c, d)), Mood.CHEER)),
    )
}

val fractionOfNumber = lesson(
    "fraction-of-number", "Fraction of a number", "Find a quarter, a third...", "🧁",
    teach = listOf(
        say("What is a quarter of twelve? Share twelve into four equal groups.", Side(List(4) { Counters("🍪", 3, CounterMode.STATIC) })),
        say("Each group has three. One quarter of twelve is three.", Stack(Side(List(4) { Counters("🍪", 3, CounterMode.STATIC) }), eq("12", "÷", "4", "=", "3"))),
        say("What about three quarters? Take three of the groups: three times three.", eq("3", "×", "3", "=", "9")),
        show("To find a fraction of a number: divide by the bottom, then multiply by the top.", Board(listOf(line("3/4 of 12"), dim("12 ÷ 4 = 3"), good("3 × 3 = 9")))),
    ),
) { rnd, i ->
    val d = rnd.choose(listOf(2, 3, 4, 5))
    val n = if (band(i) == 0) 1 else rnd.between(1, d - 1)
    val m = rnd.between(2, if (d >= 4) 6 else 8)
    val total = d * m
    mcNumber(
        rnd, "What is ${fr(n, d)} of $total?", Counters("🍪", total, CounterMode.STATIC, cols = 6).takeIf { total <= 30 }, n * m,
        "Divide $total by $d first, then multiply by $n.",
        listOf(
            say("Share $total into $d equal groups: $total ÷ $d = $m.", Board(listOf(line("${fr(n, d)} of $total"), hot("$total ÷ $d = $m")))),
            show("Take $n of those groups: $n × $m = ${n * m}.", Board(listOf(line("${fr(n, d)} of $total"), dim("$total ÷ $d = $m"), good("$n × $m = ${n * m}"))), Mood.CHEER),
        ),
        spread = 2, min = 1, max = 40, extra = listOf(m, total - n * m, n * m + 1, n * m - 1).filter { it > 0 },
    )
}

val mixedNumbers = lesson(
    "mixed-numbers", "Mixed numbers", "Whole numbers and fractions together", "🥧",
    teach = listOf(
        say("Sometimes we have more than one whole. Here are seven quarters.", Side(listOf(Pie(4, 4), Pie(4, 3)))),
        say("Four quarters make one whole pie. Then three quarters are left.", Side(listOf(Pie(4, 4), Pie(4, 3)), listOf("+"))),
        show("Seven quarters is one and three quarters. We write 7/4 = 1 3/4.", eq("7/4", "=", "1 3/4"), Mood.CHEER),
        show("To change: divide the top by the bottom. The answer is the whole number, the remainder is the new top.", Board(listOf(line("7 ÷ 4 = 1 remainder 3"), good("7/4 = 1 3/4")))),
    ),
) { rnd, i ->
    val d = rnd.between(2, if (band(i) == 0) 4 else 6)
    val w = rnd.between(1, 3)
    val r = rnd.between(1, d - 1)
    val top = d * w + r
    val mixed = "$w $r/$d"
    val solution = listOf(
        say("Divide $top by $d: ${if (w == 1) "1 whole" else "$w wholes"}, and $r left over.", Board(listOf(line("$top ÷ $d = $w remainder $r"), good("$top/$d = $mixed")))),
        show("So ${fr(top, d)} is the same as $mixed.", eq(fr(top, d), "=", mixed), Mood.CHEER),
    )
    if (i % 2 == 0) {
        val wrongs = listOf("${w + 1} $r/$d", "$w ${r + 1}/$d", "$w ${r - 1}/$d", "${w + 1} $r/${d + 1}", "${maxOf(w - 1, 1)} $r/$d", "$w $r/${d + 1}")
            .filter { c -> c != mixed && c.substringAfter(' ').let { f -> f.substringBefore('/').toInt() in 1 until f.substringAfter('/').toInt() } }
            .distinct().shuffled(rnd).take(3)
        mcText(rnd, "Write ${fr(top, d)} as a mixed number.", null, mixed, wrongs, "Divide $top by $d. The answer is the whole part. The remainder goes on top.", solution)
    } else {
        val wrongs = listOf(fr(w + r, d), fr(top + 1, d), fr(w * r + d, d), fr(top - 1, d)).filter { it != fr(top, d) }.distinct().shuffled(rnd).take(3)
        mcText(rnd, "Write $mixed as a fraction.", null, fr(top, d), wrongs, "Whole number × bottom, plus the top: $w × $d + $r.", listOf(
            say("Turn the $w wholes into ${fracWords(w * d, d)}, then add the $r more.", Board(listOf(line(mixed), dim("$w × $d = ${w * d}"), line("${w * d} + $r = $top"), good(fr(top, d))))),
            show("So $mixed = ${fr(top, d)}.", eq(mixed, "=", fr(top, d)), Mood.CHEER),
        ))
    }
}

val simplify = lesson(
    "simplify-fractions", "Simplest form", "Make a fraction as small as possible", "✂️",
    teach = listOf(
        say("A fraction can often be written with smaller numbers. Take four eighths.", FracBars(listOf(8 to 4, 2 to 1), listOf("4/8", "1/2"))),
        say("Divide the top and the bottom by the same number. Four goes into both four and eight.", Board(listOf(line("4/8"), dim("÷ 4 on top and bottom"), good("= 1/2")))),
        show("When nothing else divides both numbers, the fraction is in its simplest form.", Board(listOf(line("6/9 ÷ 3 = 2/3"), good("2 and 3 have no common factor")))),
    ),
) { rnd, i ->
    val q = rnd.choose(listOf(2, 3, 4, 5, 6, 7))
    val p = rnd.choose((1 until q).filter { gcd(it, q) == 1 })
    val k = rnd.between(2, if (band(i) == 0) 3 else 5)
    val n = p * k
    val d = q * k
    val wrongs = listOf(fr(p, d), fr(n, q), fr(p + 1, q), fr(p, q + 1), fr(n / gcd(n, d) + 1, d / gcd(n, d))).filter { w ->
        val (a, b) = w.split('/').map { it.toInt() }
        b >= 2 && a >= 1 && a < b && !sameValue(a, b, p, q)
    }.distinct().shuffled(rnd).take(3)
    mcText(
        rnd, "Write ${fr(n, d)} in its simplest form.", null, fr(p, q), wrongs,
        "Find a number that goes into both $n and $d, and divide both by it.",
        listOf(
            say("$k goes into both $n and $d.", Board(listOf(line(fr(n, d)), dim("÷ $k on top and bottom")))),
            show("${fr(n, d)} = ${fr(p, q)}.", Board(listOf(line(fr(n, d)), dim("÷ $k on top and bottom"), good("= ${fr(p, q)}"))), Mood.CHEER),
        ),
    )
}

val addUnlike = lesson(
    "add-unlike", "Adding different bottoms", "Make the bottoms match first", "🔄",
    teach = listOf(
        say("What is a half plus a quarter? The pieces are different sizes.", FracBars(listOf(2 to 1, 4 to 1), listOf("1/2", "1/4"))),
        say("Turn the half into quarters, so the pieces match.", FracBars(listOf(2 to 1, 4 to 2), listOf("1/2", "2/4"))),
        say("Now add: two quarters plus one quarter is three quarters.", eq("2/4", "+", "1/4", "=", "3/4")),
        show("Make the bottoms the same first, then add the tops.", Board(listOf(line("1/2 + 1/4"), dim("1/2 = 2/4"), line("2/4 + 1/4"), good("= 3/4"))), Mood.CHEER),
    ),
) { rnd, _ ->
    var d1: Int; var k: Int; var a: Int; var b: Int; var d2: Int; var num: Int
    var guard = 0
    do {
        d1 = rnd.between(2, 5); k = rnd.between(2, 3); d2 = d1 * k
        a = rnd.between(1, d1 - 1); b = rnd.between(1, d2 - 1)
        num = a * k + b
        guard++
    } while ((num >= d2 || gcd(num, d2) != 1) && guard < 200)
    if (num >= d2 || gcd(num, d2) != 1) { d1 = 2; k = 2; d2 = 4; a = 1; b = 1; num = 3 }
    val ans = fr(num, d2)
    val wrongs = listOf(fr(a + b, d1 + d2), fr(a + b, d2), fr(num + 1, d2), fr(num - 1, d2), fr(a + b, d1)).filter { w ->
        val (x, y) = w.split('/').map { it.toInt() }
        y >= 2 && x >= 1 && !sameValue(x, y, num, d2)
    }.distinct().shuffled(rnd).take(3)
    mcText(
        rnd, "What is ${fr(a, d1)} + ${fr(b, d2)}?", null, ans, wrongs,
        "Change ${fr(a, d1)} into ${d2}ths first, so both have the same bottom.",
        listOf(
            say("Multiply the top and bottom of ${fr(a, d1)} by $k to get ${fr(a * k, d2)}.", Board(listOf(line("${fr(a, d1)} + ${fr(b, d2)}"), dim("${fr(a, d1)} = ${fr(a * k, d2)}")))),
            show("Now add: ${fr(a * k, d2)} + ${fr(b, d2)} = $ans.", Board(listOf(line("${fr(a, d1)} + ${fr(b, d2)}"), dim("${fr(a, d1)} = ${fr(a * k, d2)}"), line("${fr(a * k, d2)} + ${fr(b, d2)}"), good("= $ans"))), Mood.CHEER),
        ),
    )
}

val multFractions = lesson(
    "mult-fractions", "Multiplying fractions", "Top times top, bottom times bottom", "✖️",
    teach = listOf(
        say("What is half of a half? Shade half of a half of a bar.", FracBars(listOf(2 to 1, 4 to 1), listOf("1/2", "1/2 of 1/2"))),
        say("It is one quarter! Multiply the tops, and multiply the bottoms.", Board(listOf(line("1/2 × 1/2"), dim("tops: 1 × 1 = 1"), dim("bottoms: 2 × 2 = 4"), good("= 1/4")))),
        show("Another: two thirds times three quarters.", Board(listOf(line("2/3 × 3/4"), dim("tops: 2 × 3 = 6"), dim("bottoms: 3 × 4 = 12"), line("6/12 = 1/2")))),
    ),
) { rnd, _ ->
    var a: Int; var b: Int; var c: Int; var d: Int
    var guard = 0
    do {
        b = rnd.between(2, 5); d = rnd.between(2, 5)
        a = rnd.between(1, b - 1); c = rnd.between(1, d - 1)
        guard++
    } while (gcd(a * c, b * d) != 1 && guard < 300)
    if (gcd(a * c, b * d) != 1) { a = 1; b = 2; c = 1; d = 3 }
    val ans = fr(a * c, b * d)
    val wrongs = listOf(fr(a + c, b + d), fr(a * c, b + d), fr(a + c, b * d), fr(a * c + 1, b * d)).filter { w ->
        val (x, y) = w.split('/').map { it.toInt() }
        !sameValue(x, y, a * c, b * d) && x >= 1 && y >= 2
    }.distinct().shuffled(rnd).take(3)
    mcText(
        rnd, "What is ${fr(a, b)} × ${fr(c, d)}?", null, ans, wrongs,
        "Multiply the tops together and the bottoms together.",
        listOf(show("Tops: $a × $c = ${a * c}. Bottoms: $b × $d = ${b * d}.", Board(listOf(line("${fr(a, b)} × ${fr(c, d)}"), dim("tops: $a × $c = ${a * c}"), dim("bottoms: $b × $d = ${b * d}"), good("= $ans"))), Mood.CHEER)),
    )
}

val decimalTenths = lesson(
    "decimal-tenths", "Tenths and decimals", "The decimal point", "🔟",
    teach = listOf(
        say("Cut a whole into ten equal parts. Each part is one tenth.", FracBars(listOf(10 to 1), listOf("1/10"))),
        say("We can write one tenth as a decimal: zero point one.", Board(listOf(line("1/10"), good("= 0.1")))),
        say("Three tenths is zero point three.", FracBars(listOf(10 to 3), listOf("0.3"))),
        show("The dot is called the decimal point. Digits after it are parts of a whole.", Board(listOf(line("3/10 = 0.3"), line("7/10 = 0.7"), good("10/10 = 1")))),
    ),
) { rnd, i ->
    val n = rnd.between(1, 9)
    val dec = "0.$n"
    when (i % 3) {
        0 -> mcText(rnd, "What decimal is coloured?", FracBars(listOf(10 to n)), dec, (1..9).filter { it != n }.shuffled(rnd).take(3).map { "0.$it" }, "Count the coloured tenths: that is the digit after the point.", listOf(show("$n ${if (n == 1) "tenth" else "tenths"} is $dec.", FracBars(listOf(10 to n), listOf(dec)), Mood.CHEER)))
        1 -> mcText(rnd, "Write ${fr(n, 10)} as a decimal.", null, dec, (1..9).filter { it != n }.shuffled(rnd).take(3).map { "0.$it" } , "Tenths go in the first place after the point.", listOf(show("${fr(n, 10)} = $dec.", eq(fr(n, 10), "=", dec), Mood.CHEER)))
        else -> mcNumber(rnd, "$dec = ?/10", null, n, "The digit after the point tells how many tenths.", listOf(show("$dec is $n ${if (n == 1) "tenth" else "tenths"}: ${fr(n, 10)}.", eq(dec, "=", fr(n, 10)), Mood.CHEER)), spread = 2, min = 1, max = 9)
    }
}

val decimalPlace = lesson(
    "decimal-place", "Decimal places", "Tenths and hundredths", "🔬",
    teach = listOf(
        say("Look at 3.47. The digits after the point are parts of a whole.", Board(listOf(line("3 . 4 7"), dim("ones . tenths hundredths")))),
        say("The 3 is three ones. The 4 is four tenths.", Board(listOf(line("3 . 4 7"), hot("3 = three ones"), hot("4 = four tenths")))),
        show("The 7 is seven hundredths. So 3.47 is 3 + 0.4 + 0.07.", Board(listOf(line("3 . 4 7"), dim("3 ones"), dim("4 tenths"), good("7 hundredths"))), Mood.CHEER),
    ),
) { rnd, i ->
    val (a, t, h) = (1..9).shuffled(rnd).take(3)   // three different digits, so "the 4" is never ambiguous
    val n = "$a.$t$h"
    val which = rnd.between(1, 2)   // 1 = tenths digit, 2 = hundredths digit
    val digit = if (which == 1) t else h
    val place = if (which == 1) "tenths" else "hundredths"
    val wrongs = listOf("ones", "tenths", "hundredths", "tens").filter { it != place }
    mcText(
        rnd, "In $n, what does the $digit stand for?", Board(listOf(line(n))), "$digit $place", wrongs.shuffled(rnd).take(3).map { "$digit $it" },
        "Count the places after the point: tenths first, then hundredths.",
        listOf(show("After the point: first place is tenths, second is hundredths. The $digit is in the $place place.", Board(listOf(line(n), dim("first after point: tenths"), dim("second: hundredths"), good("$digit $place"))), Mood.CHEER)),
    )
}

val compareDecimals = lesson(
    "compare-decimals", "Which decimal is bigger?", "Compare digit by digit", "🔍",
    teach = listOf(
        say("Which is bigger, 0.6 or 0.45? Compare the tenths first.", Board(listOf(line("0.6  and  0.45"), dim("tenths: 6 and 4")))),
        say("Six tenths is more than four tenths. So 0.6 is bigger.", Board(listOf(line("0.6  and  0.45"), dim("tenths: 6 and 4"), good("0.6 > 0.45")))),
        show("A longer decimal is not always bigger. Compare the digits from the left.", Board(listOf(line("0.6 = 0.60"), line("0.60 > 0.45"), hot("writing 0.6 as 0.60 helps")))),
    ),
) { rnd, i ->
    val a = rnd.between(1, 9) * 10 + if (rnd.nextBoolean()) rnd.between(1, 9) else 0
    var b = rnd.between(1, 9) * 10 + rnd.between(1, 9)
    if (a == b) b += 1
    val sa = fmtDec(a).trimEnd('0').trimEnd('.'); val sb = fmtDec(b).trimEnd('0').trimEnd('.')
    val bigger = if (a > b) sa else sb
    val smaller = if (a > b) sb else sa
    mcText(
        rnd, "Which is bigger: $sa or $sb?", null, bigger, listOf(smaller),
        "Write them with the same number of decimal places, then compare.",
        listOf(show("${fmtDec(a)} and ${fmtDec(b)}: compare the tenths first. $bigger is bigger.", Board(listOf(line("${fmtDec(a)}  and  ${fmtDec(b)}"), good("$bigger is bigger"))), Mood.CHEER)),
    )
}

val addDecimals = lesson(
    "add-decimals", "Decimal sums", "Add and subtract: line up the points", "🧾",
    teach = listOf(
        say("To add decimals, line up the decimal points. Try 2.50 plus 1.75.", Board(listOf(line("2.50 + 1.75")))),
        say("Ignore the points for a moment and add 250 plus 175.", ColumnMath(250, 175, '+')),
        say("The answer is 425. Put the point back, two places from the right.", Board(listOf(line("250 + 175 = 425"), good("4.25")))),
        show("Two point five plus one point seven five makes four point two five.", eq("2.50", "+", "1.75", "=", "4.25"), Mood.CHEER),
    ),
) { rnd, i ->
    val sub = band(i) == 2 && rnd.nextBoolean()
    val x = rnd.between(150, 899)
    val y = if (sub) rnd.between(50, x - 20) else rnd.between(110, 699)
    val r = if (sub) x - y else x + y
    val sign = if (sub) "−" else "+"
    val solution = listOf(
        say("Write both with two decimal places: ${fmtDec(x)} $sign ${fmtDec(y)}.", Board(listOf(line("${fmtDec(x)} $sign ${fmtDec(y)}")))),
        say("Ignore the points and ${if (sub) "subtract" else "add"}: $x $sign $y = $r.", ColumnMath(x, y, if (sub) '-' else '+')),
        show("Put the point back two places from the right: ${fmtDec(r)}.", eq(fmtDec(x), sign, fmtDec(y), "=", fmtDec(r)), Mood.CHEER),
    )
    mcNumber(
        rnd, "What is ${fmtDec(x)} $sign ${fmtDec(y)}?", null, r,
        "Line up the points, then ${if (sub) "subtract" else "add"} like whole numbers.",
        solution, spread = 10, min = 10, max = 1600, extra = listOf(r + 10, r - 10, r + 100, r - 100).filter { it > 0 },
    ) { fmtDec(it) }
}

val percentBasics = lesson(
    "percent-basics", "Percent", "Out of 100", "💯",
    teach = listOf(
        say("Percent means out of one hundred. Here is a grid of one hundred squares.", Grid100(0)),
        say("Colour twenty-five squares. That is twenty-five out of one hundred.", Grid100(25)),
        show("Twenty-five percent. We write 25%.", Stack(Grid100(25), BigText("25%")), Mood.CHEER),
        show("Half the grid is fifty percent. All of it is one hundred percent.", Board(listOf(line("50 out of 100 = 50%"), line("100 out of 100 = 100%"), good("1/2 = 50%")))),
    ),
) { rnd, _ ->
    val filled = rnd.choose(listOf(10, 20, 25, 30, 40, 50, 60, 70, 75, 80, 90))
    mcNumber(
        rnd, "What percent of the grid is coloured?", Grid100(filled), filled,
        "Count the coloured squares out of 100.",
        listOf(show("$filled squares out of 100 is $filled%.", Stack(Grid100(filled), BigText("$filled%")), Mood.CHEER)),
        spread = 10, min = 5, max = 100, extra = listOf(100 - filled, filled + 5, filled - 5, filled + 10, filled - 10).filter { it > 0 },
    ) { "$it%" }
}

val percentOf = lesson(
    "percent-of", "Percent of a number", "Find 10%, 25%, 50%", "🏷️",
    teach = listOf(
        say("To find ten percent, divide by ten. Ten percent of fifty is five.", Board(listOf(line("10% of 50"), dim("50 ÷ 10"), good("= 5")))),
        say("Fifty percent is half. Fifty percent of eighty is forty.", Board(listOf(line("50% of 80"), dim("80 ÷ 2"), good("= 40")))),
        say("Twenty-five percent is a quarter. Divide by four.", Board(listOf(line("25% of 80"), dim("80 ÷ 4"), good("= 20")))),
        show("Keep these handy!", Board(listOf(line("10% → ÷ 10"), line("20% → ÷ 5"), line("25% → ÷ 4"), line("50% → ÷ 2"))), Mood.CHEER),
    ),
) { rnd, i ->
    val (p, div) = rnd.choose(if (band(i) == 0) listOf(50 to 2, 10 to 10) else listOf(50 to 2, 10 to 10, 25 to 4, 20 to 5))
    val m = rnd.between(2, 12)
    val base = div * m
    mcNumber(
        rnd, "What is $p% of $base?", null, m,
        "$p% means divide by $div.",
        listOf(show("$p% is the same as dividing by $div: $base ÷ $div = $m.", Board(listOf(line("$p% of $base"), dim("$base ÷ $div"), good("= $m"))), Mood.CHEER)),
        spread = 3, min = 1, max = 100, extra = listOf(base - m, m * 2, m + 1, m - 1).filter { it > 0 },
    )
}

private class Equiv(val frac: String, val dec: String, val pct: String)
private val equivs = listOf(
    Equiv("1/2", "0.5", "50%"), Equiv("1/4", "0.25", "25%"), Equiv("3/4", "0.75", "75%"), Equiv("1/10", "0.1", "10%"),
    Equiv("1/5", "0.2", "20%"), Equiv("3/10", "0.3", "30%"), Equiv("2/5", "0.4", "40%"), Equiv("9/10", "0.9", "90%"),
)

val fracDecPct = lesson(
    "frac-dec-pct", "Fraction, decimal, percent", "Three names for one amount", "🎭",
    teach = listOf(
        say("A fraction, a decimal and a percent can all name the same amount. Half...", Board(listOf(line("1/2"), line("0.5"), line("50%")))),
        say("A quarter is 0.25, or twenty-five percent.", Board(listOf(line("1/4"), line("0.25"), line("25%")))),
        say("Three quarters is 0.75, or seventy-five percent.", Board(listOf(line("3/4"), line("0.75"), line("75%")))),
        show("Learn the common ones and you can switch between them quickly.", Board(listOf(line("1/2 = 0.5 = 50%"), line("1/4 = 0.25 = 25%"), line("1/10 = 0.1 = 10%"), line("1/5 = 0.2 = 20%"))), Mood.CHEER),
    ),
) { rnd, i ->
    val e = rnd.choose(if (band(i) == 0) equivs.take(4) else equivs)
    val others = equivs.filter { it !== e }.shuffled(rnd).take(3)
    val explain = show("${e.frac} = ${e.dec} = ${e.pct}.", Board(listOf(line(e.frac), line(e.dec), line(e.pct))), Mood.CHEER)
    when (i % 3) {
        0 -> mcText(rnd, "${e.frac} is the same as what percent?", null, e.pct, others.map { it.pct }, "Think of the fraction as parts of 100.", listOf(explain))
        1 -> mcText(rnd, "${e.dec} is the same as what percent?", null, e.pct, others.map { it.pct }, "Multiply the decimal by 100.", listOf(explain))
        else -> mcText(rnd, "${e.pct} is the same as which fraction?", null, e.frac, others.map { it.frac }, "Write the percent out of 100, then simplify.", listOf(explain))
    }
}

val world6 = World(
    id = "w6", title = "Fraction Bakery", tagline = "Fractions, decimals and percent", emoji = "🍰",
    color = 0xFFFF5C8A, level = "Class 3 to 6",
    lessons = listOf(
        halfQuarter, fractionParts, fractionSet, compareFractions, equivFractions, addFractions, subFractions, fractionOfNumber,
        mixedNumbers, simplify, addUnlike, multFractions, decimalTenths, decimalPlace, compareDecimals, addDecimals,
        percentBasics, percentOf, fracDecPct,
    ),
)
