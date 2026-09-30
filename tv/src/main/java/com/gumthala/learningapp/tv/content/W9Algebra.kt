package com.gumthala.learningapp.tv.content

import kotlin.random.Random

/*
 * World 9 - Algebra & Data Planet. Ratio and proportion, money sums (profit, loss, interest), letters
 * for numbers, simple equations on a balance, number rules, and handling data: mean, median, mode,
 * bar graphs and probability.
 */

private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
private fun rs(n: Int) = "₹$n"

private val letters = listOf("x", "y", "n")

/** A bag of red and blue balls, drawn STATIC so it never gives the answer away. */
/** "5 red balls and 1 blue ball" */
private fun bagText(red: Int, blue: Int) = "$red red ${if (red == 1) "ball" else "balls"} and $blue blue ${if (blue == 1) "ball" else "balls"}"

private fun ballBag(red: Int, blue: Int): Visual = when {
    blue == 0 -> Counters("🔴", red, CounterMode.STATIC)
    red == 0 -> Counters("🔵", blue, CounterMode.STATIC)
    else -> Side(listOf(Counters("🔴", red, CounterMode.STATIC), Counters("🔵", blue, CounterMode.STATIC)))
}

// ---- Ratio -----------------------------------------------------------------------------------

val ratio = lesson(
    "ratio", "Ratio", "Comparing two amounts", "🔴",
    teach = listOf(
        say("A ratio compares two amounts. Here are 2 red balls and 3 blue balls.", ballBag(2, 3)),
        show("We write the ratio of red to blue as 2 to 3, or 2 : 3.", Stack(ballBag(2, 3), BigText("2 : 3")), Mood.CHEER),
        show("Order matters! Red to blue is 2 : 3. But blue to red is 3 : 2.", Board(listOf(line("red to blue = 2 : 3"), line("blue to red = 3 : 2")))),
        show("We can make a ratio simpler, just like a fraction. Divide both numbers by the same number.", Board(listOf(line("4 : 6"), dim("÷ 2  and  ÷ 2"), good("= 2 : 3")))),
    ),
) { rnd, i ->
    when (listOf(0, 0, 0, 1, 0, 1, 2, 2)[i.coerceIn(0, 7)]) {
        0 -> {
            var a: Int; var b: Int
            do { a = rnd.between(1, 7); b = rnd.between(1, 7) } while (a == b || gcd(a, b) != 1)
            val t = a + b
            mcText(
                rnd, "A bag has ${bagText(a, b)}. What is the ratio of red to blue?", ballBag(a, b), "$a : $b",
                listOf("$b : $a", "$a : $t", "$t : $a", "${a + 1} : $b").filter { it != "$a : $b" }.shuffled(rnd).take(3),
                "Say the red number first, then the blue number.",
                listOf(show("$a red and $b blue. Red to blue is $a : $b.", Stack(ballBag(a, b), BigText("$a : $b")), Mood.CHEER)),
            )
        }
        1 -> {
            var a: Int; var b: Int
            do { a = rnd.between(1, 6); b = rnd.between(1, 6) } while (a == b || gcd(a, b) != 1)
            val k = rnd.between(2, 5)
            mcText(
                rnd, "Make this ratio simpler: ${a * k} : ${b * k}.", null, "$a : $b",
                listOf("$b : $a", "${a * k} : $b", "$a : ${b + 1}", "${a + 1} : $b").filter { it != "$a : $b" }.shuffled(rnd).take(3),
                "Divide both numbers by the same number.",
                listOf(show("Divide both by $k. ${a * k} ÷ $k = $a and ${b * k} ÷ $k = $b.", Board(listOf(line("${a * k} : ${b * k}"), dim("÷ $k  and  ÷ $k"), good("= $a : $b"))), Mood.CHEER)),
            )
        }
        else -> {
            var a: Int; var b: Int
            do { a = rnd.between(1, 5); b = rnd.between(1, 5) } while (a == b || gcd(a, b) != 1)
            val k = rnd.between(2, 6)
            mcNumber(
                rnd, "Boys to girls is $a : $b. There are ${a * k} boys. How many girls are there?", null, b * k,
                "Find how many times bigger the boys number is, then do the same for girls.",
                listOf(
                    say("$a became ${a * k}. That is $a × $k = ${a * k}, so multiply by $k.", Board(listOf(line("boys : girls"), line("$a : $b"), dim("× $k  and  × $k"), good("${a * k} : ${b * k}")))),
                    show("So there are ${b * k} girls.", Board(listOf(line("${a * k} : ${b * k}"), good("girls = ${b * k}"))), Mood.CHEER),
                ),
                spread = 3, min = 1, max = 60, extra = listOf(b, a * k + b, b + k, a * k - a + b).filter { it != b * k },
            )
        }
    }
}

private class Share(val a: Int, val b: Int)

private val sharePairsEasy = listOf(Share(1, 2), Share(1, 3), Share(2, 1), Share(3, 1))
private val sharePairsMid = sharePairsEasy + listOf(Share(2, 3), Share(3, 2), Share(1, 4), Share(4, 1))
private val sharePairsHard = sharePairsMid + listOf(Share(3, 5), Share(2, 5), Share(5, 3), Share(3, 4))

val ratioShare = lesson(
    "ratio-share", "Sharing in a ratio", "Split a total into parts", "🍫",
    teach = listOf(
        say("Share 20 sweets between Asha and Ravi in the ratio 1 to 3.", Board(listOf(line("20 sweets"), line("Asha : Ravi = 1 : 3")))),
        say("Add the parts. One part plus three parts makes 4 equal parts.", Board(listOf(line("1 + 3 = 4 parts")))),
        say("Each part is 20 divided by 4, which is 5.", Board(listOf(line("1 + 3 = 4 parts"), line("20 ÷ 4 = 5 in each part")))),
        show("Asha gets 1 part, so 5. Ravi gets 3 parts, so 15. Check: 5 + 15 = 20.", Board(listOf(line("Asha: 1 × 5 = 5"), line("Ravi: 3 × 5 = 15"), good("5 + 15 = 20 ✓"))), Mood.CHEER),
    ),
) { rnd, i ->
    val pool = when (band(i)) { 0 -> sharePairsEasy; 1 -> sharePairsMid; else -> sharePairsHard }
    val p = rnd.choose(pool)
    val k = rnd.between(2, if (band(i) == 0) 6 else 9)
    val parts = p.a + p.b
    val total = parts * k
    val names = kidNames.shuffled(rnd)
    val first = names[0]; val second = names[1]
    val thing = rnd.choose(listOf("marbles", "stickers", "sweets", "coins", "stamps"))
    val askSecond = rnd.nextBoolean()
    val who = if (askSecond) second else first
    val ans = if (askSecond) p.b * k else p.a * k
    val other = if (askSecond) p.a * k else p.b * k
    val mine = if (askSecond) p.b else p.a
    mcNumber(
        rnd, "Share $total $thing between $first and $second in the ratio ${p.a} : ${p.b}. How many does $who get?", null, ans,
        "Add the parts, divide the total by that, then multiply by $who's part.",
        listOf(
            say("${p.a} + ${p.b} = $parts parts. $total ÷ $parts = $k in each part.", Board(listOf(line("${p.a} + ${p.b} = $parts parts"), line("$total ÷ $parts = $k each part")))),
            show("$who has $mine ${if (mine == 1) "part" else "parts"}: $mine × $k = $ans.", Board(listOf(line("$total ÷ $parts = $k"), good("$who: $mine × $k = $ans"))), Mood.CHEER),
        ),
        spread = 3, min = 1, max = total, extra = listOf(k, other, total / 2, total - k).filter { it != ans && it > 0 },
    )
}

// ---- Unitary method --------------------------------------------------------------------------

val unitary = lesson(
    "unitary", "Unitary method", "Find the price of one first", "☝️",
    teach = listOf(
        say("The unitary method has two steps. First find the cost of one. Then find the cost of many.", Board(listOf(line("step 1: find ONE"), line("step 2: find MANY")))),
        say("4 pencils cost 20 rupees. One pencil costs 20 divided by 4, which is 5 rupees.", Board(listOf(line("4 pencils = ${rs(20)}"), good("1 pencil = 20 ÷ 4 = ${rs(5)}")))),
        show("Now 7 pencils cost 7 times 5, which is 35 rupees.", Board(listOf(line("1 pencil = ${rs(5)}"), good("7 pencils = 7 × 5 = ${rs(35)}"))), Mood.CHEER),
    ),
) { rnd, i ->
    val item = rnd.choose(listOf("pens" to "pen", "notebooks" to "notebook", "mangoes" to "mango", "toffees" to "toffee", "erasers" to "eraser"))
    val topPrice = when (item.second) { "notebook" -> 30; "mango" -> 20; "pen" -> 15; "eraser" -> 8; else -> 6 }
    val one = rnd.between(2, minOf(topPrice, if (band(i) == 0) 9 else 30))
    val n = rnd.between(2, if (band(i) == 0) 6 else 9)
    var m: Int
    do { m = rnd.between(2, 12) } while (m == n)
    val cost = one * n
    val travel = band(i) == 2 && i % 2 == 0
    when {
        band(i) == 0 -> mcNumber(
            rnd, "$n ${item.first} cost ${rs(cost)}. What does 1 ${item.second} cost?", null, one,
            "Divide the total cost by the number of ${item.first}.",
            listOf(show("${rs(cost)} ÷ $n = ${rs(one)} for one ${item.second}.", Board(listOf(line("$n ${item.first} = ${rs(cost)}"), good("1 ${item.second} = $cost ÷ $n = ${rs(one)}"))), Mood.CHEER)),
            spread = 3, min = 1, max = 60, extra = listOf(cost - n, cost + n, n, cost / 2).filter { it != one && it > 0 },
        ) { rs(it) }
        travel -> {
            val speed = rnd.between(20, 60)
            val h1 = rnd.between(2, 4)
            var h2: Int
            do { h2 = rnd.between(2, 8) } while (h2 == h1)
            mcNumber(
                rnd, "A bus goes ${speed * h1} km in $h1 hours. How far does it go in $h2 hours at the same speed?", null, speed * h2,
                "Find the distance in one hour first.",
                listOf(show("${speed * h1} ÷ $h1 = $speed km in 1 hour. $speed × $h2 = ${speed * h2}.", Board(listOf(line("$h1 hours = ${speed * h1} km"), line("1 hour = ${speed * h1} ÷ $h1 = $speed km"), good("$h2 hours = $h2 × $speed = ${speed * h2} km"))), Mood.CHEER)),
                spread = 10, min = 20, max = 600, extra = listOf(speed, speed * h1 + speed * h2, speed * h1 * h2, speed * h1 - speed).filter { it != speed * h2 && it > 0 },
            ) { "$it km" }
        }
        else -> mcNumber(
            rnd, "$n ${item.first} cost ${rs(cost)}. What do $m ${item.first} cost?", null, one * m,
            "Find the cost of one, then multiply by $m.",
            listOf(
                say("One ${item.second} costs $cost ÷ $n = ${rs(one)}.", Board(listOf(line("$n ${item.first} = ${rs(cost)}"), line("1 ${item.second} = $cost ÷ $n = ${rs(one)}")))),
                show("$m ${item.first} cost $m × $one = ${rs(one * m)}.", Board(listOf(line("1 ${item.second} = ${rs(one)}"), good("$m ${item.first} = $m × $one = ${rs(one * m)}"))), Mood.CHEER),
            ),
            spread = 6, min = 2, max = 300, extra = listOf(one, cost + m, cost * m, cost - n + m).filter { it != one * m && it > 0 },
        ) { rs(it) }
    }
}

// ---- Profit, loss, interest ------------------------------------------------------------------

val profitLoss = lesson(
    "profit-loss", "Profit and loss", "Buying and selling for more or less", "💸",
    teach = listOf(
        say("A shop buys a toy for 40 rupees. That is the cost price.", Board(listOf(good("cost price (CP) = ${rs(40)}")))),
        say("The shop sells it for 55 rupees. That is the selling price.", Board(listOf(line("cost price (CP) = ${rs(40)}"), good("selling price (SP) = ${rs(55)}")))),
        show("Sold for more than it cost? That is a profit. Profit = SP − CP = 55 − 40 = 15 rupees.", Board(listOf(line("SP ${rs(55)} is more than CP ${rs(40)}"), good("profit = 55 − 40 = ${rs(15)}"))), Mood.CHEER),
        show("Sold for less than it cost? That is a loss. Loss = CP − SP.", Board(listOf(line("CP ${rs(50)}, SP ${rs(42)}"), hot("loss = 50 − 42 = ${rs(8)}")))),
    ),
) { rnd, i ->
    val name = rnd.choose(kidNames)
    val thing = rnd.choose(listOf("a toy", "a kite", "a book", "a bag", "a cap"))
    val cp = rnd.between(4, if (band(i) == 0) 12 else 30) * 5
    val diff = rnd.between(1, if (band(i) == 0) 6 else 14) * 5
    val profit = rnd.nextBoolean()
    when (listOf(0, 1, 0, 2, 1, 2, 3, 3)[i.coerceIn(0, 7)]) {
        0 -> mcNumber(
            rnd, "$name buys $thing for ${rs(cp)} and sells it for ${rs(cp + diff)}. What is the profit?", null, diff,
            "Profit is selling price take away cost price.",
            listOf(show("${cp + diff} − $cp = $diff. The profit is ${rs(diff)}.", Board(listOf(line("SP = ${rs(cp + diff)}"), line("CP = ${rs(cp)}"), good("profit = ${cp + diff} − $cp = ${rs(diff)}"))), Mood.CHEER)),
            spread = 10, min = 5, max = 200, extra = listOf(cp + diff, cp, diff + 5, diff - 5).filter { it != diff && it > 0 },
        ) { rs(it) }
        1 -> mcNumber(
            rnd, "$name buys $thing for ${rs(cp + diff)} and sells it for ${rs(cp)}. What is the loss?", null, diff,
            "Loss is cost price take away selling price.",
            listOf(show("${cp + diff} − $cp = $diff. The loss is ${rs(diff)}.", Board(listOf(line("CP = ${rs(cp + diff)}"), line("SP = ${rs(cp)}"), hot("loss = ${cp + diff} − $cp = ${rs(diff)}"))), Mood.CHEER)),
            spread = 10, min = 5, max = 200, extra = listOf(cp + diff, cp, diff + 5, diff - 5).filter { it != diff && it > 0 },
        ) { rs(it) }
        2 -> {
            val buy = if (profit) cp else cp + diff
            val sell = if (profit) cp + diff else cp
            val right = if (profit) "profit of ${rs(diff)}" else "loss of ${rs(diff)}"
            val swapped = if (profit) "loss of ${rs(diff)}" else "profit of ${rs(diff)}"
            mcText(
                rnd, "$name buys $thing for ${rs(buy)} and sells it for ${rs(sell)}. What happens?", null, right,
                listOf(swapped, "${if (profit) "profit" else "loss"} of ${rs(diff + 5)}", "no profit and no loss"),
                "Is the selling price more or less than the cost price?",
                listOf(show(
                    if (profit) "Sold for ${rs(sell)}, which is more than ${rs(buy)}. Profit = ${sell - buy}." else "Sold for ${rs(sell)}, which is less than ${rs(buy)}. Loss = ${buy - sell}.",
                    Board(listOf(line("CP = ${rs(buy)}"), line("SP = ${rs(sell)}"), if (profit) good("profit = ${sell - buy}") else hot("loss = ${buy - sell}"))), Mood.CHEER,
                )),
            )
        }
        else -> mcNumber(
            rnd, "$name buys $thing for ${rs(cp)} and wants a profit of ${rs(diff)}. What should the selling price be?", null, cp + diff,
            "Selling price = cost price + profit.",
            listOf(show("${cp} + $diff = ${cp + diff}. Sell it for ${rs(cp + diff)}.", Board(listOf(line("CP = ${rs(cp)}"), line("profit = ${rs(diff)}"), good("SP = $cp + $diff = ${rs(cp + diff)}"))), Mood.CHEER)),
            spread = 10, min = 5, max = 300, extra = listOf(cp - diff, diff, cp * 2, cp + diff + 5).filter { it != cp + diff && it > 0 },
        ) { rs(it) }
    }
}

val simpleInterest = lesson(
    "simple-interest", "Simple interest", "Extra money a bank pays you", "🏦",
    teach = listOf(
        say("When you keep money in a bank, the bank pays you extra. That extra money is called interest.", BigText("interest", "extra money for saving", "🏦")),
        say("The money you put in is the principal. The rate is how many rupees you get for every 100 rupees, each year.", Board(listOf(line("principal  P = ${rs(1000)}"), line("rate  R = 5 % a year"), line("time  T = 2 years")))),
        say("Simple interest equals principal times rate times time, divided by 100.", Board(listOf(good("SI = P × R × T ÷ 100")))),
        show("Put in the numbers. 1000 times 5 times 2 is 10000. Divide by 100 to get 100 rupees.", Board(listOf(line("1000 × 5 × 2 = 10000"), line("10000 ÷ 100 = 100"), good("interest = ${rs(100)}"))), Mood.CHEER),
    ),
) { rnd, i ->
    val p = rnd.between(1, if (band(i) == 0) 5 else 20) * 100
    val r = rnd.choose(if (band(i) == 0) listOf(5, 10) else listOf(2, 4, 5, 8, 10))
    val t = rnd.between(1, if (band(i) == 0) 2 else 5)
    val si = p * r * t / 100
    val yearWord = if (t == 1) "year" else "years"
    val askAmount = band(i) == 2 && i % 2 == 1
    val ans = if (askAmount) p + si else si
    val prompt = if (askAmount) "Put ${rs(p)} in a bank at $r% simple interest for $t $yearWord. How much money is there at the end?"
    else "Find the simple interest on ${rs(p)} at $r% a year for $t $yearWord."
    mcNumber(
        rnd, prompt, null, ans,
        if (askAmount) "First find the interest, then add it to the money you started with." else "Interest = P × R × T ÷ 100.",
        listOf(
            say("$p × $r × $t = ${p * r * t}. Divide by 100: ${p * r * t} ÷ 100 = $si.", Board(listOf(line("$p × $r × $t = ${p * r * t}"), line("${p * r * t} ÷ 100 = $si"), good("interest = ${rs(si)}")))),
            show(if (askAmount) "Amount = $p + $si = ${rs(p + si)}." else "The simple interest is ${rs(si)}.", Board(listOf(if (askAmount) line("amount = principal + interest") else line("SI = ${rs(si)}"), good(if (askAmount) "$p + $si = ${rs(p + si)}" else "SI = ${rs(si)}"))), Mood.CHEER),
        ),
        spread = si / 4 + 2, min = 1, max = 5000,
        extra = if (askAmount) listOf(si, p, p - si, p + si * 2) else listOf(p * r / 100, p * r * t / 10, p + si, si * 2, si + t),
    ) { rs(it) }
}

// ---- Letters for numbers and equations -------------------------------------------------------

val variables = lesson(
    "variables", "Letters for numbers", "What does x mean?", "🔤",
    teach = listOf(
        say("In algebra, a letter like x stands for a number. It can be any number we choose.", BigText("x", "a number in disguise", "🎭")),
        say("Look at x plus 3. If x is 4, swap the x for 4.", Board(listOf(line("x + 3"), dim("x = 4"), good("4 + 3 = 7")))),
        say("3x means 3 times x. There is no times sign, it is hiding. If x is 5, then 3x is 15.", Board(listOf(line("3x  means  3 × x"), dim("x = 5"), good("3 × 5 = 15")))),
        show("Two steps: 2x + 1 with x = 3. First 2 × 3 = 6. Then 6 + 1 = 7.", Board(listOf(line("2x + 1"), dim("x = 3"), line("2 × 3 = 6"), good("6 + 1 = 7"))), Mood.CHEER),
    ),
) { rnd, i ->
    val v = rnd.choose(letters)
    val kind = when (band(i)) { 0 -> 0; 1 -> if (i % 2 == 1) 1 else 0; else -> if (i % 2 == 0) 2 else 1 }
    when (kind) {
        0 -> {
            val x = rnd.between(2, 9); val a = rnd.between(1, 8)
            val plus = rnd.nextBoolean() || x <= a
            val ans = if (plus) x + a else x - a
            val expr = if (plus) "$v + $a" else "$v − $a"
            mcNumber(
                rnd, "If $v = $x, what is $expr?", Board(listOf(line("$v = $x"), line("$expr = ?"))), ans,
                "Swap the $v for $x, then work it out.",
                listOf(show("$x ${if (plus) "+" else "−"} $a = $ans.", Board(listOf(line(expr), dim("$v = $x"), good("$x ${if (plus) "+" else "−"} $a = $ans"))), Mood.CHEER)),
                spread = 2, min = 0, max = 30, extra = listOf(if (plus) x - a else x + a, x, a).filter { it != ans && it >= 0 },
            )
        }
        1 -> {
            val x = rnd.between(2, 9); val k = rnd.between(2, 9)
            mcNumber(
                rnd, "If $v = $x, what is ${k}$v?", Board(listOf(line("$v = $x"), line("${k}$v = ?"))), k * x,
                "${k}$v means $k × $v. Swap the $v for $x.",
                listOf(show("$k × $x = ${k * x}.", Board(listOf(line("${k}$v  means  $k × $v"), dim("$v = $x"), good("$k × $x = ${k * x}"))), Mood.CHEER)),
                spread = 4, min = 1, max = 100, extra = listOf(k + x, k * x + k, k * x - x, x).filter { it != k * x && it > 0 },
            )
        }
        else -> {
            val x = rnd.between(2, 8); val k = rnd.between(2, 5); val a = rnd.between(1, 9)
            val ans = k * x + a
            mcNumber(
                rnd, "If $v = $x, what is ${k}$v + $a?", Board(listOf(line("$v = $x"), line("${k}$v + $a = ?"))), ans,
                "First do $k × $x. Then add $a.",
                listOf(show("$k × $x = ${k * x}, then ${k * x} + $a = $ans.", Board(listOf(line("${k}$v + $a"), dim("$v = $x"), line("$k × $x = ${k * x}"), good("${k * x} + $a = $ans"))), Mood.CHEER)),
                spread = 4, min = 2, max = 100, extra = listOf(k + x + a, k * (x + a), x + a, k * x).filter { it != ans && it > 0 },
            )
        }
    }
}

val equations1 = lesson(
    "equations-1", "Mystery number", "Keep the balance level", "❓",
    teach = listOf(
        say("An equation is like a balance. Both sides weigh the same. Here x plus 3 balances 8.", Balance("x + 3", "8", 0)),
        say("To find x, take 3 away from both sides. The balance stays level.", Balance("x + 3 − 3", "8 − 3", 0)),
        say("Now x on its own balances 5. So x equals five.", Balance("x", "5", 0), mood = Mood.CHEER),
        show("Check it: 5 + 3 = 8. Yes! Whatever you do to one side, do to the other.", Board(listOf(line("x + 3 = 8"), line("x = 8 − 3"), good("x = 5"), dim("check: 5 + 3 = 8 ✓"))), Mood.CHEER),
    ),
) { rnd, i ->
    val subtract = band(i) == 2 || (band(i) == 1 && i % 2 == 1)
    val v = rnd.choose(letters)
    if (!subtract) {
        val x = rnd.between(1, if (band(i) == 0) 9 else 20); val a = rnd.between(1, if (band(i) == 0) 8 else 15)
        val b = x + a
        mcNumber(
            rnd, "Solve: $v + $a = $b. What is $v?", Balance("$v + $a", "$b", 0), x,
            "Take $a away from both sides.",
            listOf(
                say("Take $a away from both sides.", Balance("$v + $a − $a", "$b − $a", 0)),
                show("So $v = $b − $a = $x.", Balance(v, "$x", 0), Mood.CHEER),
            ),
            spread = 3, min = 1, max = 40, extra = listOf(b + a, a, b).filter { it != x && it > 0 },
        )
    } else {
        val a = rnd.between(1, 9); val b = rnd.between(2, 14)
        val x = a + b
        mcNumber(
            rnd, "Solve: $v − $a = $b. What is $v?", Balance("$v − $a", "$b", 0), x,
            "Add $a to both sides, because adding undoes taking away.",
            listOf(
                say("Add $a to both sides.", Balance("$v − $a + $a", "$b + $a", 0)),
                show("So $v = $b + $a = $x.", Balance(v, "$x", 0), Mood.CHEER),
            ),
            spread = 3, min = 1, max = 40, extra = listOf(b - a, a, b).filter { it != x && it > 0 },
        )
    }
}

val equations2 = lesson(
    "equations-2", "Two-step equations", "Undo one thing, then undo the next", "✌️",
    teach = listOf(
        say("Some equations need two steps. Here 2x plus 3 balances 11.", Balance("2x + 3", "11", 0)),
        say("Step one: take 3 away from both sides.", Balance("2x + 3 − 3", "11 − 3", 0)),
        say("Now 2x balances 8. Two lots of x make 8.", Balance("2x", "8", 0)),
        say("Step two: divide both sides by 2. Then x is 4.", Balance("x", "4", 0), mood = Mood.CHEER),
        show("Check it: 2 × 4 + 3 = 11 ✓. Undo the adding first, then undo the times.", Board(listOf(line("2x + 3 = 11"), line("2x = 11 − 3 = 8"), line("x = 8 ÷ 2"), good("x = 4"))), Mood.CHEER),
    ),
) { rnd, i ->
    val v = rnd.choose(letters)
    val twoStep = band(i) == 2 || (band(i) == 1 && i % 2 == 0)
    if (!twoStep) {
        val k = rnd.between(2, 9); val x = rnd.between(2, 9)
        mcNumber(
            rnd, "Solve: ${k}$v = ${k * x}. What is $v?", Balance("${k}$v", "${k * x}", 0), x,
            "${k}$v means $k × $v. Divide both sides by $k.",
            listOf(
                say("Divide both sides by $k.", Balance("${k}$v ÷ $k", "${k * x} ÷ $k", 0)),
                show("So $v = ${k * x} ÷ $k = $x.", Balance(v, "$x", 0), Mood.CHEER),
            ),
            spread = 2, min = 1, max = 30, extra = listOf(k * x - k, k, k * x).filter { it != x && it > 0 },
        )
    } else {
        val k = rnd.between(2, if (band(i) == 1) 4 else 6); val x = rnd.between(1, if (band(i) == 1) 6 else 9); val a = rnd.between(1, 9)
        val b = k * x + a
        mcNumber(
            rnd, "Solve: ${k}$v + $a = $b. What is $v?", Balance("${k}$v + $a", "$b", 0), x,
            "First take $a away from both sides. Then divide by $k.",
            listOf(
                say("Take $a away from both sides: $b − $a = ${b - a}.", Balance("${k}$v", "${b - a}", 0)),
                show("Divide by $k: ${b - a} ÷ $k = $x. So $v = $x.", Balance(v, "$x", 0), Mood.CHEER),
            ),
            spread = 2, min = 1, max = 30, extra = listOf(b - a, x + 1, x - 1, a, k).filter { it != x && it > 0 },
        )
    }
}

// ---- Number rules ----------------------------------------------------------------------------

val numberRules = lesson(
    "number-rules", "Number rules", "Find the rule, find the next one", "🔮",
    teach = listOf(
        say("A number pattern follows a rule. Look at 3, 6, 9, 12. What is the rule?", Strip(listOf("3", "6", "9", "12"), arrows = true, note = "+3")),
        say("Each number is 3 more than the one before. The rule is add 3. So the next number is 15.", Strip(listOf("3", "6", "9", "12", "?"), blankAt = 4, arrows = true, note = "+3"), mood = Mood.THINK),
        say("Some patterns multiply. 2, 4, 8, 16. Each number is double the one before.", Strip(listOf("2", "4", "8", "16"), arrows = true, note = "×2")),
        show("There is a fast way. For 3, 6, 9, 12 the nth number is 3 × n. The 10th number is 3 × 10 = 30.", Board(listOf(line("1st: 3 × 1 = 3"), line("2nd: 3 × 2 = 6"), line("3rd: 3 × 3 = 9"), good("10th: 3 × 10 = 30"))), Mood.CHEER),
    ),
) { rnd, i ->
    when (listOf(0, 0, 1, 2, 1, 3, 4, 4)[i.coerceIn(0, 7)]) {
        0 -> {
            val d = rnd.between(2, 7); val a = rnd.between(1, 10)
            val items = (0..3).map { "${a + d * it}" }
            mcNumber(
                rnd, "What number comes next?  ${items.joinToString(", ")}, ?", Strip(items + "?", blankAt = 4, arrows = true), a + d * 4,
                "How much bigger is each number than the one before?",
                listOf(show("The rule is add $d. ${a + d * 3} + $d = ${a + d * 4}.", Strip(items + "${a + d * 4}", arrows = true, note = "+$d"), Mood.CHEER)),
                spread = 3, min = 1, max = 80, extra = listOf(a + d * 3 + d + 1, a + d * 3 + d - 1, a + d * 3 + 1).filter { it != a + d * 4 && it > 0 },
            )
        }
        1 -> {
            val d = rnd.between(2, 9); val a = rnd.between(1, 12)
            val items = (0..4).map { a + d * it }
            val blank = rnd.between(1, 3)
            mcNumber(
                rnd, "Find the missing number:  ${items.mapIndexed { idx, n -> if (idx == blank) "?" else "$n" }.joinToString(", ")}", Strip(items.mapIndexed { idx, n -> if (idx == blank) "?" else "$n" }, blankAt = blank, arrows = true), items[blank],
                "Find how much each step goes up, then fill the gap.",
                listOf(show("The rule is add $d. So the missing number is ${items[blank]}.", Strip(items.map { "$it" }, arrows = true, note = "+$d"), Mood.CHEER)),
                spread = 3, min = 1, max = 80, extra = listOf(items[blank] + 1, items[blank] - 1, items[blank] + d, items[blank] - d).filter { it != items[blank] && it > 0 },
            )
        }
        2 -> {
            val a = rnd.between(1, 3) * 10 + rnd.between(2, 3) * 10; val d = rnd.between(2, 6)
            val items = (0..3).map { "${a - d * it}" }
            mcNumber(
                rnd, "This pattern goes down.  ${items.joinToString(", ")}, ?  What comes next?", Strip(items + "?", blankAt = 4, arrows = true), a - d * 4,
                "How much smaller is each number than the one before?",
                listOf(show("The rule is take away $d. ${a - d * 3} − $d = ${a - d * 4}.", Strip(items + "${a - d * 4}", arrows = true, note = "−$d"), Mood.CHEER)),
                spread = 3, min = 1, max = 80, extra = listOf(a - d * 3 + d, a - d * 3 - d - 1, a - d * 3 - d + 1).filter { it != a - d * 4 && it > 0 },
            )
        }
        3 -> {
            val a = rnd.between(1, 4); val r = rnd.choose(listOf(2, 3))
            val nums = (0..4).map { k -> var v = a; repeat(k) { v *= r }; v }
            val items = nums.take(4).map { "$it" }
            val next = nums[4]
            mcNumber(
                rnd, "Each number is ${if (r == 2) "double" else "three times"} the one before.  ${items.joinToString(", ")}, ?", Strip(items + "?", blankAt = 4, arrows = true), next,
                "${if (r == 2) "Double" else "Multiply by 3"} the last number.",
                listOf(show("${nums[3]} × $r = $next.", Strip(items + "$next", arrows = true, note = "×$r"), Mood.CHEER)),
                spread = 4, min = 2, max = 400, extra = listOf(nums[3] + (nums[3] - nums[2]), nums[3] * (r + 1), nums[3] + r, next - 1).filter { it != next && it > 0 },
            )
        }
        else -> {
            val k = rnd.between(2, 9); val a = rnd.choose(listOf(0, 1, 2, 3)); val n = rnd.between(6, 10)
            val ans = k * n + a
            val rule = if (a == 0) "${k}n" else "${k}n + $a"
            mcNumber(
                rnd, "The nth number is $rule. What is the ${n}th number?", Board(listOf(line("rule:  $rule"), line("n = $n"))), ans,
                "Swap n for $n in the rule.",
                listOf(show(if (a == 0) "$k × $n = $ans." else "$k × $n = ${k * n}, and ${k * n} + $a = $ans.", Board(listOf(line(rule), dim("n = $n"), line("$k × $n = ${k * n}"), good("answer = $ans"))), Mood.CHEER)),
                spread = 5, min = 2, max = 120, extra = listOf(k + n + a, k * n, k * (n + a), ans + k).filter { it != ans && it > 0 },
            )
        }
    }
}

// ---- Data ------------------------------------------------------------------------------------

private fun meanOf(v: List<Int>) = v.sum() / v.size

/** A list of [n] numbers in 1..[hi] whose sum is a multiple of n, so the mean is a whole number. */
private fun meanSet(rnd: Random, n: Int, hi: Int): List<Int> {
    var list: List<Int>
    var guard = 0
    do {
        list = List(n) { rnd.between(1, hi) }
        guard++
    } while ((list.sum() % n != 0 || list.toSet().size < 2) && guard < 400)
    if (list.sum() % n != 0) list = List(n) { 6 } // never reached in practice
    return list
}

val meanLesson = lesson(
    "mean", "The mean", "The fair share average", "⚖️",
    teach = listOf(
        say("The mean is the fair share. Here are three piles of books: 3, 5 and 4.", Bars(listOf("A", "B", "C"), listOf(3, 5, 4))),
        say("Add them all up. Three plus five plus four makes twelve.", Board(listOf(line("3 + 5 + 4"), good("= 12")))),
        say("Share the twelve fairly between the 3 piles. Twelve divided by three is four.", Board(listOf(line("3 + 5 + 4 = 12"), line("12 ÷ 3 = 4"), good("mean = 4")))),
        show("The mean is: add everything up, then divide by how many numbers there are.", Board(listOf(good("mean = total ÷ how many"))), Mood.CHEER),
    ),
) { rnd, i ->
    val n = if (band(i) == 0) 3 else rnd.between(3, 5)
    val values = meanSet(rnd, n, if (band(i) == 0) 9 else 15)
    val m = meanOf(values)
    val sum = values.sum()
    mcNumber(
        rnd, "What is the mean of ${values.joinToString(", ")}?",
        if (band(i) == 0) Bars(listOf("A", "B", "C", "D", "E").take(n), values) else null, m,
        "Add them all up, then divide by ${Words.number(n)}.",
        listOf(show("${values.joinToString(" + ")} = $sum. $sum ÷ $n = $m.", Board(listOf(line(values.joinToString(" + ") + " = $sum"), line("$sum ÷ $n = $m"), good("mean = $m"))), Mood.CHEER)),
        spread = 2, min = 1, max = 30, extra = listOf(sum, m + 1, m - 1, values.max(), values.min(), sum / (n + 1)).filter { it != m && it > 0 },
    )
}

/** Five (or seven) numbers that have exactly one most-common value. */
private fun modeSet(rnd: Random, size: Int): Pair<List<Int>, Int> {
    val mode = rnd.between(1, 12)
    val times = if (size >= 7) 3 else 2
    val others = (1..14).filter { it != mode }.shuffled(rnd).take(size - times)
    return (List(times) { mode } + others).shuffled(rnd) to mode
}

val medianMode = lesson(
    "median-mode", "Mode, median and range", "Most common, middle, spread", "📊",
    teach = listOf(
        say("The mode is the number that appears most often. In 2, 3, 3, 5, 3, 7 the mode is 3.", Board(listOf(line("2  3  3  5  3  7"), good("mode = 3 (three times)")))),
        say("The median is the middle number once they are in order. Take 9, 3, 5. Put them in order first: 3, 5, 9.", Board(listOf(line("9  3  5"), dim("in order:"), line("3  5  9"), good("median = 5")))),
        say("The range shows how spread out the numbers are. It is the biggest number take away the smallest.", Board(listOf(line("3  5  9"), line("biggest 9, smallest 3"), good("range = 9 − 3 = 6")))),
        show("Mode: most often. Median: the middle. Range: biggest minus smallest.", Board(listOf(line("mode = most often"), line("median = the middle one"), line("range = biggest − smallest"))), Mood.CHEER),
    ),
) { rnd, i ->
    when (listOf(0, 0, 0, 1, 1, 2, 1, 2)[i.coerceIn(0, 7)]) {
        0 -> {
            val (list, mode) = modeSet(rnd, if (band(i) == 0) 5 else 7)
            mcNumber(
                rnd, "What is the mode of ${list.joinToString(", ")}?", null, mode,
                "The mode is the number you see most often.",
                listOf(show("$mode appears ${list.count { it == mode }} times, more than any other. The mode is $mode.", Board(listOf(line(list.joinToString("  ")), good("mode = $mode"))), Mood.CHEER)),
                spread = 2, min = 1, max = 15, extra = list.filter { it != mode },
            )
        }
        1 -> {
            val list = (1..20).shuffled(rnd).take(5)
            val sorted = list.sorted()
            val med = sorted[2]
            val showSorted = i < 4
            val shown = if (showSorted) sorted else list
            mcNumber(
                rnd, "What is the median of ${shown.joinToString(", ")}?", null, med,
                if (showSorted) "The median is the middle number of the five." else "Put the numbers in order first. Then take the middle one.",
                if (showSorted) listOf(show("The middle one of the five is $med.", Board(listOf(line(sorted.joinToString("  ")), good("median = $med"))), Mood.CHEER))
                else listOf(
                    say("Put them in order: ${sorted.joinToString(", ")}.", Board(listOf(line(list.joinToString("  ")), dim("in order:"), line(sorted.joinToString("  "))))),
                    show("The middle one is $med.", Board(listOf(line(sorted.joinToString("  ")), good("median = $med"))), Mood.CHEER),
                ),
                spread = 3, min = 1, max = 20, extra = listOf(list[2], sorted.first(), sorted.last(), sorted[1], sorted[3]).filter { it != med },
            )
        }
        else -> {
            val list = (1..30).shuffled(rnd).take(5)
            val hi = list.max(); val lo = list.min()
            mcNumber(
                rnd, "What is the range of ${list.joinToString(", ")}?", null, hi - lo,
                "Range = biggest number − smallest number.",
                listOf(show("Biggest $hi, smallest $lo. $hi − $lo = ${hi - lo}.", Board(listOf(line(list.joinToString("  ")), line("biggest $hi, smallest $lo"), good("range = $hi − $lo = ${hi - lo}"))), Mood.CHEER)),
                spread = 3, min = 1, max = 40, extra = listOf(hi, lo, hi + lo, hi - lo + 1, hi - lo - 1).filter { it != hi - lo && it > 0 },
            )
        }
    }
}

val barGraph = lesson(
    "bar-graph", "Reading bar graphs", "The taller the bar, the more", "📈",
    teach = listOf(
        say("A bar graph shows numbers as bars. Here is what children chose as their favourite fruit.", Bars(listOf("Apple", "Mango", "Grapes"), listOf(6, 9, 4), unit = "kids")),
        say("The taller the bar, the bigger the number. Mango has the tallest bar, so mango is the favourite.", Bars(listOf("Apple", "Mango", "Grapes"), listOf(6, 9, 4), highlight = 1, unit = "kids"), mood = Mood.CHEER),
        show("Read the number above each bar. Apple 6, mango 9, grapes 4.", Bars(listOf("Apple", "Mango", "Grapes"), listOf(6, 9, 4), unit = "kids")),
        show("Mango got 9 and grapes got 4. How many more? 9 − 4 = 5 more children.", Board(listOf(line("mango 9, grapes 4"), good("9 − 4 = 5 more")))),
    ),
) { rnd, i ->
    val fruits = listOf("Apple", "Banana", "Mango", "Grapes", "Orange", "Cherry").shuffled(rnd).take(4)
    val values = (1..10).shuffled(rnd).take(4)
    val chart = Bars(fruits, values, unit = "kids")
    when (listOf(0, 1, 0, 1, 2, 3, 2, 3)[i.coerceIn(0, 7)]) {
        0 -> {
            val k = rnd.nextInt(4)
            mcNumber(
                rnd, "How many children chose ${fruits[k].lowercase()}?", chart, values[k],
                "Find the ${fruits[k].lowercase()} bar and read its number.",
                listOf(show("The ${fruits[k].lowercase()} bar shows ${values[k]}.", Bars(fruits, values, highlight = k, unit = "kids"), Mood.CHEER)),
                spread = 2, min = 1, max = 10, extra = values.filterIndexed { idx, _ -> idx != k },
            )
        }
        1 -> {
            val top = values.indexOf(values.max())
            mcText(
                rnd, "Which fruit is the most popular?", chart, fruits[top], fruits.filterIndexed { idx, _ -> idx != top },
                "Look for the tallest bar.",
                listOf(show("${fruits[top]} has the tallest bar, ${values[top]}. It is the most popular.", Bars(fruits, values, highlight = top, unit = "kids"), Mood.CHEER)),
            )
        }
        2 -> {
            val two = values.indices.shuffled(rnd).take(2)
            val hiIdx = two.maxBy { values[it] }
            val loIdx = two.first { it != hiIdx }
            val d = values[hiIdx] - values[loIdx]
            mcNumber(
                rnd, "How many more children chose ${fruits[hiIdx].lowercase()} than ${fruits[loIdx].lowercase()}?", chart, d,
                "Read both bars, then take the smaller number from the bigger.",
                listOf(show("${values[hiIdx]} − ${values[loIdx]} = $d more.", Board(listOf(line("${fruits[hiIdx].lowercase()}: ${values[hiIdx]}"), line("${fruits[loIdx].lowercase()}: ${values[loIdx]}"), good("${values[hiIdx]} − ${values[loIdx]} = $d"))), Mood.CHEER)),
                spread = 2, min = 1, max = 12, extra = listOf(values[hiIdx] + values[loIdx], values[hiIdx], values[loIdx]).filter { it != d },
            )
        }
        else -> {
            val total = values.sum()
            mcNumber(
                rnd, "How many children chose fruit altogether?", chart, total,
                "Read all the bars and add the numbers together.",
                listOf(show("${values.joinToString(" + ")} = $total children.", Board(listOf(line(values.joinToString(" + ")), good("= $total"))), Mood.CHEER)),
                spread = 4, min = 5, max = 40, extra = listOf(total - 1, total + 1, total - values.min(), total + values.max()).filter { it != total },
            )
        }
    }
}

// ---- Probability -----------------------------------------------------------------------------

private val chanceWords = listOf("certain", "likely", "unlikely", "impossible")

val probability = lesson(
    "probability", "Chance and probability", "How likely is it?", "🎲",
    teach = listOf(
        say("Probability tells us how likely something is. It can be certain, likely, unlikely, or impossible.", Board(listOf(line("certain: it will happen"), line("likely: it probably will"), line("unlikely: it probably won't"), line("impossible: it can't happen")))),
        say("Here is a bag with 3 red balls and 2 blue balls. You pick one without looking.", ballBag(3, 2)),
        say("There are 5 balls in all, and 3 of them are red. So the chance of red is 3 out of 5.", Board(listOf(line("red balls: 3"), line("all balls: 5"), good("chance of red = 3/5")))),
        show("Probability = the ones you want, divided by all the ones there are. It is never more than 1.", Board(listOf(good("probability = wanted ÷ total"), line("impossible = 0"), line("certain = 1"))), Mood.CHEER),
    ),
) { rnd, i ->
    when (listOf(0, 2, 0, 2, 1, 1, 2, 1)[i.coerceIn(0, 7)]) {
        0 -> {
            val a = rnd.between(1, 8); var b: Int
            do { b = rnd.between(1, 8) } while (b == a)
            val likely = if (a > b) "red" else "blue"
            mcText(
                rnd, "A bag has ${bagText(a, b)}. You pick one without looking. Which colour is more likely?", ballBag(a, b), likely,
                listOf(if (likely == "red") "blue" else "red", "they are equally likely"),
                "Which colour are there more of?",
                listOf(show("There are more ${likely} balls (${maxOf(a, b)} against ${minOf(a, b)}), so $likely is more likely.", ballBag(a, b), Mood.CHEER)),
            )
        }
        1 -> {
            var r: Int; var b: Int
            do { r = rnd.between(1, 7); b = rnd.between(1, 7) } while (r == b || gcd(r, r + b) != 1)
            val t = r + b
            mcText(
                rnd, "A bag has ${bagText(r, b)}. What is the chance of picking a red ball?", ballBag(r, b), "$r/$t",
                listOf("$b/$t", "$r/$b", "$r/${t + 1}", "${r + 1}/$t").filter { it != "$r/$t" }.shuffled(rnd).take(3),
                "The number of red balls goes on top. All the balls go on the bottom.",
                listOf(show("$r red out of $t balls in all. The chance is $r/$t.", Stack(ballBag(r, b), Board(listOf(good("$r red ÷ $t in all = $r/$t")))), Mood.CHEER)),
            )
        }
        else -> {
            val bags = listOf(6 to 0, 0 to 6, 8 to 1, 1 to 8, 9 to 1, 1 to 7, 7 to 1, 5 to 0)
            val (r, b) = rnd.choose(bags)
            val pickRed = rnd.nextBoolean()
            val wanted = if (pickRed) r else b
            val total = r + b
            val word = when {
                wanted == 0 -> "impossible"
                wanted == total -> "certain"
                wanted * 2 > total -> "likely"
                else -> "unlikely"
            }
            val colour = if (pickRed) "red" else "blue"
            mcText(
                rnd, "A bag has ${bagText(r, b)}. How likely is it that you pick a $colour ball?", ballBag(r, b), word, chanceWords.filter { it != word },
                "Are there none, a few, most, or all of the $colour balls?",
                listOf(show(
                    when (word) {
                        "impossible" -> "There are no $colour balls, so it is impossible."
                        "certain" -> "Every ball is $colour, so it is certain."
                        "likely" -> "Most of the balls are $colour, so it is likely."
                        else -> "Only a few balls are $colour, so it is unlikely."
                    },
                    ballBag(r, b), Mood.CHEER,
                )),
            )
        }
    }
}

val world9 = World(
    id = "w9", title = "Algebra & Data Planet", tagline = "Ratio, equations, averages and chance", emoji = "🌌",
    color = 0xFF0EA5E9, level = "Class 6 to 7",
    lessons = listOf(ratio, ratioShare, unitary, profitLoss, simpleInterest, variables, equations1, equations2, numberRules, meanLesson, medianMode, barGraph, probability),
)
