package com.gumthala.learningapp.tv.content

import kotlin.random.Random

/*
 * World 2 - Add & Take-away Forest. Joining and separating, first with pictures, then on a
 * number line, then by "making ten". Every generated question carries its own animated working.
 */

internal val kidNames = listOf("Asha", "Ravi", "Mira", "Sunil", "Priya", "Arjun", "Meena", "Raju", "Kavya", "Om", "Tara", "Dev")

/** a + b by counting on from the bigger number, hop by hop on a number line. */
internal fun countOnSolution(a: Int, b: Int): List<Step> {
    val big = maxOf(a, b)
    val small = minOf(a, b)
    val top = if (a + b <= 10) 10 else 20
    val hops = (1..small).map { big + it }
    return listOf(
        say("Start at the bigger number: $big. Then count on $small.", NumberLine(0, top, start = big, hops = hops), counting = true),
        show("We land on ${a + b}. So ${Words.number(a)} plus ${Words.number(b)} makes ${a + b}.", Stack(NumberLine(0, top, start = big, hops = hops), eq("$a", "+", "$b", "=", "${a + b}")), Mood.CHEER),
    )
}

/** a - b by counting back. */
internal fun countBackSolution(a: Int, b: Int): List<Step> {
    val top = if (a <= 10) 10 else 20
    val hops = (1..b).map { a - it }
    return listOf(
        say("Start at $a and count back $b.", NumberLine(0, top, start = a, hops = hops), counting = true),
        show("We land on ${a - b}. So ${Words.number(a)} minus ${Words.number(b)} leaves ${a - b}.", Stack(NumberLine(0, top, start = a, hops = hops), eq("$a", "−", "$b", "=", "${a - b}")), Mood.CHEER),
    )
}

private fun joinSolution(t: Thing, a: Int, b: Int): List<Step> = listOf(
    say("Put the two groups together and count them all.", Counters(t.emoji, a + b, CounterMode.COUNT, split = a, showTotal = true), counting = true),
    show("${Words.number(a)} plus ${Words.number(b)} makes ${a + b}.", Stack(Counters(t.emoji, a + b, CounterMode.STATIC, split = a), eq("$a", "+", "$b", "=", "${a + b}")), Mood.CHEER),
)

private fun takeAwaySolution(t: Thing, a: Int, b: Int): List<Step> = listOf(
    say("Start with $a. Cross out $b.", Counters(t.emoji, a, CounterMode.CROSS_OUT, crossed = b)),
    say("Now count what is left.", Counters(t.emoji, a - b, CounterMode.COUNT, showTotal = true), counting = true),
    show("${Words.number(a)} take away ${Words.number(b)} leaves ${a - b}.", eq("$a", "−", "$b", "=", "${a - b}"), Mood.CHEER),
)

// ---------------------------------------------------------------------------------------------

val addPictures = lesson(
    "add-pictures", "Adding means joining", "Put two groups together", "➕",
    teach = listOf(
        say("Adding means putting groups together.", Side(listOf(Counters("🍎", 2, CounterMode.STATIC), Counters("🍎", 3, CounterMode.STATIC)), listOf("+"))),
        say("Two apples and three more apples. Count them all.", Counters("🍎", 5, CounterMode.COUNT, split = 2, showTotal = true), counting = true),
        show("Two plus three makes five. The plus sign means and.", Stack(Counters("🍎", 5, CounterMode.STATIC, split = 2), eq("2", "+", "3", "=", "5"))),
        say("Let's try again. Four ducks and one more duck.", Counters("🐤", 5, CounterMode.COUNT, split = 4, showTotal = true), counting = true),
        show("Four plus one makes five. The equals sign means is the same as.", Stack(Counters("🐤", 5, CounterMode.STATIC, split = 4), eq("4", "+", "1", "=", "5")), Mood.CHEER),
    ),
) { rnd, i ->
    val t = Things.pickEasy(rnd)
    val limit = when (band(i)) { 0 -> 5; 1 -> 8; else -> 10 }
    val a = rnd.between(1, limit - 1)
    val b = rnd.between(1, limit - a)
    val names = kidNames.shuffled(rnd)
    mcNumber(
        rnd, "${names[0]} has ${t.count(a)}. ${names[1]} gives ${b} more. How many now?",
        Counters(t.emoji, a + b, CounterMode.STATIC, split = a, cols = if (a + b > 5) 5 else 0), a + b,
        "Count the first group, then keep counting the second group.",
        joinSolution(t, a, b), spread = 2, min = 1, max = 11,
    )
}

val add10 = lesson(
    "add-10", "Add up to 10", "Count on to find the total", "🔟",
    teach = listOf(
        say("Let's add without counting everything. We count on!", Equation("5", "+", "2", "=", "?")),
        say("Put five in your head. Now count on two more.", NumberLine(0, 10, start = 5, hops = listOf(6, 7)), counting = true),
        show("Five plus two makes seven.", Stack(NumberLine(0, 10, start = 5, hops = listOf(6, 7)), eq("5", "+", "2", "=", "7"))),
        say("What about three plus four? Start with the bigger number, four.", NumberLine(0, 10, start = 4, hops = listOf(5, 6, 7)), counting = true),
        show("Count on three: five, six, seven. Three plus four makes seven too!", eq("3", "+", "4", "=", "7"), Mood.CHEER),
        show("Adding in any order gives the same answer. 3 + 4 = 4 + 3.", Stack(eq("3", "+", "4", "=", "7"), eq("4", "+", "3", "=", "7"))),
    ),
) { rnd, i ->
    val limit = when (band(i)) { 0 -> 6; 1 -> 8; else -> 10 }
    val a = rnd.between(1, limit - 1)
    val b = rnd.between(1, limit - a)
    val ans = a + b
    mcNumber(
        rnd, "What is $a + $b?", Equation("$a", "+", "$b", "=", "?"), ans,
        "Start with the bigger number and count on the smaller one.",
        countOnSolution(a, b), spread = 2, min = 0, max = 11, extra = listOf(ans - 1, ans + 1, maxOf(a, b)),
    )
}

val addLine = lesson(
    "add-line", "Add on a number line", "Hop to the right to add", "🐸",
    teach = listOf(
        say("Meet the number line. Adding means hopping to the right.", NumberLine(0, 10, start = 3)),
        say("Three plus two. Start at three, hop two steps.", NumberLine(0, 10, start = 3, hops = listOf(4, 5)), counting = true),
        show("We landed on five. Three plus two makes five.", Stack(NumberLine(0, 10, start = 3, hops = listOf(4, 5)), eq("3", "+", "2", "=", "5"))),
        say("Two plus four. Start at two and hop four steps.", NumberLine(0, 10, start = 2, hops = listOf(3, 4, 5, 6)), counting = true),
        show("We landed on six. Two plus four makes six.", Stack(NumberLine(0, 10, start = 2, hops = listOf(3, 4, 5, 6)), eq("2", "+", "4", "=", "6")), Mood.CHEER),
    ),
) { rnd, i ->
    val limit = when (band(i)) { 0 -> 7; 1 -> 9; else -> 10 }
    val a = rnd.between(1, limit - 2)
    val b = rnd.between(2, minOf(5, limit - a))
    mcNumber(
        rnd, "Start at $a. Hop $b steps to the right. Where do you land?", NumberLine(0, 10, start = a), a + b,
        "Count each hop: one, two, three...",
        listOf(
            say("Start at $a and hop $b steps.", NumberLine(0, 10, start = a, hops = (1..b).map { a + it }), counting = true),
            show("We land on ${a + b}.", eq("$a", "+", "$b", "=", "${a + b}"), Mood.CHEER),
        ),
        spread = 2, min = 0, max = 10, extra = listOf(a + b - 1, a + b + 1, a),
    )
}

val doubles = lesson(
    "doubles", "Doubles", "The same number twice", "👯",
    teach = listOf(
        say("A double is a number added to itself.", Side(listOf(Counters("🐞", 2, CounterMode.STATIC), Counters("🐞", 2, CounterMode.STATIC)), listOf("+"))),
        show("Two plus two. Double two is four.", Stack(Side(listOf(Counters("🐞", 2, CounterMode.STATIC), Counters("🐞", 2, CounterMode.STATIC)), listOf("+")), eq("2", "+", "2", "=", "4"))),
        show("Double three is six. Three plus three.", Stack(Side(listOf(Counters("🐞", 3, CounterMode.STATIC), Counters("🐞", 3, CounterMode.STATIC)), listOf("+")), eq("3", "+", "3", "=", "6"))),
        show("Double five is ten. Five plus five.", Stack(Side(listOf(Counters("🐞", 5, CounterMode.STATIC, cols = 5), Counters("🐞", 5, CounterMode.STATIC, cols = 5)), listOf("+")), eq("5", "+", "5", "=", "10")), Mood.CHEER),
        say("Doubles jump up by two each time. Listen!", Strip(listOf("2", "4", "6", "8", "10", "12", "14", "16", "18", "20")), counting = true),
    ),
) { rnd, i ->
    val hi = when (band(i)) { 0 -> 5; 1 -> 8; else -> 10 }
    val n = rnd.between(1, hi)
    val t = Things.pickEasy(rnd)
    if (i % 2 == 0) {
        mcNumber(
            rnd, "What is double $n?", Side(listOf(Counters(t.emoji, n, CounterMode.STATIC, cols = 5), Counters(t.emoji, n, CounterMode.STATIC, cols = 5)), listOf("+")), n * 2,
            "A double means the same number twice: $n + $n.",
            listOf(
                say("Double $n means $n plus $n.", Side(listOf(Counters(t.emoji, n, CounterMode.COUNT, cols = 5), Counters(t.emoji, n, CounterMode.COUNT, cols = 5)), listOf("+")), counting = true),
                show("${Words.number(n)} plus ${Words.number(n)} makes ${n * 2}.", eq("$n", "+", "$n", "=", "${n * 2}"), Mood.CHEER),
            ),
            spread = 3, min = 1, max = 21, extra = listOf(n * 2 - 2, n * 2 + 2, n),
        )
    } else {
        mcNumber(
            rnd, "What is $n + $n?", Equation("$n", "+", "$n", "=", "?"), n * 2,
            "Adding a number to itself is a double.",
            countOnSolution(n, n), spread = 3, min = 1, max = 21, extra = listOf(n * 2 - 2, n * 2 + 2, n * 2 + 1),
        )
    }
}

val make10 = lesson(
    "make-10", "Make 10", "Number pairs that add to ten", "🔟",
    teach = listOf(
        say("Ten is a special number. A ten frame has ten boxes.", TenFrame(10), counting = true),
        say("Here are seven counters. How many more make ten?", TenFrame(7), mood = Mood.THINK),
        say("Fill the empty boxes: eight, nine, ten.", TenFrame(10), counting = true, from = 7),
        show("Three more! Seven and three make ten.", Stack(TenFrame(10), eq("7", "+", "3", "=", "10")), Mood.CHEER),
        say("These pairs all make ten. Learn them well!", Strip(listOf("1+9", "2+8", "3+7", "4+6", "5+5"), arrows = false)),
    ),
) { rnd, i ->
    val a = rnd.between(1, 9)
    val b = 10 - a
    if (band(i) < 2 || i % 2 == 0) {
        mcNumber(
            rnd, "$a and how many more make 10?", TenFrame(a), b,
            "Count the empty boxes in the ten frame.",
            listOf(
                say("Count on from $a up to ten.", TenFrame(10), counting = true, from = a),
                show("${Words.number(a)} and ${Words.number(b)} make ten.", Stack(TenFrame(10), eq("$a", "+", "$b", "=", "10")), Mood.CHEER),
            ),
            spread = 2, min = 1, max = 9, extra = listOf(b - 1, b + 1),
        )
    } else {
        mcNumber(
            rnd, "$a + ? = 10", Equation("$a", "+", "?", "=", "10"), b,
            "How many more to get to ten?",
            listOf(
                say("Count on from $a to ten.", NumberLine(0, 10, start = a, hops = (a + 1..10).toList()), counting = true),
                show("We hopped ${10 - a} steps. ${Words.number(a)} plus ${Words.number(b)} makes ten.", eq("$a", "+", "$b", "=", "10"), Mood.CHEER),
            ),
            spread = 2, min = 1, max = 9,
        )
    }
}

val takeAway = lesson(
    "take-away", "Taking away", "When some go away", "🎈",
    teach = listOf(
        say("Taking away means some go away. Look at five balloons.", Counters("🎈", 5, CounterMode.STATIC)),
        say("Two balloons float away. Cross them out.", Counters("🎈", 5, CounterMode.CROSS_OUT, crossed = 2)),
        say("How many are left? Count them.", Counters("🎈", 3, CounterMode.COUNT, showTotal = true), counting = true),
        show("Five take away two leaves three. The minus sign means take away.", eq("5", "−", "2", "=", "3"), Mood.CHEER),
        say("Another one. Four cookies, and Ravi eats one.", Counters("🍪", 4, CounterMode.CROSS_OUT, crossed = 1)),
        show("Four take away one leaves three.", eq("4", "−", "1", "=", "3")),
    ),
) { rnd, i ->
    val t = Things.pickEasy(rnd)
    val limit = when (band(i)) { 0 -> 5; 1 -> 8; else -> 10 }
    val a = rnd.between(2, limit)
    val b = rnd.between(1, a - 1)
    val names = kidNames.shuffled(rnd)
    val verb = if (t.emoji == "🍪" || t.emoji == "🍎") "eats" else "gives away"
    mcNumber(
        rnd, "${names[0]} has ${t.count(a)}. ${names[0]} $verb $b. How many are left?",
        Counters(t.emoji, a, CounterMode.STATIC, cols = if (a > 5) 5 else 0), a - b,
        "Take away $b from $a. Cross them out and count the rest.",
        takeAwaySolution(t, a, b), spread = 2, min = 0, max = 10, extra = listOf(a + b, a - b + 1, a - b - 1),
    )
}

val sub10 = lesson(
    "sub-10", "Subtract up to 10", "Count back to find what's left", "➖",
    teach = listOf(
        say("We can take away by counting back.", Equation("7", "−", "3", "=", "?")),
        say("Start at seven. Count back three.", NumberLine(0, 10, start = 7, hops = listOf(6, 5, 4)), counting = true),
        show("We land on four. Seven minus three leaves four.", Stack(NumberLine(0, 10, start = 7, hops = listOf(6, 5, 4)), eq("7", "−", "3", "=", "4")), Mood.CHEER),
        say("Nine minus two. Start at nine and count back two.", NumberLine(0, 10, start = 9, hops = listOf(8, 7)), counting = true),
        show("Nine minus two leaves seven.", eq("9", "−", "2", "=", "7")),
        show("If you take away zero, nothing changes. 6 − 0 = 6.", eq("6", "−", "0", "=", "6")),
    ),
) { rnd, i ->
    val limit = when (band(i)) { 0 -> 6; 1 -> 8; else -> 10 }
    val a = rnd.between(2, limit)
    val b = rnd.between(1, minOf(a, if (band(i) == 0) 3 else 5))
    mcNumber(
        rnd, "What is $a − $b?", Equation("$a", "−", "$b", "=", "?"), a - b,
        "Start at $a and count back $b.",
        countBackSolution(a, b), spread = 2, min = 0, max = 10, extra = listOf(a + b, a - b + 1, a - b - 1),
    )
}

val subLine = lesson(
    "sub-line", "Subtract on a number line", "Hop to the left to take away", "🐸",
    teach = listOf(
        say("Taking away means hopping to the left.", NumberLine(0, 10, start = 8)),
        say("Eight minus three. Start at eight, hop back three.", NumberLine(0, 10, start = 8, hops = listOf(7, 6, 5)), counting = true),
        show("We landed on five. Eight minus three leaves five.", Stack(NumberLine(0, 10, start = 8, hops = listOf(7, 6, 5)), eq("8", "−", "3", "=", "5"))),
        say("Ten minus six. Start at ten, hop back six.", NumberLine(0, 10, start = 10, hops = listOf(9, 8, 7, 6, 5, 4)), counting = true),
        show("We landed on four. Ten minus six leaves four.", eq("10", "−", "6", "=", "4"), Mood.CHEER),
    ),
) { rnd, i ->
    val a = rnd.between(when (band(i)) { 0 -> 4; else -> 6 }, 10)
    val b = rnd.between(2, minOf(6, a))
    mcNumber(
        rnd, "Start at $a. Hop $b steps to the left. Where do you land?", NumberLine(0, 10, start = a), a - b,
        "Count each hop back: one, two, three...",
        countBackSolution(a, b), spread = 2, min = 0, max = 10, extra = listOf(a - b - 1, a - b + 1, a + b.coerceAtMost(10 - a)),
    )
}

val factFamily = lesson(
    "fact-family", "Fact families", "Adding and taking away are friends", "👨‍👩‍👧",
    teach = listOf(
        say("Three numbers can make a whole family of sums. Meet 3, 4 and 7.", Strip(listOf("3", "4", "7"), arrows = false)),
        say("Two adding facts first.", Board(listOf(line("3 + 4 = 7"), line("4 + 3 = 7")))),
        say("Then two taking-away facts. Seven is the biggest, so it starts.", Board(listOf(line("3 + 4 = 7"), line("4 + 3 = 7"), hot("7 − 3 = 4"), hot("7 − 4 = 3")))),
        show("If you know one fact, you know all four!", Board(listOf(line("3 + 4 = 7"), line("4 + 3 = 7"), line("7 − 3 = 4"), line("7 − 4 = 3"))), Mood.CHEER),
    ),
) { rnd, i ->
    val hi = when (band(i)) { 0 -> 6; 1 -> 9; else -> 10 }
    val a = rnd.between(1, hi - 1)
    val b = rnd.between(1, hi - a)
    val c = a + b
    val family = Board(listOf(line("$a + $b = $c"), line("$b + $a = $c"), line("$c − $a = $b"), line("$c − $b = $a")))
    val solution = listOf(
        say("$a and $b make $c, so they are a family.", Strip(listOf("$a", "$b", "$c"), arrows = false)),
        show("Use the family to find the missing number.", family, Mood.CHEER),
    )
    when (i % 3) {
        0 -> mcNumber(rnd, "$a + $b = $c. So $c − $a = ?", Equation("$c", "−", "$a", "=", "?"), b, "Use the same three numbers: $a, $b and $c.", solution, spread = 2, min = 0, max = 10, extra = listOf(c, a))
        1 -> mcNumber(rnd, "$c − $b = $a. So $a + $b = ?", Equation("$a", "+", "$b", "=", "?"), c, "The same three numbers again: $a, $b and $c.", solution, spread = 2, min = 0, max = 11, extra = listOf(a, b))
        else -> mcNumber(rnd, "Which number is missing? $a + ? = $c", Equation("$a", "+", "?", "=", "$c"), b, "Try taking $a away from $c.", solution, spread = 2, min = 0, max = 10, extra = listOf(c, a))
    }
}

val add20 = lesson(
    "add-20", "Add up to 20", "Make ten first, then add the rest", "🧩",
    teach = listOf(
        say("Big sums are easy if we make ten first. Try 8 plus 5.", Equation("8", "+", "5", "=", "?")),
        say("Eight needs two more to make ten.", TenFrame(8, frames = 2), counting = true),
        say("Split the five into two and three. Two fills the frame.", Board(listOf(line("8 + 5"), dim("5 is 2 and 3"), line("8 + 2 + 3")))),
        say("Eight plus two is ten. Then ten plus three is thirteen.", Board(listOf(line("8 + 5"), dim("5 is 2 and 3"), line("8 + 2 + 3"), line("10 + 3"), good("= 13")))),
        show("Eight plus five makes thirteen!", Stack(TenFrame(13, frames = 2), eq("8", "+", "5", "=", "13")), Mood.CHEER),
    ),
) { rnd, i ->
    val a = rnd.between(when (band(i)) { 0 -> 7; else -> 6 }, 9)
    val need = 10 - a
    val b = rnd.between(need + 1, 9)   // always more than "need", so the sum really does cross ten
    val rest = b - need
    mcNumber(
        rnd, "What is $a + $b?", Equation("$a", "+", "$b", "=", "?"), a + b,
        "Make ten first: $a needs $need more to reach ten.",
        listOf(
            say("$a needs $need to make ten. Split $b into $need and $rest.", Board(listOf(line("$a + $b"), dim("$b is $need and $rest"), line("$a + $need + $rest")))),
            say("$a plus $need is ten. Ten plus $rest is ${a + b}.", Board(listOf(line("$a + $b"), dim("$b is $need and $rest"), line("$a + $need + $rest"), line("10 + $rest"), good("= ${a + b}")))),
        ),
        spread = 3, min = 10, max = 20, extra = listOf(a + b - 1, a + b + 1, a + b + 2),
    )
}

val sub20 = lesson(
    "sub-20", "Subtract up to 20", "Go back to ten, then the rest", "🧩",
    teach = listOf(
        say("For bigger take-aways, go back to ten first. Try 13 minus 5.", Equation("13", "−", "5", "=", "?")),
        say("Thirteen take away three is ten.", NumberLine(0, 20, start = 13, hops = listOf(12, 11, 10)), counting = true),
        say("We still need to take away two more. Five is three and two.", Board(listOf(line("13 − 5"), dim("5 is 3 and 2"), line("13 − 3 − 2")))),
        say("Ten take away two is eight.", Board(listOf(line("13 − 5"), dim("5 is 3 and 2"), line("13 − 3 − 2"), line("10 − 2"), good("= 8")))),
        show("Thirteen minus five leaves eight!", eq("13", "−", "5", "=", "8"), Mood.CHEER),
    ),
) { rnd, i ->
    val a = rnd.between(12, when (band(i)) { 0 -> 15; else -> 18 })
    val ones = a - 10
    val b = rnd.between(ones + 1, 9)
    val rest = b - ones
    mcNumber(
        rnd, "What is $a − $b?", Equation("$a", "−", "$b", "=", "?"), a - b,
        "Go back to ten first: $a minus $ones is ten.",
        listOf(
            say("$a minus $ones is ten. Split $b into $ones and $rest.", Board(listOf(line("$a − $b"), dim("$b is $ones and $rest"), line("$a − $ones − $rest")))),
            say("Ten minus $rest is ${a - b}.", Board(listOf(line("$a − $b"), dim("$b is $ones and $rest"), line("$a − $ones − $rest"), line("10 − $rest"), good("= ${a - b}")))),
        ),
        spread = 3, min = 0, max = 20, extra = listOf(a - b - 1, a - b + 1, a - b + 2),
    )
}

val storySums = lesson(
    "story-sums", "Story sums", "Is it adding or taking away?", "📖",
    teach = listOf(
        say("Some stories are adding stories. Some are taking-away stories.", BigText("+  or  −", emoji = "📖")),
        say("Mira has 4 flowers. Her friend gives her 3 more. More means add!", Counters("🌸", 7, CounterMode.COUNT, split = 4, showTotal = true), counting = true),
        show("Four plus three makes seven flowers.", eq("4", "+", "3", "=", "7")),
        say("Ravi has 8 sweets. He eats 3. Eats means take away!", Counters("🍬", 8, CounterMode.CROSS_OUT, crossed = 3)),
        show("Eight minus three leaves five sweets.", eq("8", "−", "3", "=", "5"), Mood.CHEER),
        show("Words like more, altogether and in all mean add. Words like left, ate and gave away mean take away.", Board(listOf(line("more, altogether, in all  →  +"), line("left, ate, gave away  →  −")))),
    ),
) { rnd, i ->
    val names = kidNames.shuffled(rnd)
    val t = Things.pickEasy(rnd)
    val limit = when (band(i)) { 0 -> 9; 1 -> 12; else -> 18 }
    val adding = rnd.nextBoolean()
    if (adding) {
        val a = rnd.between(2, limit - 2)
        val b = rnd.between(2, limit - a)
        mcNumber(
            rnd, "${names[0]} has ${t.count(a)}. ${names[1]} has ${b} more. How many altogether?", null, a + b,
            "\"Altogether\" means we add.",
            listOf(
                say("Altogether means add.", eq("$a", "+", "$b", "=", "?")),
                show("${Words.number(a)} plus ${Words.number(b)} makes ${a + b}.", eq("$a", "+", "$b", "=", "${a + b}"), Mood.CHEER),
            ),
            spread = 3, min = 0, max = 20, extra = listOf(a - b, a + b + 1, a + b - 1).filter { it >= 0 },
        )
    } else {
        val a = rnd.between(5, limit)
        val b = rnd.between(2, a - 1)
        mcNumber(
            rnd, "${names[0]} had ${t.count(a)}. ${names[0]} gave away $b. How many are left?", null, a - b,
            "\"Gave away\" and \"left\" mean we take away.",
            listOf(
                say("Gave away means take away.", eq("$a", "−", "$b", "=", "?")),
                show("${Words.number(a)} minus ${Words.number(b)} leaves ${a - b}.", eq("$a", "−", "$b", "=", "${a - b}"), Mood.CHEER),
            ),
            spread = 3, min = 0, max = 20, extra = listOf(a + b, a - b + 1, a - b - 1),
        )
    }
}

val world2 = World(
    id = "w2", title = "Add & Take-away Forest", tagline = "Join groups and take some away", emoji = "🌳",
    color = 0xFFFB923C, level = "Class 1",
    lessons = listOf(addPictures, add10, addLine, doubles, make10, takeAway, sub10, subLine, factFamily, add20, sub20, storySums),
)
