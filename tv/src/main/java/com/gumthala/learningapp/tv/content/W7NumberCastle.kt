package com.gumthala.learningapp.tv.content

import kotlin.random.Random

/*
 * World 7 - Number Castle. Big numbers in the Indian system, number families (factors, multiples, primes,
 * HCF, LCM), the order of operations, negative numbers, squares, powers and Roman numerals.
 */

private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
private fun lcm(a: Int, b: Int): Int = a / gcd(a, b) * b
private fun factorsOf(n: Int): List<Int> = (1..n).filter { n % it == 0 }
private fun isPrime(n: Int): Boolean = n >= 2 && (2..Math.sqrt(n.toDouble()).toInt()).none { n % it == 0 }

/** Negative numbers print with a real minus sign so they read clearly on a big screen. */
private fun num(n: Int): String = if (n < 0) "−${-n}" else "$n"

/** A number as a token inside a sum: negatives get brackets, like 5 + (−3). */
private fun tok(n: Int): String = if (n < 0) "(−${-n})" else "$n"

internal fun toRoman(n: Int): String {
    val values = intArrayOf(100, 90, 50, 40, 10, 9, 5, 4, 1)
    val symbols = arrayOf("C", "XC", "L", "XL", "X", "IX", "V", "IV", "I")
    var left = n
    val sb = StringBuilder()
    for (i in values.indices) while (left >= values[i]) { sb.append(symbols[i]); left -= values[i] }
    return sb.toString()
}

private fun fromRoman(s: String): Int {
    val v = mapOf('I' to 1, 'V' to 5, 'X' to 10, 'L' to 50, 'C' to 100)
    var total = 0
    for (i in s.indices) {
        val a = v.getValue(s[i])
        val b = if (i + 1 < s.length) v.getValue(s[i + 1]) else 0
        total += if (a < b) -a else a
    }
    return total
}

val bigNumbers = lesson(
    "big-numbers", "Lakhs and crores", "Reading big numbers in India", "🏰",
    teach = listOf(
        say("Big numbers have commas to help us read them. In India we group the last three digits, then pairs.", PlaceChart(452318)),
        say("Four lakh, fifty-two thousand, three hundred and eighteen.", Board(listOf(line("4,52,318"), dim("4 lakhs"), dim("52 thousands"), dim("318")))),
        say("Ten thousand is ten times a thousand. A lakh is one hundred thousand.", Board(listOf(line("10,000 = ten thousand"), line("1,00,000 = one lakh"), line("1,00,00,000 = one crore")))),
        show("Count three digits from the right, then put a comma after every two digits.", Board(listOf(line("1234567"), dim("last three: 567"), dim("then pairs: 34, 12"), good("12,34,567"))), Mood.CHEER),
    ),
) { rnd, i ->
    val digits = if (band(i) == 0) 5 else rnd.between(6, 7)
    var n: Int
    do { n = rnd.between(Math.pow(10.0, (digits - 1).toDouble()).toInt(), Math.pow(10.0, digits.toDouble()).toInt() - 1) }
    while (n.toString().none { c -> c != '0' && n.toString().count { it == c } == 1 })
    if (i % 2 == 0) {
        val s = n.toString()
        // a non-zero digit that appears once, so "the digit 5" points at exactly one place
        val pos = (0 until digits).shuffled(rnd).first { p -> s[digits - 1 - p] != '0' && s.count { it == s[digits - 1 - p] } == 1 }
        val d = s[digits - 1 - pos] - '0'
        val value = d * Math.pow(10.0, pos.toDouble()).toInt()
        val wrongs = listOf(d * Math.pow(10.0, (pos + 1).toDouble()).toInt(), d * Math.pow(10.0, (pos - 1).coerceAtLeast(0).toDouble()).toInt(), d, d * Math.pow(10.0, (pos + 2).toDouble()).toInt())
            .filter { it != value && it > 0 }.distinct().take(3).map { Words.indian(it.toLong()) }
        mcText(
            rnd, "In ${Words.indian(n.toLong())}, what is the value of the digit $d?", PlaceChart(n.toLong()), Words.indian(value.toLong()), wrongs,
            "Which place is the digit in? Count the places from the right.",
            listOf(show("The $d is in the ${listOf("ones", "tens", "hundreds", "thousands", "ten-thousands", "lakhs", "ten-lakhs")[pos]} place, so it is worth ${Words.indian(value.toLong())}.", PlaceChart(n.toLong()), Mood.CHEER)),
        )
    } else {
        val correct = Words.indian(n.toLong())
        val s = n.toString()
        val intl = s.reversed().chunked(3).joinToString(",").reversed()
        val pairs = s.reversed().chunked(2).joinToString(",").reversed()
        val wrongs = listOf(intl, pairs, s.take(1) + "," + s.drop(1)).filter { it != correct }.distinct().take(3)
        mcText(
            rnd, "Which shows $s with commas the Indian way?", null, correct, wrongs,
            "Three digits at the end, then pairs going left.",
            listOf(show("Last three digits, then pairs: $correct.", Board(listOf(line(s), good(correct))), Mood.CHEER)),
        )
    }
}

val roundBig = lesson(
    "round-big", "Rounding big numbers", "To the nearest 100 or 1000", "🎯",
    teach = listOf(
        say("To round to the nearest hundred, look at the tens digit. Take 3,462.", Board(listOf(line("3462"), dim("tens digit: 6")))),
        say("Six is five or more, so we round up to 3,500.", Board(listOf(line("3462"), dim("tens digit: 6"), good("round up → 3500")))),
        say("For thousands, look at the hundreds digit. 3,462 has a 4 there, so round down to 3,000.", Board(listOf(line("3462"), dim("hundreds digit: 4"), good("round down → 3000")))),
        show("Five or more? Round up. Four or less? Round down.", Board(listOf(good("0 1 2 3 4  →  down"), hot("5 6 7 8 9  →  up"))), Mood.CHEER),
    ),
) { rnd, i ->
    val thousand = band(i) == 2 && rnd.nextBoolean()
    val unit = if (thousand) 1000 else 100
    val n = rnd.between(if (thousand) 1100 else 120, if (thousand) 9899 else 4899)
    val down = n / unit * unit
    val up = down + unit
    val ans = if (n - down >= unit / 2) up else down
    val checkDigit = (n / (unit / 10)) % 10
    mcNumber(
        rnd, "Round $n to the nearest ${if (thousand) "thousand" else "hundred"}.", null, ans,
        "Look at the ${if (thousand) "hundreds" else "tens"} digit, $checkDigit. Five or more rounds up.",
        listOf(show("The ${if (thousand) "hundreds" else "tens"} digit is $checkDigit, so round ${if (ans == up) "up" else "down"} to $ans.", Board(listOf(line("$n"), dim("check digit: $checkDigit"), good("→ $ans"))), Mood.CHEER)),
        spread = unit, min = unit, max = 12000,
        // other multiples of the unit, plus the "rounded to the wrong place" answer: never a random in-between number
        extra = listOf(down, up, down - unit, up + unit, n / (unit / 10) * (unit / 10)).filter { it != ans && it >= unit },
    )
}

val factors = lesson(
    "factors", "Factors", "Numbers that divide exactly", "🧩",
    teach = listOf(
        say("A factor is a number that divides another number exactly. Let's find the factors of twelve.", Board(listOf(line("12")))),
        say("One times twelve. Two times six. Three times four.", Board(listOf(line("1 × 12 = 12"), line("2 × 6 = 12"), line("3 × 4 = 12")))),
        show("So the factors of twelve are 1, 2, 3, 4, 6 and 12.", Board(listOf(line("1 × 12"), line("2 × 6"), line("3 × 4"), good("factors: 1 2 3 4 6 12"))), Mood.CHEER),
        show("Every number has 1 and itself as factors.", Board(listOf(line("7 → 1, 7"), line("10 → 1, 2, 5, 10")))),
    ),
) { rnd, i ->
    val n = rnd.choose(listOf(12, 16, 18, 20, 24, 28, 30, 36))
    val fs = factorsOf(n)
    val pairs = fs.filter { it * it <= n }.map { line("$it × ${n / it} = $n") }
    val solution = listOf(show("Factor pairs of $n: ${fs.joinToString(", ")}.", Board(pairs + good("factors: ${fs.joinToString(" ")}")), Mood.CHEER))
    if (i % 2 == 0) {
        val k = rnd.choose(fs.filter { it > 1 && it < n })
        val nonFactors = (2..n - 1).filter { n % it != 0 }.shuffled(rnd).take(3)
        mcNumber(rnd, "Which of these is a factor of $n?", null, k, "A factor divides $n exactly, with nothing left over.", solution, spread = 2, min = 2, max = n, extra = nonFactors)
    } else {
        mcNumber(rnd, "How many factors does $n have?", null, fs.size, "List the pairs that multiply to make $n, then count each number.", solution, spread = 2, min = 2, max = 12, extra = listOf(fs.size - 1, fs.size + 1, fs.size / 2))
    }
}

val multiples = lesson(
    "multiples", "Multiples", "The times-table numbers", "🔢",
    teach = listOf(
        say("Multiples are the numbers in a times table. The multiples of four...", Strip(listOf("4", "8", "12", "16", "20", "24")), counting = true),
        show("Each multiple is four more than the last. They are 4 × 1, 4 × 2, 4 × 3 and so on.", Board(listOf(line("4 × 1 = 4"), line("4 × 2 = 8"), line("4 × 3 = 12"), good("4, 8, 12, 16, 20 ...")))),
        show("A number is a multiple of four if four divides it exactly.", Board(listOf(good("24 ÷ 4 = 6  →  24 is a multiple of 4"), hot("25 ÷ 4 leaves 1  →  not a multiple"))), Mood.CHEER),
    ),
) { rnd, i ->
    val k = rnd.between(3, if (band(i) == 0) 6 else 9)
    val m = k * rnd.between(3, if (band(i) == 0) 8 else 12)
    val wrongs = listOf(m + 1, m - 1, m + 2, m - 2, m + k - 1, m - k + 1).filter { it % k != 0 && it > 0 }.distinct().shuffled(rnd).take(3)
    mcNumber(
        rnd, "Which of these is a multiple of $k?", null, m, "Count in ${k}s, or check: does $k divide it exactly?",
        listOf(show("$m ÷ $k = ${m / k} exactly, so $m is a multiple of $k.", eq("$k", "×", "${m / k}", "=", "$m"), Mood.CHEER)),
        spread = 2, min = 2, max = 120, extra = wrongs,
    )
}

val primes = lesson(
    "prime", "Prime numbers", "Only two factors", "⭐",
    teach = listOf(
        say("A prime number has exactly two factors: one and itself. Seven is prime.", Board(listOf(line("7"), good("factors: 1 and 7")))),
        say("Nine is not prime. It has three factors: one, three and nine.", Board(listOf(line("9"), hot("factors: 1, 3, 9")))),
        say("Numbers that are not prime are called composite.", Board(listOf(good("prime: 2 3 5 7 11 13"), hot("composite: 4 6 8 9 10 12")))),
        show("Two is the only even prime. One is neither prime nor composite.", Board(listOf(line("2 is the only even prime"), dim("1 has just one factor"))), Mood.CHEER),
    ),
) { rnd, i ->
    val limit = if (band(i) == 0) 20 else 50
    val primeList = (2..limit).filter { isPrime(it) }
    val composites = (4..limit).filter { !isPrime(it) }
    if (i % 2 == 0) {
        val p = rnd.choose(primeList)
        mcNumber(rnd, "Which of these is a prime number?", null, p, "A prime has only two factors: 1 and itself.", listOf(show("$p has only 1 and $p as factors, so it is prime.", Board(listOf(line("$p"), good("factors: 1, $p"))), Mood.CHEER)), spread = 2, min = 2, max = limit, extra = composites.shuffled(rnd).take(3))
    } else {
        val n = if (rnd.nextBoolean()) rnd.choose(primeList) else rnd.choose(composites)
        val fs = factorsOf(n)
        mcText(
            rnd, "Is $n prime or composite?", null, if (isPrime(n)) "Prime" else "Composite", listOf(if (isPrime(n)) "Composite" else "Prime"),
            "Find all the factors of $n. Only two means prime.",
            listOf(show("Factors of $n: ${fs.joinToString(", ")}. ${if (isPrime(n)) "Only two, so it is prime." else "More than two, so it is composite."}", Board(listOf(line("$n"), (if (isPrime(n)) good("factors: ${fs.joinToString(", ")}") else hot("factors: ${fs.joinToString(", ")}")))), Mood.CHEER)),
        )
    }
}

val divisibility = lesson(
    "divisibility", "Divisibility tests", "Quick ways to check", "✅",
    teach = listOf(
        say("You can tell if a number divides exactly without dividing. Look at the last digit.", Board(listOf(line("by 2: last digit 0, 2, 4, 6, 8"), line("by 5: last digit 0 or 5"), line("by 10: last digit 0")))),
        say("For three and nine, add up the digits. Take 456.", Board(listOf(line("456"), dim("4 + 5 + 6 = 15")))),
        say("Fifteen is in the three times table, so 456 divides by three. It is not in the nine times table, so not by nine.", Board(listOf(line("456"), dim("4 + 5 + 6 = 15"), good("15 is a multiple of 3"), hot("15 is not a multiple of 9")))),
        show("Memorise these tests!", Board(listOf(line("2, 5, 10: look at the last digit"), line("3, 9: add the digits"))), Mood.CHEER),
    ),
) { rnd, i ->
    val d = rnd.choose(if (band(i) == 0) listOf(2, 5, 10) else listOf(2, 3, 5, 9, 10))
    val m = d * rnd.between(if (d >= 9) 12 else 15, 90)
    val wrongs = LinkedHashSet<Int>()
    for (delta in listOf(1, -1, 2, -2, 3, 4)) if ((m + delta) % d != 0 && m + delta > 9) wrongs.add(m + delta)
    val sum = m.toString().map { it - '0' }
    val why = when (d) {
        2 -> "The last digit is ${m % 10}, which is even."
        5 -> "The last digit is ${m % 10}, which is 0 or 5."
        10 -> "The last digit is 0."
        else -> "${sum.joinToString(" + ")} = ${sum.sum()}, and ${sum.sum()} is in the $d times table."
    }
    mcNumber(
        rnd, "Which of these numbers can be divided exactly by $d?", null, m,
        when (d) { 2, 5, 10 -> "Check the last digit."; else -> "Add up the digits. Is the total in the $d times table?" },
        listOf(show(why, Board(listOf(line("$m"), good(why))), Mood.CHEER)),
        spread = 2, min = 10, max = 1000, extra = wrongs.toList().shuffled(rnd).take(3),
    )
}

val hcf = lesson(
    "hcf", "Highest common factor", "The biggest number that fits both", "🏆",
    teach = listOf(
        say("The highest common factor, or H.C.F., is the biggest factor two numbers share. Try 12 and 18.", Board(listOf(line("12  and  18")))),
        say("List the factors of each.", Board(listOf(line("12: 1 2 3 4 6 12"), line("18: 1 2 3 6 9 18")))),
        say("The shared ones are one, two, three and six.", Board(listOf(line("12: 1 2 3 4 6 12"), line("18: 1 2 3 6 9 18"), hot("shared: 1 2 3 6")))),
        show("The highest shared factor is six. The H.C.F. of 12 and 18 is 6.", Board(listOf(line("12: 1 2 3 4 6 12"), line("18: 1 2 3 6 9 18"), dim("shared: 1 2 3 6"), good("H.C.F. = 6"))), Mood.CHEER),
    ),
) { rnd, i ->
    val pairs = if (band(i) == 0) listOf(8 to 12, 12 to 18, 10 to 15, 9 to 12, 6 to 9) else listOf(8 to 12, 12 to 18, 20 to 30, 16 to 24, 14 to 21, 18 to 24, 24 to 36, 15 to 25, 12 to 16, 28 to 42)
    val (a, b) = rnd.choose(pairs)
    val h = gcd(a, b)
    val fa = factorsOf(a); val fb = factorsOf(b)
    val common = fa.filter { it in fb }
    mcNumber(
        rnd, "What is the H.C.F. of $a and $b?", null, h, "List the factors of both numbers. Which is the biggest one they share?",
        listOf(show("Shared factors: ${common.joinToString(", ")}. The highest is $h.", Board(listOf(line("$a: ${fa.joinToString(" ")}"), line("$b: ${fb.joinToString(" ")}"), good("H.C.F. = $h"))), Mood.CHEER)),
        spread = 2, min = 1, max = 40, extra = listOf(lcm(a, b), minOf(a, b), h * 2, common.getOrElse(common.size - 2) { 1 }).filter { it != h && it <= 40 },
    )
}

val lcmLesson = lesson(
    "lcm", "Lowest common multiple", "The first number in both tables", "🎪",
    teach = listOf(
        say("The lowest common multiple, or L.C.M., is the first number that appears in both times tables. Try 4 and 6.", Board(listOf(line("4  and  6")))),
        say("List the multiples of each.", Board(listOf(line("4: 4 8 12 16 20"), line("6: 6 12 18 24")))),
        say("Twelve is the first number in both lists.", Board(listOf(line("4: 4 8 12 16 20"), line("6: 6 12 18 24"), hot("first match: 12")))),
        show("So the L.C.M. of 4 and 6 is 12.", Board(listOf(line("4: 4 8 12 16 20"), line("6: 6 12 18 24"), good("L.C.M. = 12"))), Mood.CHEER),
    ),
) { rnd, i ->
    val pairs = if (band(i) == 0) listOf(2 to 3, 3 to 4, 2 to 5, 4 to 6, 3 to 5) else listOf(4 to 6, 6 to 8, 4 to 10, 6 to 9, 8 to 12, 9 to 12, 10 to 15, 3 to 7, 5 to 6, 4 to 14)
    val (a, b) = rnd.choose(pairs)
    val l = lcm(a, b)
    fun multiplesUpTo(x: Int) = (1..l / x + 1).map { x * it }
    mcNumber(
        rnd, "What is the L.C.M. of $a and $b?", null, l, "List the multiples of each number. Find the first one that appears in both lists.",
        listOf(show("The first match is $l, so the L.C.M. of $a and $b is $l.", Board(listOf(line("$a: ${multiplesUpTo(a).joinToString(" ")}"), line("$b: ${multiplesUpTo(b).joinToString(" ")}"), good("L.C.M. = $l"))), Mood.CHEER)),
        spread = 4, min = 2, max = 150, extra = listOf(a * b, a + b, gcd(a, b).let { if (it == 1) l + a else it }, l * 2).filter { it != l },
    )
}

val bodmas = lesson(
    "bodmas", "Order of operations", "Brackets, then × ÷, then + −", "🎛️",
    teach = listOf(
        say("What is 2 plus 3 times 4? The order we do things in matters!", Equation("2", "+", "3", "×", "4", "=", "?")),
        say("Multiply first: three times four is twelve.", Board(listOf(line("2 + 3 × 4"), hot("3 × 4 = 12")))),
        say("Then add: two plus twelve is fourteen.", Board(listOf(line("2 + 3 × 4"), dim("3 × 4 = 12"), line("2 + 12"), good("= 14")))),
        say("Brackets come first! In 2 plus 3, all times 4, we add first.", Board(listOf(line("(2 + 3) × 4"), hot("(2 + 3) = 5"), line("5 × 4"), good("= 20")))),
        show("Order: brackets, then multiply and divide, then add and subtract.", Board(listOf(line("1. Brackets"), line("2. × and ÷"), line("3. + and −"))), Mood.CHEER),
    ),
) { rnd, i ->
    val kind = when (band(i)) { 0 -> 0; 1 -> rnd.nextInt(2); else -> rnd.nextInt(4) }
    val a = rnd.between(2, 9); val b = rnd.between(2, 6); val c = rnd.between(2, 6)
    when (kind) {
        0 -> {
            val ans = a + b * c
            mcNumber(rnd, "What is $a + $b × $c?", Equation("$a", "+", "$b", "×", "$c", "=", "?"), ans, "Multiply before you add.",
                listOf(say("Multiply first: $b × $c = ${b * c}.", Board(listOf(line("$a + $b × $c"), hot("$b × $c = ${b * c}")))), show("Then add: $a + ${b * c} = $ans.", Board(listOf(line("$a + $b × $c"), dim("$b × $c = ${b * c}"), line("$a + ${b * c}"), good("= $ans"))), Mood.CHEER)),
                spread = 4, min = 1, max = 100, extra = listOf((a + b) * c, ans + 1, ans - 1))
        }
        1 -> {
            val ans = (a + b) * c
            mcNumber(rnd, "What is ($a + $b) × $c?", Equation("($a", "+", "$b)", "×", "$c", "=", "?"), ans, "Do the brackets first.",
                listOf(say("Brackets first: $a + $b = ${a + b}.", Board(listOf(line("($a + $b) × $c"), hot("$a + $b = ${a + b}")))), show("Then multiply: ${a + b} × $c = $ans.", Board(listOf(line("($a + $b) × $c"), dim("$a + $b = ${a + b}"), line("${a + b} × $c"), good("= $ans"))), Mood.CHEER)),
                spread = 5, min = 1, max = 100, extra = listOf(a + b * c, ans + c, ans - c))
        }
        2 -> {
            val cc = rnd.between(1, minOf(6, a * b - 1))   // keeps a × b − c above zero
            val ans = a * b - cc
            mcNumber(rnd, "What is $a × $b − $cc?", Equation("$a", "×", "$b", "−", "$cc", "=", "?"), ans, "Multiply first, then subtract.",
                listOf(say("Multiply first: $a × $b = ${a * b}.", Board(listOf(line("$a × $b − $cc"), hot("$a × $b = ${a * b}")))), show("Then subtract: ${a * b} − $cc = $ans.", Board(listOf(line("$a × $b − $cc"), dim("$a × $b = ${a * b}"), line("${a * b} − $cc"), good("= $ans"))), Mood.CHEER)),
                spread = 4, min = 0, max = 100, extra = listOf(ans + 2 * cc, ans + 1, ans - 1, a * kotlin.math.abs(b - cc).coerceAtLeast(1)).filter { it > 0 && it != ans })
        }
        else -> {
            val k = rnd.between(2, 5)
            val divisor = rnd.between(2, 4)
            val top = divisor * k
            val ans = a + top / divisor * b
            mcNumber(rnd, "What is $a + $top ÷ $divisor × $b?", Equation("$a", "+", "$top", "÷", "$divisor", "×", "$b", "=", "?"), ans, "Do ÷ and × first, from left to right. Then add.",
                listOf(say("Divide first: $top ÷ $divisor = $k. Then multiply: $k × $b = ${k * b}.", Board(listOf(line("$a + $top ÷ $divisor × $b"), hot("$top ÷ $divisor = $k"), hot("$k × $b = ${k * b}")))), show("Then add: $a + ${k * b} = $ans.", Board(listOf(line("$a + $top ÷ $divisor × $b"), dim("$top ÷ $divisor = $k"), dim("$k × $b = ${k * b}"), good("$a + ${k * b} = $ans"))), Mood.CHEER)),
                spread = 4, min = 1, max = 100, extra = listOf((a + top) / divisor * b, ans + b, ans - 1))
        }
    }
}

val negatives = lesson(
    "negative-intro", "Negative numbers", "Numbers below zero", "🌡️",
    teach = listOf(
        say("Numbers can go below zero too. On a cold day the temperature can be minus three.", NumberLine(-5, 5, start = 0)),
        say("Start at zero and hop left. One, two, three steps left is minus three.", NumberLine(-5, 5, start = 0, hops = listOf(-1, -2, -3)), counting = true),
        show("Minus three is written −3. Negative numbers are to the left of zero.", NumberLine(-5, 5, mark = -3)),
        show("The further left, the smaller the number. So −3 is smaller than −1, and −1 is smaller than 2.", Board(listOf(line("−3 < −1"), line("−1 < 2"), good("left = smaller"))), Mood.CHEER),
    ),
) { rnd, i ->
    if (i % 2 == 0) {
        var a = rnd.between(-9, 9); var b = rnd.between(-9, 9)
        if (a >= 0 && b >= 0) a = -rnd.between(1, 9)
        if (a == b) b = a + 1
        val small = minOf(a, b); val big = maxOf(a, b)
        mcText(
            rnd, "Which number is smaller: ${num(a)} or ${num(b)}?", null, num(small), listOf(num(big)),
            "On the number line, the number further left is smaller.",
            listOf(show("${num(small)} is further left than ${num(big)}, so it is smaller.", NumberLine(-10, 10, start = big, hops = listOf(small)), Mood.CHEER)),
        )
    } else {
        val up = rnd.nextBoolean()
        val start = if (up) rnd.between(-7, -2) else rnd.between(-2, 5)
        val items = (0 until 4).map { if (up) start + it else start - it }
        val ans = if (up) start + 4 else start - 4
        mcNumber(
            rnd, "What number comes next?", Strip(items.map { num(it) } + "?", blankAt = 4), ans, if (up) "Each number is one more than the last." else "Each number is one less than the last.",
            listOf(say("The pattern goes ${if (up) "up" else "down"} by one each time.", Strip(items.map { num(it) } + num(ans)), counting = true)),
            spread = 2, min = -12, max = 12, extra = listOf(ans + 1, ans - 1, ans + 2),
        ) { num(it) }
    }
}

private fun hopsFrom(start: Int, steps: Int, dir: Int): List<Int> = (1..steps).map { start + dir * it }

val integerAdd = lesson(
    "integer-add", "Adding negatives", "Moving along the number line", "➕",
    teach = listOf(
        say("Adding a positive number moves right. Adding a negative number moves left.", NumberLine(-10, 10, start = 2)),
        say("Two plus minus five. Start at two and move five steps left.", NumberLine(-10, 10, start = 2, hops = hopsFrom(2, 5, -1)), counting = true),
        show("We land on minus three. So 2 + (−5) = −3.", Stack(NumberLine(-10, 10, start = 2, hops = hopsFrom(2, 5, -1)), eq("2", "+", "(−5)", "=", "−3")), Mood.CHEER),
        show("Minus four plus six. Start at minus four and move six steps right to reach two.", Stack(NumberLine(-10, 10, start = -4, hops = hopsFrom(-4, 6, 1)), eq("−4", "+", "6", "=", "2"))),
    ),
) { rnd, i ->
    val (a, b) = when (band(i)) {
        0 -> { val a = rnd.between(2, 8); a to -rnd.between(1, a) }
        1 -> { val a = -rnd.between(2, 8); a to rnd.between(1, minOf(9, 10 + a)) }
        else -> { val a = -rnd.between(1, 6); a to -rnd.between(1, 4) }
    }
    val ans = a + b
    mcNumber(
        rnd, "What is ${tok(a)} + ${tok(b)}?", NumberLine(-10, 10, start = a), ans,
        "Start at ${num(a)}. ${if (b < 0) "Adding a negative moves left" else "Adding a positive moves right"} by ${kotlin.math.abs(b)}.",
        listOf(
            say("Start at ${num(a)} and move ${kotlin.math.abs(b)} steps ${if (b < 0) "left" else "right"}.", NumberLine(-10, 10, start = a, hops = hopsFrom(a, kotlin.math.abs(b), if (b < 0) -1 else 1)), counting = true),
            show("We land on ${num(ans)}.", eq(tok(a), "+", tok(b), "=", num(ans)), Mood.CHEER),
        ),
        spread = 2, min = -10, max = 10, extra = listOf(a - b, -ans, ans + 1, ans - 1),
    ) { num(it) }
}

val integerSub = lesson(
    "integer-sub", "Subtracting negatives", "Take away, and the double negative", "➖",
    teach = listOf(
        say("Subtracting a positive number moves left. Three minus five: start at three, move five steps left.", NumberLine(-10, 10, start = 3, hops = hopsFrom(3, 5, -1)), counting = true),
        show("We land on minus two. So 3 − 5 = −2.", eq("3", "−", "5", "=", "−2"), Mood.CHEER),
        say("Here is the surprise. Subtracting a negative number moves RIGHT. Two minus minus three.", NumberLine(-10, 10, start = 2, hops = hopsFrom(2, 3, 1)), counting = true, mood = Mood.THINK),
        show("We land on five. So 2 − (−3) = 5. Two minus signs make a plus!", eq("2", "−", "(−3)", "=", "5"), Mood.CHEER),
    ),
) { rnd, i ->
    val negativeB = band(i) == 2 || (band(i) == 1 && i % 2 == 0)
    val a: Int; val b: Int
    if (negativeB) { a = rnd.between(-5, 5); b = -rnd.between(1, 5) } else { a = rnd.between(-4, 8); b = rnd.between(1, 6) }
    val ans = a - b
    mcNumber(
        rnd, "What is ${tok(a)} − ${tok(b)}?", NumberLine(-10, 10, start = a), ans,
        if (b < 0) "Subtracting a negative is the same as adding: move right." else "Subtracting a positive moves left.",
        listOf(
            say(if (b < 0) "Minus a negative means move right by ${-b}." else "Move $b steps left from ${num(a)}.", NumberLine(-10, 10, start = a, hops = hopsFrom(a, kotlin.math.abs(b), if (b < 0) 1 else -1)), counting = true),
            show("We land on ${num(ans)}.", eq(tok(a), "−", tok(b), "=", num(ans)), Mood.CHEER),
        ),
        spread = 2, min = -10, max = 10, extra = listOf(a + b, -ans, ans + 1, ans - 1),
    ) { num(it) }
}

val squares = lesson(
    "squares", "Squares and roots", "Multiplying a number by itself", "🟦",
    teach = listOf(
        say("A square number is a number times itself. Three rows of three make a square.", ArrayGrid(3, 3, "🔷"), counting = true),
        show("Three times three is nine. We write 3 squared.", Stack(ArrayGrid(3, 3, "🔷"), eq("3²", "=", "3 × 3", "=", "9")), Mood.CHEER),
        show("Four squared is sixteen, five squared is twenty-five.", Board(listOf(line("4² = 4 × 4 = 16"), line("5² = 5 × 5 = 25")))),
        show("A square root goes backwards. The square root of twenty-five is five, because 5 × 5 = 25.", Board(listOf(line("5 × 5 = 25"), good("so the square root of 25 is 5"))), Mood.CHEER),
    ),
) { rnd, i ->
    val n = rnd.between(2, if (band(i) == 0) 6 else 12)
    if (i % 2 == 0) {
        mcNumber(rnd, "What is $n²?", Equation("$n²", "=", "?"), n * n, "$n squared means $n × $n.", listOf(show("$n × $n = ${n * n}.", eq("$n", "×", "$n", "=", "${n * n}"), Mood.CHEER)), spread = n, min = 1, max = 150, extra = listOf(n * 2, n * (n + 1), (n + 1) * (n + 1), n * (n - 1)).filter { it > 0 })
    } else {
        mcNumber(rnd, "What is the square root of ${n * n}?", null, n, "Which number times itself makes ${n * n}?", listOf(show("$n × $n = ${n * n}, so the square root of ${n * n} is $n.", Board(listOf(line("$n × $n = ${n * n}"), good("root = $n"))), Mood.CHEER)), spread = 2, min = 1, max = 13, extra = listOf(n * 2, n + 1, n - 1).filter { it > 0 })
    }
}

val exponents = lesson(
    "exponents", "Powers", "Multiplying the same number again and again", "⚡",
    teach = listOf(
        say("A power is a short way to write repeated multiplying. Two to the power three.", Equation("2^3", "=", "?")),
        say("It means three twos multiplied together.", Board(listOf(line("2^3 = 2 × 2 × 2"), dim("2 × 2 = 4"), good("4 × 2 = 8")))),
        show("So two to the power three is eight.", eq("2^3", "=", "8"), Mood.CHEER),
        show("The small number says how many times to write the big number. 10^3 is 10 × 10 × 10 = 1000.", Board(listOf(line("10^2 = 100"), line("10^3 = 1000"), line("10^4 = 10000")))),
    ),
) { rnd, i ->
    val base = rnd.between(2, if (band(i) == 0) 3 else 5)
    val exp = rnd.between(2, if (band(i) == 0) 3 else 4)
    var r = 1; repeat(exp) { r *= base }
    val working = List(exp) { "$base" }.joinToString(" × ")
    mcNumber(
        rnd, "What is $base to the power $exp?", Equation("$base^$exp", "=", "?"), r, "Write $base down $exp times and multiply: $working.",
        listOf(show("$working = $r.", Board(listOf(line("$base^$exp = $working"), good("= $r"))), Mood.CHEER)),
        spread = base, min = 1, max = 700, extra = listOf(base * exp, exp.let { e -> var x = 1; repeat(base) { x *= e }; x }, r / base, r * base).filter { it != r && it > 0 },
    )
}

val romans = lesson(
    "roman", "Roman numerals", "I, V, X, L and C", "🏛️",
    teach = listOf(
        say("The Romans wrote numbers with letters. I is one, V is five, X is ten, L is fifty and C is one hundred.", Board(listOf(line("I = 1"), line("V = 5"), line("X = 10"), line("L = 50"), line("C = 100")))),
        say("Small after big? Add. VI is five plus one, which is six.", Board(listOf(line("VI = 5 + 1 = 6"), line("XII = 10 + 2 = 12")))),
        say("Small before big? Subtract. IV is five minus one, which is four.", Board(listOf(line("IV = 5 − 1 = 4"), line("IX = 10 − 1 = 9"), line("XL = 50 − 10 = 40")))),
        show("You never repeat a letter more than three times in a row: III is three, but four is IV.", Board(listOf(good("III = 3"), hot("IIII is not used"), good("IV = 4"))), Mood.CHEER),
    ),
) { rnd, i ->
    val hi = when (band(i)) { 0 -> 20; 1 -> 39; else -> 99 }
    val n = rnd.between(4, hi)
    val r = toRoman(n)
    if (i % 2 == 0) {
        mcNumber(
            rnd, "What number is $r?", null, n, "Read from the left. Add when the letters get smaller; subtract when a small letter comes before a big one.",
            listOf(show("$r is $n.", Board(listOf(line(r), good("= $n"))), Mood.CHEER)),
            spread = 2, min = 1, max = 100, extra = listOf(n + 1, n - 1, n + 2, n - 2, fromRoman(r.reversed())).filter { it > 0 && it != n },
        )
    } else {
        val wrongs = listOf(n + 1, n - 1, n + 5, n - 5, n + 10).filter { it in 1..100 }.map { toRoman(it) }.filter { it != r }.distinct().shuffled(rnd).take(3)
        mcText(
            rnd, "Which is $n in Roman numerals?", null, r, wrongs, "Break $n into tens, fives and ones.",
            listOf(show("$n is written $r.", Board(listOf(line("$n"), good(r))), Mood.CHEER)),
        )
    }
}

val world7 = World(
    id = "w7", title = "Number Castle", tagline = "Factors, primes, negatives and more", emoji = "🏰",
    color = 0xFFEF4444, level = "Class 4 to 7",
    lessons = listOf(bigNumbers, roundBig, factors, multiples, primes, divisibility, hcf, lcmLesson, bodmas, negatives, integerAdd, integerSub, squares, exponents, romans),
)
