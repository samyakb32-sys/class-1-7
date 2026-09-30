package com.gumthala.learningapp.tv.content

import kotlin.random.Random

/*
 * World 4 - Times-Table Mountain. Multiplication as equal groups, arrays and skip counting; every table
 * from 2 to 10; division as sharing and grouping; and the written methods (column multiplication,
 * long division) with every step narrated.
 */

private fun groupsOf(t: Thing, groups: Int, each: Int, mode: CounterMode = CounterMode.STATIC, label: Boolean = false): Visual =
    Side(List(groups) { Counters(t.emoji, each, mode, cols = if (each > 6) 4 else 0, label = if (label) "$each" else null) })

/** n x k by skip counting: n, 2n, 3n ... each number spoken as it appears. */
internal fun skipCountSolution(n: Int, k: Int): List<Step> = listOf(
    say("Count in ${n}s, $k times.", Strip((1..k).map { "${n * it}" }), counting = true),
    show("${Words.number(n)} times ${Words.number(k)} makes ${n * k}.", eq("$n", "×", "$k", "=", "${n * k}"), Mood.CHEER),
)

internal fun colMulSteps(a: Int, b: Int): List<Step> {
    val n = a.toString().length
    val steps = ArrayList<Step>()
    var carry = 0
    for (i in 0 until n) {
        val da = (a / Math.pow(10.0, i.toDouble()).toInt()) % 10
        val prod = da * b + carry
        val place = listOf("Ones", "Tens", "Hundreds", "Thousands")[i]
        val text = buildString {
            append("$place: $da times $b")
            if (carry > 0) append(", plus the $carry we carried,")
            append(" makes $prod.")
            append(if (prod >= 10) " Write ${prod % 10} and carry ${prod / 10}." else " Write $prod.")
        }
        steps.add(say(if (i == 0) "Line up the digits. $text" else text, ColumnMath(a, b, 'x'), from = i, to = i + 1))
        carry = prod / 10
    }
    if (carry > 0) steps.add(say("Bring down the $carry we carried.", ColumnMath(a, b, 'x'), from = n, to = n + 1))
    steps.add(show("So $a times $b makes ${a * b}.", eq("$a", "×", "$b", "=", "${a * b}"), Mood.CHEER))
    return steps
}

/** Long division, one digit of the dividend at a time, building the working on a board. */
internal fun longDivSteps(a: Int, d: Int): List<Step> {
    val digits = a.toString().map { it - '0' }
    val steps = ArrayList<Step>()
    val lines = ArrayList<BoardLine>()
    lines.add(line("$a ÷ $d"))
    var rem = 0
    val quotient = StringBuilder()
    for ((idx, dg) in digits.withIndex()) {
        val cur = rem * 10 + dg
        val q = cur / d
        val newRem = cur % d
        val shown = lines.size
        val text: String
        if (q == 0) {
            text = "How many ${d}s in $cur? None yet. Bring down the next digit."
            lines.add(dim("$cur ÷ $d = 0"))
        } else {
            text = buildString {
                if (idx > 0) append("Bring down the $dg to make $cur. ")
                append("How many ${d}s in $cur? $q, because $q times $d is ${q * d}.")
                if (newRem > 0) append(" $cur minus ${q * d} leaves $newRem.")
            }
            lines.add(line("$cur ÷ $d = $q" + if (newRem > 0) "  (left $newRem)" else ""))
        }
        if (q > 0 || quotient.isNotEmpty()) quotient.append(q)
        rem = newRem
        steps.add(say(text, Board(lines.toList()), from = shown, to = shown + 1))
    }
    lines.add(good("$a ÷ $d = $quotient"))
    steps.add(show("So $a divided by $d is $quotient.", Board(lines.toList()), Mood.CHEER))
    return steps
}

// ---------------------------------------------------------------------------------------------

val equalGroups = lesson(
    "equal-groups", "Equal groups", "Same number in every group", "🧺",
    teach = listOf(
        say("Look! Three baskets, and every basket has two apples.", groupsOf(Things.apple, 3, 2)),
        say("Count all the apples. Two, four, six.", Strip(listOf("2", "4", "6")), counting = true),
        show("Three groups of two make six. That is called multiplying.", Stack(groupsOf(Things.apple, 3, 2), eq("3 groups of 2", "=", "6"))),
        say("Here are four groups of three stars.", groupsOf(Things.star, 4, 3)),
        say("Count by threes: three, six, nine, twelve.", Strip(listOf("3", "6", "9", "12")), counting = true),
        show("Four groups of three make twelve.", eq("4 groups of 3", "=", "12"), Mood.CHEER),
    ),
) { rnd, i ->
    val t = Things.pickEasy(rnd)
    val groups = rnd.between(2, if (band(i) == 0) 3 else 5)
    val each = rnd.between(2, if (band(i) == 0) 4 else 6)
    val total = groups * each
    mcNumber(
        rnd, "$groups bags, with $each ${t.many} in each bag. How many ${t.many} altogether?", groupsOf(t, groups, each), total,
        "Count by ${each}s, or count everything.",
        listOf(
            say("Count by ${each}s, once for each bag.", Strip((1..groups).map { "${each * it}" }), counting = true),
            show("$groups groups of $each make $total.", eq("$groups", "×", "$each", "=", "$total"), Mood.CHEER),
        ),
        spread = 3, min = 2, max = 40, extra = listOf(groups + each, total + each, total - each),
    )
}

val repeatAdd = lesson(
    "repeat-add", "Adding again and again", "Repeated adding is multiplying", "🔁",
    teach = listOf(
        say("Adding the same number again and again is slow. There is a shortcut!", Equation("4", "+", "4", "+", "4", "=", "12")),
        say("Four plus four plus four. That is three fours.", Board(listOf(line("4 + 4 + 4 = 12"), hot("3 fours")))),
        show("We write three fours as three times four.", Board(listOf(line("4 + 4 + 4 = 12"), line("3 × 4 = 12"))), Mood.CHEER),
        show("The times sign means groups of. 5 × 3 means five groups of three.", Stack(groupsOf(Things.ball, 5, 3), eq("5", "×", "3", "=", "15"))),
    ),
) { rnd, i ->
    val n = rnd.between(2, if (band(i) == 0) 5 else 9)
    val k = rnd.between(2, if (band(i) == 0) 4 else 6)
    val addition = (1..k).joinToString(" + ") { "$n" }
    if (i % 2 == 0) {
        mcNumber(
            rnd, "What is $addition?", null, n * k, "Count by ${n}s, $k times.",
            skipCountSolution(n, k), spread = 3, min = 2, max = 60, extra = listOf(n * k + n, n * k - n, n + k),
        )
    } else {
        mcText(
            rnd, "Which multiplication is the same as $addition?", null, "$k × $n", listOf("$n × $n", "$k + $n", "$n × ${k + 1}"),
            "There are $k numbers, and each one is $n. So: $k groups of $n.",
            listOf(
                say("There are $k numbers being added, and each is $n.", Equation(*((1..k).flatMap { listOf("$n", "+") }.dropLast(1)).toTypedArray())),
                show("$k groups of $n is written $k × $n.", eq("$k", "×", "$n", "=", "${k * n}"), Mood.CHEER),
            ),
        )
    }
}

val arrays = lesson(
    "arrays", "Rows and columns", "Multiplying with a grid", "🔲",
    teach = listOf(
        say("Things in neat rows and columns make an array. Here are three rows of four.", ArrayGrid(3, 4, "🍪"), counting = true),
        show("Three rows of four. Four, eight, twelve.", Stack(ArrayGrid(3, 4, "🍪"), eq("3", "×", "4", "=", "12")), Mood.CHEER),
        say("Turn it around: four rows of three.", ArrayGrid(4, 3, "🍪"), counting = true),
        show("Four times three is twelve too! The answer stays the same.", Stack(ArrayGrid(4, 3, "🍪"), eq("4", "×", "3", "=", "12")), Mood.CHEER),
    ),
) { rnd, i ->
    val t = Things.pickEasy(rnd)
    val r = rnd.between(2, if (band(i) == 0) 3 else 5)
    val c = rnd.between(2, if (band(i) == 0) 5 else 8)
    mcNumber(
        rnd, "$r rows, with $c ${t.many} in each row. How many altogether?", ArrayGrid(r, c, t.emoji), r * c,
        "Count by ${c}s, one row at a time.",
        listOf(
            say("Count one row at a time, by ${c}s.", ArrayGrid(r, c, t.emoji), counting = true),
            show("$r rows of $c make ${r * c}.", eq("$r", "×", "$c", "=", "${r * c}"), Mood.CHEER),
        ),
        spread = 3, min = 2, max = 45, extra = listOf(r + c, r * c + c, r * c - c),
    )
}

private val tableTricks = mapOf(
    2 to listOf("Double the number!", "All the answers are even."),
    3 to listOf("Count up by three each time.", "Answers: 3, 6, 9, 12, 15 ..."),
    4 to listOf("Double it, then double it again.", "4 × 3: double 3 is 6, double 6 is 12."),
    5 to listOf("Answers end in 5 or 0.", "Count the fingers on your hand."),
    6 to listOf("6 × 7 is 5 × 7 plus one more 7.", "35 + 7 = 42."),
    7 to listOf("Watch out for the tricky ones.", "7 × 8 = 56: think 5, 6, 7, 8!"),
    8 to listOf("Double, double, double.", "8 × 3: 3 → 6 → 12 → 24."),
    9 to listOf("The digits of the answer add up to 9.", "9 × 4 = 36, and 3 + 6 = 9."),
    10 to listOf("Just write a zero after the number.", "10 × 7 = 70."),
)

private val keycaps = mapOf(2 to "2️⃣", 3 to "3️⃣", 4 to "4️⃣", 5 to "5️⃣", 6 to "6️⃣", 7 to "7️⃣", 8 to "8️⃣", 9 to "9️⃣", 10 to "🔟")

internal fun tableQuestion(rnd: Random, n: Int, i: Int): Question {
    val k = when (band(i)) { 0 -> rnd.between(1, 5); 1 -> rnd.between(1, 10); else -> rnd.between(3, 10) }
    val ans = n * k
    val form = if (band(i) < 2) 0 else rnd.nextInt(3)
    val solution = skipCountSolution(n, k)
    return when (form) {
        0 -> mcNumber(rnd, "What is $n × $k?", Equation("$n", "×", "$k", "=", "?"), ans, "Count in ${n}s, $k times.", solution, spread = n, min = 0, max = 100, extra = listOf(ans - n, ans + n, ans + 1, ans - 1).filter { it > 0 })
        1 -> mcNumber(rnd, "What is $k × $n?", Equation("$k", "×", "$n", "=", "?"), ans, "The order doesn't matter: $k × $n is the same as $n × $k.", solution, spread = n, min = 0, max = 100, extra = listOf(ans - n, ans + n, ans + 1, ans - 1).filter { it > 0 })
        else -> mcNumber(rnd, "$n × ? = $ans", Equation("$n", "×", "?", "=", "$ans"), k, "How many ${n}s make $ans? Count in ${n}s.", solution, spread = 2, min = 1, max = 12, extra = listOf(k - 1, k + 1))
    }
}

private fun tableLesson(n: Int): Lesson = lesson(
    "table-$n", "$n times table", "Count in ${n}s", keycaps.getValue(n),
    teach = buildList {
        add(say("Let's learn the $n times table. First, count in ${n}s.", Strip((1..10).map { "${n * it}" }), counting = true))
        add(say("Here is three rows of $n. Count by ${n}s.", ArrayGrid(3, n.coerceAtMost(10), Things.easy[n % Things.easy.size].emoji), counting = true))
        add(say("The first five.", Board((1..5).map { line("$n × $it = ${n * it}") })))
        add(say("And the next five.", Board((6..10).map { line("$n × $it = ${n * it}") })))
        val (a, b) = tableTricks.getValue(n)
        add(say("Here is a trick. $a", Board(listOf(hot(a), line(b))), mood = Mood.THINK))
        add(show("Practise a little every day and you will know it by heart!", Board(listOf(good("$n × 1 = $n"), good("$n × 5 = ${n * 5}"), good("$n × 10 = ${n * 10}"))), Mood.CHEER))
    },
) { rnd, i -> tableQuestion(rnd, n, i) }

val table2 = tableLesson(2)
val table3 = tableLesson(3)
val table4 = tableLesson(4)
val table5 = tableLesson(5)
val table6 = tableLesson(6)
val table7 = tableLesson(7)
val table8 = tableLesson(8)
val table9 = tableLesson(9)
val table10 = tableLesson(10)

val tableMix = lesson(
    "table-mix", "Times table challenge", "All the tables mixed up", "🏔️",
    teach = listOf(
        say("Time for a challenge! All the tables are mixed together.", BigText("2  3  4  5  6  7  8  9  10", emoji = "🏔️"), mood = Mood.CHEER),
        say("If you forget an answer, count in that number. Six times seven? Count in sixes.", Strip(listOf("6", "12", "18", "24", "30", "36", "42")), counting = true),
        show("Seven sixes make forty-two. You can always work it out!", eq("6", "×", "7", "=", "42"), Mood.CHEER),
    ),
) { rnd, i -> tableQuestion(rnd, rnd.between(2, 10), if (band(i) == 0) 1 else i) }

val multSpecial = lesson(
    "mult-special", "Times 0, times 1, any order", "Tricks that save time", "✨",
    teach = listOf(
        say("Anything times one stays the same. One group of seven is seven.", Stack(groupsOf(Things.star, 1, 7), eq("1", "×", "7", "=", "7"))),
        say("Anything times zero is zero. Zero groups means nothing at all.", Stack(Counters("🌟", 0, CounterMode.STATIC), eq("0", "×", "7", "=", "0"))),
        show("The order does not matter. Two rows of five, or five rows of two: ten either way.", Side(listOf(ArrayGrid(2, 5, "🍪"), ArrayGrid(5, 2, "🍪")), listOf("="))),
        show("So 3 × 8 = 8 × 3 = 24. That halves what you need to learn!", Board(listOf(line("3 × 8 = 24"), line("8 × 3 = 24"), good("Same answer!"))), Mood.CHEER),
    ),
) { rnd, i ->
    when (i % 3) {
        0 -> { val n = rnd.between(2, 9); mcNumber(rnd, "What is $n × 1?", Equation("$n", "×", "1", "=", "?"), n, "Anything times one stays the same.", listOf(show("One group of $n is $n.", eq("$n", "×", "1", "=", "$n"), Mood.CHEER)), spread = 2, min = 0, max = 12, extra = listOf(n + 1, n - 1, 1)) }
        1 -> { val n = rnd.between(2, 12); mcNumber(rnd, "What is $n × 0?", Equation("$n", "×", "0", "=", "?"), 0, "Zero groups means nothing.", listOf(show("Zero groups of $n is zero.", eq("$n", "×", "0", "=", "0"), Mood.CHEER)), spread = 3, min = 0, max = 12, extra = listOf(n, 1)) }
        else -> { val a = rnd.between(2, 9); val b = rnd.between(2, 9).let { if (it == a) (it % 9) + 2 else it }
            mcNumber(rnd, "$a × $b = $b × ?", Equation("$a", "×", "$b", "=", "$b", "×", "?"), a, "The order does not matter.", listOf(show("$a × $b and $b × $a are the same.", Board(listOf(line("$a × $b = ${a * b}"), line("$b × $a = ${a * b}"))), Mood.CHEER)), spread = 2, min = 1, max = 12, extra = listOf(b, a + 1, a - 1)) }
    }
}

val shareEqual = lesson(
    "share-equal", "Sharing equally", "Fair shares make division", "🍬",
    teach = listOf(
        say("We have twelve sweets to share fairly between three friends.", Counters("🍬", 12, CounterMode.STATIC, cols = 6)),
        say("Give each friend one at a time, until the sweets are all gone.", Side(listOf(Counters("🍬", 4, CounterMode.COUNT, label = "Asha"), Counters("🍬", 4, CounterMode.COUNT, label = "Ravi"), Counters("🍬", 4, CounterMode.COUNT, label = "Mira")))),
        show("Each friend gets four. Twelve shared by three is four.", Stack(Side(listOf(Counters("🍬", 4, CounterMode.STATIC, label = "Asha"), Counters("🍬", 4, CounterMode.STATIC, label = "Ravi"), Counters("🍬", 4, CounterMode.STATIC, label = "Mira"))), eq("12", "÷", "3", "=", "4")), Mood.CHEER),
        show("The divide sign means share equally. 12 ÷ 3 = 4.", eq("12", "÷", "3", "=", "4")),
    ),
) { rnd, i ->
    val t = Things.pickEasy(rnd)
    val groups = rnd.between(2, if (band(i) == 0) 3 else 5)
    val each = rnd.between(2, if (band(i) == 0) 4 else 6)
    val total = groups * each
    val friends = kidNames.shuffled(rnd).take(groups)
    mcNumber(
        rnd, "${t.count(total)} are shared equally between $groups friends. How many does each friend get?",
        Counters(t.emoji, total, CounterMode.STATIC, cols = 6), each,
        "Give one to each friend, again and again, until all are given out.",
        listOf(
            say("Give each friend the same number, until none are left.", Side(friends.map { Counters(t.emoji, each, CounterMode.COUNT, cols = if (each > 5) 3 else 0, label = it) })),
            show("$total shared between $groups gives $each each.", eq("$total", "÷", "$groups", "=", "$each"), Mood.CHEER),
        ),
        spread = 2, min = 1, max = 12, extra = listOf(groups, total - groups, each + 1, each - 1),
    )
}

val groupDiv = lesson(
    "group-div", "Making equal groups", "How many groups can we make?", "📦",
    teach = listOf(
        say("Division can also mean making groups. We have twelve sweets.", Counters("🍬", 12, CounterMode.STATIC, cols = 6)),
        say("Put four sweets in each bag. How many bags can we fill?", Side(listOf(Counters("🍬", 4, CounterMode.STATIC), Counters("🍬", 4, CounterMode.STATIC), Counters("🍬", 4, CounterMode.STATIC)))),
        show("We filled three bags. Twelve divided into groups of four is three.", eq("12", "÷", "4", "=", "3"), Mood.CHEER),
        show("Sharing and grouping both use the same sum: 12 ÷ 3 = 4 and 12 ÷ 4 = 3.", Board(listOf(line("12 ÷ 3 = 4"), line("12 ÷ 4 = 3")))),
    ),
) { rnd, i ->
    val t = Things.pickEasy(rnd)
    val each = rnd.between(2, if (band(i) == 0) 4 else 6)
    val bags = rnd.between(2, if (band(i) == 0) 4 else 5)   // at most 5 x 6 = 30 things in the picture
    val total = each * bags
    mcNumber(
        rnd, "${t.count(total)}. Put $each in each bag. How many bags can be filled?", Counters(t.emoji, total, CounterMode.STATIC, cols = 6), bags,
        "Make groups of $each until there are none left. Count the groups.",
        listOf(
            say("Make groups of $each and count the bags.", Side(List(bags) { Counters(t.emoji, each, CounterMode.STATIC, cols = if (each > 5) 3 else 0) })),
            show("$total divided into groups of $each makes $bags groups.", eq("$total", "÷", "$each", "=", "$bags"), Mood.CHEER),
        ),
        spread = 2, min = 1, max = 12, extra = listOf(each, total - each, bags + 1, bags - 1),
    )
}

val multDivFamily = lesson(
    "mult-div-family", "Times and divide are friends", "One fact, four answers", "🤝",
    teach = listOf(
        say("Multiplying and dividing are friends. Look at 3, 4 and 12.", Strip(listOf("3", "4", "12"), arrows = false)),
        say("Two times facts.", Board(listOf(line("3 × 4 = 12"), line("4 × 3 = 12")))),
        say("And two divide facts. They undo each other.", Board(listOf(line("3 × 4 = 12"), line("4 × 3 = 12"), hot("12 ÷ 3 = 4"), hot("12 ÷ 4 = 3")))),
        show("Know the times table, and you know the division too!", Board(listOf(line("3 × 4 = 12"), line("4 × 3 = 12"), line("12 ÷ 3 = 4"), line("12 ÷ 4 = 3"))), Mood.CHEER),
    ),
) { rnd, i ->
    val a = rnd.between(2, if (band(i) == 0) 5 else 9)
    val b = rnd.between(2, if (band(i) == 0) 5 else 9)
    val c = a * b
    val fam = Board(listOf(line("$a × $b = $c"), line("$b × $a = $c"), line("$c ÷ $a = $b"), line("$c ÷ $b = $a")))
    val solution = listOf(
        say("$a, $b and $c make a fact family.", Strip(listOf("$a", "$b", "$c"), arrows = false)),
        show("Use the times fact to find the divide answer.", fam, Mood.CHEER),
    )
    if (i % 2 == 0) mcNumber(rnd, "$a × $b = $c. So $c ÷ $a = ?", Equation("$c", "÷", "$a", "=", "?"), b, "Which number times $a makes $c? That is $b.", solution, spread = 2, min = 1, max = 12, extra = listOf(c, a))
    else mcNumber(rnd, "$c ÷ $b = $a. So $a × $b = ?", Equation("$a", "×", "$b", "=", "?"), c, "Divide and multiply undo each other.", solution, spread = b, min = 1, max = 100, extra = listOf(c + a, c - a, a + b))
}

val divFacts = lesson(
    "div-facts", "Dividing with tables", "Use the times tables backwards", "➗",
    teach = listOf(
        say("To divide, think of the times table. What is thirty-five divided by five?", Equation("35", "÷", "5", "=", "?")),
        say("Count in fives until you reach thirty-five. Count the jumps.", Strip(listOf("5", "10", "15", "20", "25", "30", "35")), counting = true),
        show("Seven jumps of five make thirty-five. So 35 ÷ 5 = 7.", eq("35", "÷", "5", "=", "7"), Mood.CHEER),
        show("Or ask: five times what makes thirty-five? 5 × 7 = 35, so 35 ÷ 5 = 7.", Board(listOf(line("5 × ? = 35"), line("5 × 7 = 35"), good("35 ÷ 5 = 7")))),
    ),
) { rnd, i ->
    val d = rnd.between(2, if (band(i) == 0) 5 else 10)
    val q = rnd.between(2, if (band(i) == 0) 6 else 10)
    val a = d * q
    mcNumber(
        rnd, "What is $a ÷ $d?", Equation("$a", "÷", "$d", "=", "?"), q,
        "Think: $d times what makes $a?",
        listOf(
            say("Count in ${d}s until you reach $a.", Strip((1..q).map { "${d * it}" }), counting = true),
            show("$q jumps of $d make $a. So $a ÷ $d = $q.", eq("$a", "÷", "$d", "=", "$q"), Mood.CHEER),
        ),
        spread = 2, min = 1, max = 12, extra = listOf(q - 1, q + 1, d, a - d).filter { it in 1..12 },
    )
}

val mult2digit = lesson(
    "mult-2digit", "Multiply bigger numbers", "Times by one digit, column by column", "🧮",
    teach = listOf(
        say("Let's multiply a big number by one digit. Try 23 times 4.", ColumnMath(23, 4, 'x'), from = 0, to = 0),
        say("Ones: three times four makes twelve. Write 2 and carry 1.", ColumnMath(23, 4, 'x'), from = 0, to = 1),
        say("Tens: two times four is eight, plus the carried one makes nine.", ColumnMath(23, 4, 'x'), from = 1, to = 2),
        show("Twenty-three times four makes ninety-two.", eq("23", "×", "4", "=", "92"), Mood.CHEER),
        say("Another way: split 23 into 20 and 3. Times four each.", Board(listOf(line("23 × 4"), dim("20 × 4 = 80"), dim("3 × 4 = 12"), good("80 + 12 = 92")))),
    ),
) { rnd, i ->
    val a = rnd.between(if (band(i) == 0) 11 else 12, if (band(i) == 2) 99 else 49)
    val b = rnd.between(2, if (band(i) == 0) 5 else 9)
    mcNumber(
        rnd, "What is $a × $b?", Equation("$a", "×", "$b", "=", "?"), a * b,
        "Multiply the ones first, then the tens. Don't forget to add what you carried.",
        colMulSteps(a, b), spread = 10, min = 10, max = 900, extra = listOf(a * b + 10, a * b - 10, a * b + b, (a % 10) * b + (a / 10) * b).filter { it > 0 },
    )
}

val times10 = lesson(
    "times-10-100", "Times 10 and times 100", "Slide the digits along", "🚀",
    teach = listOf(
        say("Multiplying by ten is easy. Watch thirty-four times ten.", Side(listOf(Blocks(0, 3, 4), Blocks(3, 4, 0)), listOf("× 10  →"))),
        say("Every ten becomes a hundred and every one becomes a ten. The digits slide one place left.", Board(listOf(line("34 × 10"), dim("3 tens, 4 ones"), line("3 hundreds, 4 tens"), good("= 340")))),
        show("A quick way: just add a zero on the end. 34 × 10 = 340.", eq("34", "×", "10", "=", "340"), Mood.CHEER),
        show("Times one hundred? Add two zeros. 34 × 100 = 3400.", eq("34", "×", "100", "=", "3400")),
    ),
) { rnd, i ->
    val hundred = band(i) == 2 && rnd.nextBoolean()
    val a = rnd.between(2, if (hundred) 30 else 99)
    val m = if (hundred) 100 else 10
    val ans = a * m
    mcNumber(
        rnd, "What is $a × $m?", Equation("$a", "×", "$m", "=", "?"), ans,
        "Add ${if (hundred) "two zeros" else "a zero"} to the end of $a.",
        listOf(
            say("Slide the digits ${if (hundred) "two places" else "one place"} to the left.", PlaceChart(a.toLong())),
            show("So $a times $m is $ans.", eq("$a", "×", "$m", "=", "$ans"), Mood.CHEER),
        ),
        spread = 10, min = 10, max = 4000, extra = listOf(ans / 10, ans * 10, ans + m, a + m).filter { it > 0 && it != ans },
    )
}

val divRemainder = lesson(
    "div-remainder", "Leftovers", "When it doesn't share out evenly", "🍪",
    teach = listOf(
        say("What if things don't share out evenly? Thirteen cookies, four to a box.", Counters("🍪", 13, CounterMode.STATIC, cols = 7)),
        say("We fill three boxes with four each. That uses twelve cookies.", Counters("🍪", 13, CounterMode.STATIC, cols = 7, split = 12)),
        show("One cookie is left over. We call it the remainder.", Counters("🍪", 13, CounterMode.STATIC, cols = 7, split = 12, label = "3 boxes, 1 left over"), Mood.THINK),
        show("Thirteen divided by four is three, remainder one.", eq("13", "÷", "4", "=", "3 r 1"), Mood.CHEER),
        show("The remainder is always smaller than what you divide by.", Board(listOf(line("13 ÷ 4 = 3 r 1"), good("1 is smaller than 4")))),
    ),
) { rnd, i ->
    val d = rnd.between(2, if (band(i) == 0) 4 else 6)
    val q = rnd.between(2, if (band(i) == 0) 3 else 4)   // keeps the picture to at most 29 cookies
    val r = rnd.between(1, d - 1)
    val a = d * q + r
    val correct = "$q r $r"
    val wrongs = LinkedHashSet<String>()
    wrongs.add("$r r $q")
    wrongs.add("${q + 1} r $r")
    wrongs.add("$q r ${if (r + 1 < d) r + 1 else r - 1}")
    wrongs.add("${q - 1} r ${r}")
    wrongs.remove(correct)
    mcText(
        rnd, "What is $a ÷ $d?", Counters("🍪", a, CounterMode.STATIC, cols = 7),
        correct, wrongs.toList().take(3),
        "Make groups of $d. How many full groups? How many are left?",
        listOf(
            say("Make groups of $d. We can fill $q groups, using ${d * q}.", Counters("🍪", a, CounterMode.STATIC, cols = 7, split = d * q)),
            show("$r ${if (r == 1) "is" else "are"} left over. So $a ÷ $d = $q remainder $r.", eq("$a", "÷", "$d", "=", "$q r $r"), Mood.CHEER),
        ),
    )
}

val longDivision = lesson(
    "long-division", "Long division", "Divide big numbers step by step", "🪜",
    teach = listOf(
        say("Long division shares a big number one digit at a time. Try 78 divided by 6.", Board(listOf(line("78 ÷ 6")))),
    ) + longDivSteps(78, 6),
) { rnd, i ->
    val d = rnd.between(2, if (band(i) == 0) 5 else 9)
    val q = rnd.between(if (band(i) == 2) 20 else 12, if (band(i) == 2) 99 else 40)
    val a = d * q
    mcNumber(
        rnd, "What is $a ÷ $d?", Equation("$a", "÷", "$d", "=", "?"), q,
        "Divide one digit at a time, from the left. Bring down the next digit each time.",
        longDivSteps(a, d), spread = 10, min = 2, max = 120, extra = listOf(q + 1, q - 1, q + 10, q - 10, (q % 10) * 10 + q / 10).filter { it != q },
    )
}

val world4 = World(
    id = "w4", title = "Times-Table Mountain", tagline = "Multiply and divide with confidence", emoji = "⛰️",
    color = 0xFF8B5CF6, level = "Class 2 to 4",
    lessons = listOf(
        equalGroups, repeatAdd, arrays, table2, table5, table10, table3, table4, table6, table9, table7, table8, tableMix,
        multSpecial, shareEqual, groupDiv, multDivFamily, divFacts, mult2digit, times10, divRemainder, longDivision,
    ),
)
