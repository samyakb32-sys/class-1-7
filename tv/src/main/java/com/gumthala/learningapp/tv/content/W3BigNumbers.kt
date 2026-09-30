package com.gumthala.learningapp.tv.content

import kotlin.random.Random

/*
 * World 3 - Big Numbers Town. Two- and three-digit numbers: what the digits mean, skip counting,
 * comparing, and the written methods for adding and subtracting with carrying and borrowing.
 */

private val places = listOf("Ones", "Tens", "Hundreds", "Thousands")

private fun digit(n: Int, fromRight: Int): Int = (n / Math.pow(10.0, fromRight.toDouble()).toInt()) % 10

/** Column addition taught one column at a time, each step narrating exactly what the sum does. */
internal fun colAddSteps(a: Int, b: Int): List<Step> {
    val n = maxOf(a.toString().length, b.toString().length)
    val steps = ArrayList<Step>()
    var carry = 0
    for (i in 0 until n) {
        val da = digit(a, i)
        val db = digit(b, i)
        val sum = da + db + carry
        val text = buildString {
            append("${places[i]}: $da plus $db")
            if (carry > 0) append(" plus the 1 we carried")
            append(" makes $sum.")
            append(if (sum >= 10) " Write ${sum % 10} and carry 1." else " Write $sum.")
        }
        steps.add(say(if (i == 0) "Line up the digits. $text" else text, ColumnMath(a, b, '+'), from = i, to = i + 1))
        carry = sum / 10
    }
    if (carry > 0) steps.add(say("Bring down the 1 we carried.", ColumnMath(a, b, '+'), from = n, to = n + 1))
    steps.add(show("So $a plus $b makes ${a + b}.", eq("$a", "+", "$b", "=", "${a + b}"), Mood.CHEER))
    return steps
}

/** Column subtraction, borrowing a ten whenever the top digit is too small. */
internal fun colSubSteps(a: Int, b: Int): List<Step> {
    val n = maxOf(a.toString().length, b.toString().length)
    val steps = ArrayList<Step>()
    var borrowIn = 0
    for (i in 0 until n) {
        val da = digit(a, i)
        val db = digit(b, i)
        val base = da - borrowIn
        val text = buildString {
            append("${places[i]}: ")
            if (borrowIn > 0) append("$da gave one away, so it is now $base. ")
            if (base < db) {
                append("$base is too small to take away $db, so borrow a ten: ${base + 10} minus $db is ${base + 10 - db}.")
            } else {
                append("$base minus $db is ${base - db}.")
            }
        }
        steps.add(say(if (i == 0) "Line up the digits. $text" else text, ColumnMath(a, b, '-'), from = i, to = i + 1))
        borrowIn = if (base < db) 1 else 0
    }
    steps.add(show("So $a minus $b leaves ${a - b}.", eq("$a", "−", "$b", "=", "${a - b}"), Mood.CHEER))
    return steps
}

// ---------------------------------------------------------------------------------------------

val tensOnes = lesson(
    "tens-ones", "Tens and ones", "What the two digits mean", "🧱",
    teach = listOf(
        say("Numbers bigger than nine use two digits. Let's build thirty-four.", BigText("34", emoji = "🧱")),
        say("Ten ones can be packed into one long tens rod. Here are three tens.", Blocks(0, 3, 0)),
        say("And four single ones.", Blocks(0, 3, 4)),
        show("Three tens and four ones make thirty-four. The 3 is in the tens place, the 4 is in the ones place.", Stack(Blocks(0, 3, 4), eq("3 tens", "+", "4 ones", "=", "34"))),
        show("Fifty-two is five tens and two ones. The digit's place tells you its worth.", Stack(Blocks(0, 5, 2), BigText("52"))),
    ),
) { rnd, i ->
    val t = rnd.between(1, if (band(i) == 0) 5 else 9)
    val o = rnd.between(0, 9)
    val n = t * 10 + o
    val solution = listOf(
        say("Count the tens rods, then the single ones.", Blocks(0, t, o)),
        show("$t tens and $o ones make $n.", Stack(Blocks(0, t, o), eq("$t tens", "+", "$o ones", "=", "$n")), Mood.CHEER),
    )
    when (i % 3) {
        0 -> mcNumber(rnd, "What number do the blocks show?", Blocks(0, t, o), n, "Count the tens first, then add the ones.", solution, spread = 10, min = 10, max = 99, extra = listOf(o * 10 + t, n + 1, n - 1, n + 10).filter { it in 10..99 })
        1 -> mcNumber(rnd, "$t tens and $o ones. What number is that?", null, n, "A ten is worth 10, so $t tens is ${t * 10}.", solution, spread = 10, min = 10, max = 99, extra = listOf(o * 10 + t, t + o).filter { it != n && it in 1..99 })
        else -> mcNumber(rnd, "How many tens are in $n?", Blocks(0, t, o), t, "Look at the digit on the left: that is the tens.", solution, spread = 2, min = 0, max = 9, extra = listOf(o))
    }
}

val countBy10 = lesson(
    "count-by-10", "Count by tens", "Ten, twenty, thirty... up to 100", "🔢",
    teach = listOf(
        say("Counting by tens is quick. Each rod is ten. Let's count them!", Strip(listOf("10", "20", "30", "40", "50")), counting = true),
        say("Keep going all the way to one hundred.", Strip(listOf("60", "70", "80", "90", "100")), counting = true),
        show("Ten tens make one hundred. Notice the pattern: every number ends in zero.", Board(listOf(line("10  20  30  40  50"), line("60  70  80  90  100"), good("10 tens = 100")))),
        say("What comes after seventy?", Strip(listOf("50", "60", "70", "?"), blankAt = 3), mood = Mood.THINK),
    ),
) { rnd, i ->
    val start = rnd.between(1, if (band(i) == 0) 5 else 7) * 10
    val items = (0..3).map { start + it * 10 }
    val blank = if (band(i) == 0) 3 else rnd.between(1, 3)
    val ans = items[blank]
    mcNumber(
        rnd, "Count by tens. What number is missing?", Strip(items.mapIndexed { k, v -> if (k == blank) "?" else "$v" }, blankAt = blank), ans,
        "Add ten each time.",
        listOf(
            say("Count by tens: ${items.joinToString(", ") { Words.number(it) }}.", Strip(items.map { "$it" }), counting = true),
            show("The missing number is $ans.", Strip(items.map { "$it" }), Mood.CHEER),
        ),
        spread = 10, min = 0, max = 110, extra = listOf(ans - 1, ans + 1, ans + 20, ans - 20).filter { it in 0..110 },
    )
}

val skipCount = lesson(
    "skip-count", "Skip counting", "Count by 2s and 5s", "🦘",
    teach = listOf(
        say("Skip counting means jumping over numbers. Count by twos!", Strip(listOf("2", "4", "6", "8", "10", "12")), counting = true),
        show("Each jump adds two. Even numbers: 2, 4, 6, 8, 10.", NumberLine(0, 12, start = 0, hops = listOf(2, 4, 6, 8, 10, 12)), Mood.CHEER),
        say("Now count by fives. Your hand has five fingers!", Strip(listOf("5", "10", "15", "20", "25", "30")), counting = true),
        show("Counting by fives always ends in five or zero.", NumberLine(0, 30, start = 0, hops = listOf(5, 10, 15, 20, 25, 30), labelEvery = 5)),
        say("What comes next? Two, four, six...", Strip(listOf("2", "4", "6", "?"), blankAt = 3), mood = Mood.THINK),
    ),
) { rnd, i ->
    val step = if (band(i) == 0) rnd.choose(listOf(2, 5)) else rnd.choose(listOf(2, 5, 10, 3, 4))
    val first = step * rnd.between(0, 3)
    val len = 5
    val items = (0 until len).map { first + it * step }
    val blank = if (band(i) == 0) len - 1 else rnd.between(1, len - 1)
    val ans = items[blank]
    mcNumber(
        rnd, "Skip count by $step. What number is missing?", Strip(items.mapIndexed { k, v -> if (k == blank) "?" else "$v" }, blankAt = blank), ans,
        "Each number is $step more than the one before.",
        listOf(
            say("Count by $step: ${items.joinToString(", ") { Words.number(it) }}.", Strip(items.map { "$it" }), counting = true),
            show("The missing number is $ans.", Strip(items.map { "$it" }), Mood.CHEER),
        ),
        spread = step, min = 0, max = 100, extra = listOf(ans - 1, ans + 1, ans - step, ans + step).filter { it in 0..100 },
    )
}

val oddEven = lesson(
    "odd-even", "Odd or even", "Pairs with nothing left over", "👫",
    teach = listOf(
        say("Let's make pairs. Six dogs: two by two.", Counters("🐶", 6, CounterMode.STATIC, cols = 2)),
        show("Every dog has a partner. Six is an even number!", Counters("🐶", 6, CounterMode.STATIC, cols = 2, label = "6 is even"), Mood.CHEER),
        say("Now seven dogs. One is left alone.", Counters("🐶", 7, CounterMode.STATIC, cols = 2)),
        show("Seven is an odd number.", Counters("🐶", 7, CounterMode.STATIC, cols = 2, label = "7 is odd")),
        show("Look at the last digit. 0, 2, 4, 6, 8 means even. 1, 3, 5, 7, 9 means odd.", Board(listOf(good("even: 0 2 4 6 8"), hot("odd: 1 3 5 7 9"))), Mood.CHEER),
    ),
) { rnd, i ->
    val n = if (band(i) == 0) rnd.between(1, 10) else rnd.between(10, if (band(i) == 1) 30 else 99)
    val even = n % 2 == 0
    val t = Things.pickEasy(rnd)
    val last = n % 10
    mcText(
        rnd, "Is $n an odd or an even number?", if (n <= 12) Counters(t.emoji, n, CounterMode.STATIC, cols = 2) else null,
        if (even) "Even" else "Odd", listOf(if (even) "Odd" else "Even"),
        "Look at the last digit: $last. Even numbers end in 0, 2, 4, 6 or 8.",
        listOf(
            say("The last digit of $n is $last.", BigText("$n", sub = "last digit: $last")),
            show("$last is ${if (even) "even" else "odd"}, so $n is ${if (even) "even" else "odd"}.", Board(listOf(good("even: 0 2 4 6 8"), hot("odd: 1 3 5 7 9"))), Mood.CHEER),
        ),
    )
}

val compare2 = lesson(
    "compare-2digit", "Which is bigger?", "Compare bigger numbers", "⚖️",
    teach = listOf(
        say("To compare numbers, look at the tens first. Forty-seven and thirty-eight.", Side(listOf(Stack(BigText("47"), Blocks(0, 4, 7)), Stack(BigText("38"), Blocks(0, 3, 8))), listOf("?"))),
        say("Four tens is more than three tens. So forty-seven is bigger!", Side(listOf(Stack(BigText("47"), Blocks(0, 4, 7)), Stack(BigText("38"), Blocks(0, 3, 8))), listOf(">")), from = Step.ALL, mood = Mood.CHEER),
        say("What if the tens are the same? Then look at the ones. 53 and 58.", Board(listOf(line("53  and  58"), dim("tens: 5 and 5  (same)"), line("ones: 3 and 8"), good("58 is bigger")))),
        show("The sign opens its mouth towards the bigger number. 47 > 38.", Stack(eq("47", ">", "38"), eq("38", "<", "47")), Mood.CHEER),
    ),
) { rnd, i ->
    val a = rnd.between(10, 99)
    var b = if (band(i) == 2 && rnd.nextBoolean()) (a / 10) * 10 + rnd.between(0, 9) else rnd.between(10, 99)
    if (b == a) b = if (a < 99) a + 1 else a - 1
    val correct = if (a > b) ">" else "<"
    val explanation = if (a / 10 != b / 10) "Compare the tens: ${a / 10} and ${b / 10}." else "The tens are the same, so compare the ones: ${a % 10} and ${b % 10}."
    if (i % 2 == 0) {
        mcText(
            rnd, "Which sign goes between $a and $b?", Equation("$a", "?", "$b"), correct, listOf(if (correct == ">") "<" else ">", "="),
            "$explanation The bigger number gets the open side.",
            listOf(say(explanation, Board(listOf(line("$a  and  $b"), dim(explanation)))), show("$a ${correct} $b", eq("$a", correct, "$b"), Mood.CHEER)),
        )
    } else {
        val big = maxOf(a, b)
        // Exactly the two numbers in the question: any other choice would be a number nobody mentioned.
        mcText(
            rnd, "Which number is bigger: $a or $b?", null, "$big", listOf("${minOf(a, b)}"), explanation,
            listOf(say(explanation, Board(listOf(line("$a  and  $b"), dim(explanation)))), show("$big is bigger.", eq("$big", ">", "${minOf(a, b)}"), Mood.CHEER)),
        )
    }
}

val hundreds = lesson(
    "hundreds", "Hundreds, tens and ones", "Numbers up to 999", "💯",
    teach = listOf(
        say("Ten tens make a hundred. A hundred is a big flat square.", Blocks(1, 0, 0)),
        say("Here is three hundred and forty-two. Three flats...", Blocks(3, 0, 0)),
        say("...four tens rods...", Blocks(3, 4, 0)),
        say("...and two ones.", Blocks(3, 4, 2)),
        show("Three hundreds, four tens and two ones make 342.", Stack(Blocks(3, 4, 2), eq("300", "+", "40", "+", "2", "=", "342")), Mood.CHEER),
        show("In 342 the digit 3 means three hundreds, 4 means four tens, 2 means two ones.", PlaceChart(342)),
    ),
) { rnd, i ->
    val h = rnd.between(1, if (band(i) == 0) 3 else 5)
    val t = rnd.between(0, 9)
    val o = rnd.between(0, 9)
    val n = h * 100 + t * 10 + o
    val solution = listOf(
        say("Count the flats, the rods, then the ones.", Blocks(h, t, o)),
        show("$h hundreds, $t tens and $o ones make $n.", Stack(Blocks(h, t, o), PlaceChart(n.toLong())), Mood.CHEER),
    )
    if (i % 2 == 0) {
        mcNumber(rnd, "What number do the blocks show?", Blocks(h, t, o), n, "Hundreds first, then tens, then ones.", solution, spread = 10, min = 100, max = 999, extra = listOf(o * 100 + t * 10 + h, n + 10, n - 10, n + 100).filter { it in 100..999 })
    } else {
        mcNumber(rnd, "$h hundreds, $t tens and $o ones. What number is this?", null, n, "Write the hundreds digit first, then tens, then ones.", solution, spread = 10, min = 100, max = 999, extra = listOf(o * 100 + t * 10 + h, h + t + o).filter { it != n && it in 10..999 })
    }
}

val digitValue = lesson(
    "digit-value", "What is a digit worth?", "Place value of each digit", "🔍",
    teach = listOf(
        say("The same digit can be worth different amounts. Look at 555.", PlaceChart(555)),
        say("The first 5 is in the hundreds place. It is worth five hundred.", Board(listOf(line("5 5 5"), hot("5 hundreds = 500")))),
        say("The middle 5 is in the tens place. It is worth fifty.", Board(listOf(line("5 5 5"), dim("5 hundreds = 500"), hot("5 tens = 50")))),
        show("The last 5 is in the ones place. It is worth just five.", Board(listOf(line("5 5 5"), dim("5 hundreds = 500"), dim("5 tens = 50"), hot("5 ones = 5"))), Mood.CHEER),
    ),
) { rnd, i ->
    val digits = IntArray(3) { rnd.between(1, 9) }
    while (digits[0] == digits[1] || digits[1] == digits[2] || digits[0] == digits[2]) { digits[0] = rnd.between(1, 9); digits[1] = rnd.between(1, 9); digits[2] = rnd.between(1, 9) }
    val n = digits[0] * 100 + digits[1] * 10 + digits[2]
    val pos = if (band(i) == 0) rnd.between(1, 2) else rnd.between(0, 2)   // 0 = hundreds, 1 = tens, 2 = ones
    val d = digits[pos]
    val worth = d * listOf(100, 10, 1)[pos]
    val placeName = listOf("hundreds", "tens", "ones")[pos]
    mcNumber(
        rnd, "In $n, what is the digit $d worth?", PlaceChart(n.toLong()), worth,
        "Which place is the $d in? It is in the $placeName place.",
        listOf(
            say("The $d is in the $placeName place.", PlaceChart(n.toLong())),
            show("$d $placeName is $worth.", Board(listOf(line("$n"), hot("$d $placeName = $worth"))), Mood.CHEER),
        ),
        spread = 10, min = 1, max = 999, extra = listOf(d, d * 10, d * 100, d * 1).filter { it != worth }.distinct(),
    )
}

private fun pairNoCarry(rnd: Random, lo: Int, hi: Int): Pair<Int, Int> {
    while (true) {
        val a = rnd.between(lo, hi); val b = rnd.between(lo, hi)
        if (a % 10 + b % 10 <= 9 && (a / 10) + (b / 10) <= 9) return a to b
    }
}

val add2 = lesson(
    "add-2digit", "Add bigger numbers", "Add the ones, then the tens", "➕",
    teach = listOf(
        say("To add big numbers, write them in columns. Let's do 34 plus 25.", ColumnMath(34, 25, '+'), from = 0, to = 0),
        say("Start with the ones: four plus five makes nine.", ColumnMath(34, 25, '+'), from = 0, to = 1),
        say("Now the tens: three plus two makes five.", ColumnMath(34, 25, '+'), from = 1, to = 2),
        say("With blocks it is the same: join the tens, join the ones.", Side(listOf(Blocks(0, 3, 4), Blocks(0, 2, 5), Blocks(0, 5, 9)), listOf("+", "=")), mood = Mood.CHEER),
        show("Thirty-four plus twenty-five makes fifty-nine.", eq("34", "+", "25", "=", "59"), Mood.CHEER),
    ),
) { rnd, i ->
    val (a, b) = pairNoCarry(rnd, if (band(i) == 0) 10 else 11, if (band(i) == 2) 75 else 54)
    mcNumber(
        rnd, "What is $a + $b?", Equation("$a", "+", "$b", "=", "?"), a + b,
        "Add the ones first, then the tens.",
        colAddSteps(a, b), spread = 10, min = 10, max = 99, extra = listOf(a + b + 10, a + b - 10, a + b + 1, a + b - 1).filter { it in 10..99 },
    )
}

private fun pairCarry(rnd: Random, three: Boolean): Pair<Int, Int> {
    while (true) {
        val a = if (three) rnd.between(100, 499) else rnd.between(11, 79)
        val b = if (three) rnd.between(100, 499) else rnd.between(11, 79)
        val carries = a % 10 + b % 10 >= 10
        val ok = if (three) (a + b) < 1000 else (a + b) in 20..99
        if (carries && ok) return a to b
    }
}

val addCarry = lesson(
    "add-carry", "Add with carrying", "When ones make more than ten", "🎒",
    teach = listOf(
        say("Sometimes the ones make more than nine. Try 47 plus 38.", ColumnMath(47, 38, '+'), from = 0, to = 0),
        say("Ones: seven plus eight makes fifteen. That is one ten and five ones.", Board(listOf(line("7 + 8 = 15"), hot("15 = 1 ten + 5 ones")))),
        say("Write 5 in the ones place and carry the ten over to the tens column.", ColumnMath(47, 38, '+'), from = 0, to = 1),
        say("Tens: four plus three plus the carried one makes eight.", ColumnMath(47, 38, '+'), from = 1, to = 2),
        show("Forty-seven plus thirty-eight makes eighty-five.", eq("47", "+", "38", "=", "85"), Mood.CHEER),
    ),
) { rnd, i ->
    val three = band(i) == 2
    val (a, b) = pairCarry(rnd, three)
    mcNumber(
        rnd, "What is $a + $b?", Equation("$a", "+", "$b", "=", "?"), a + b,
        "Add the ones. If they make ten or more, carry one to the tens.",
        colAddSteps(a, b), spread = 10, min = 20, max = 999, extra = listOf(a + b - 10, a + b + 10, a + b - 100, a + b + 1).filter { it in 20..999 },
    )
}

private fun pairNoBorrow(rnd: Random, lo: Int, hi: Int): Pair<Int, Int> {
    while (true) {
        val a = rnd.between(lo, hi); val b = rnd.between(10, a)
        if (a % 10 >= b % 10 && a / 10 >= b / 10 && a != b) return a to b
    }
}

val sub2 = lesson(
    "sub-2digit", "Take away bigger numbers", "Subtract the ones, then the tens", "➖",
    teach = listOf(
        say("Write the bigger number on top. Let's do 58 minus 23.", ColumnMath(58, 23, '-'), from = 0, to = 0),
        say("Ones: eight minus three is five.", ColumnMath(58, 23, '-'), from = 0, to = 1),
        say("Tens: five minus two is three.", ColumnMath(58, 23, '-'), from = 1, to = 2),
        say("With blocks: fifty-eight is five tens and eight ones. Take away two tens and three ones.", Side(listOf(Blocks(0, 5, 8), Blocks(0, 2, 3), Blocks(0, 3, 5)), listOf("−", "="))),
        show("Fifty-eight minus twenty-three leaves thirty-five.", eq("58", "−", "23", "=", "35"), Mood.CHEER),
    ),
) { rnd, i ->
    val (a, b) = pairNoBorrow(rnd, if (band(i) == 0) 20 else 30, if (band(i) == 2) 99 else 79)
    mcNumber(
        rnd, "What is $a − $b?", Equation("$a", "−", "$b", "=", "?"), a - b,
        "Take away the ones first, then the tens.",
        colSubSteps(a, b), spread = 10, min = 0, max = 99, extra = listOf(a - b + 10, a - b - 10, a + b, a - b + 1).filter { it in 0..99 },
    )
}

private fun pairBorrow(rnd: Random, three: Boolean): Pair<Int, Int> {
    while (true) {
        val a = if (three) rnd.between(200, 899) else rnd.between(21, 99)
        val b = if (three) rnd.between(101, a - 1) else rnd.between(11, a - 1)
        val ok = a % 10 < b % 10 && (!three || (digit(a, 1) >= 1 && a / 100 >= b / 100 && (digit(a, 1) - 1 >= digit(b, 1))))
        if (ok && (three || a / 10 > b / 10)) return a to b
    }
}

val subBorrow = lesson(
    "sub-borrow", "Borrowing", "When the top digit is too small", "🏦",
    teach = listOf(
        say("What if the top digit is too small? Try 52 minus 27.", ColumnMath(52, 27, '-'), from = 0, to = 0),
        say("Two is too small to take away seven. So we borrow a ten from the five.", Board(listOf(line("52  =  5 tens  +  2 ones"), hot("borrow one ten"), line("4 tens  +  12 ones")))),
        say("Twelve minus seven is five. Then the tens: four minus two is two.", ColumnMath(52, 27, '-'), from = 0),
        show("Fifty-two minus twenty-seven leaves twenty-five.", eq("52", "−", "27", "=", "25"), Mood.CHEER),
        show("Borrowing is just swapping one ten for ten ones.", Board(listOf(line("1 ten  =  10 ones"), good("so 52 is 4 tens and 12 ones")))),
    ),
) { rnd, i ->
    val three = band(i) == 2
    val (a, b) = pairBorrow(rnd, three)
    mcNumber(
        rnd, "What is $a − $b?", Equation("$a", "−", "$b", "=", "?"), a - b,
        "The top ones digit is too small. Borrow a ten.",
        colSubSteps(a, b), spread = 10, min = 0, max = 999, extra = listOf(a - b + 10, a - b - 10, (a % 10 - b % 10).let { kotlin.math.abs(it) } + ((a / 10 - b / 10) * 10)).filter { it in 0..999 },
    )
}

val roundTens = lesson(
    "round-tens", "Rounding to tens", "Which ten is closer?", "🎯",
    teach = listOf(
        say("Rounding means finding the nearest ten. Where is 43?", NumberLine(40, 50, mark = 43)),
        say("Forty-three is closer to forty than to fifty. So it rounds down to forty.", NumberLine(40, 50, start = 43, hops = listOf(40))),
        say("What about 48? It is very near fifty.", NumberLine(40, 50, start = 48, hops = listOf(50))),
        show("Five or more? Round up. Four or less? Round down. 45 rounds up to 50.", Board(listOf(good("0 1 2 3 4  →  round down"), hot("5 6 7 8 9  →  round up"))), Mood.CHEER),
    ),
) { rnd, i ->
    val tens = rnd.between(1, 8) * 10
    val o = rnd.between(1, 9)
    val n = tens + o
    val ans = if (o >= 5) tens + 10 else tens
    mcNumber(
        rnd, "Round $n to the nearest ten.", NumberLine(tens, tens + 10, mark = n), ans,
        "Look at the ones digit, $o. Five or more rounds up.",
        listOf(
            say("Is $n closer to $tens or ${tens + 10}?", NumberLine(tens, tens + 10, mark = n)),
            show("The ones digit is $o, so it rounds ${if (o >= 5) "up" else "down"} to $ans.", NumberLine(tens, tens + 10, start = n, hops = listOf(ans)), Mood.CHEER),
        ),
        spread = 10, min = 10, max = 100, extra = listOf(tens, tens + 10).filter { it != ans } + listOf(n),
    )
}

val world3 = World(
    id = "w3", title = "Big Numbers Town", tagline = "Tens, hundreds and written sums", emoji = "🏘️",
    color = 0xFF3B82F6, level = "Class 2",
    lessons = listOf(tensOnes, countBy10, skipCount, oddEven, compare2, hundreds, digitValue, add2, addCarry, sub2, subBorrow, roundTens),
)
