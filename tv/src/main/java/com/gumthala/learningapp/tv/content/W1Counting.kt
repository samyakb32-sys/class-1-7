package com.gumthala.learningapp.tv.content

import kotlin.random.Random

/*
 * World 1 - Counting Garden. Starts from literally nothing: what counting is.
 * Question visuals must never spoil the answer, so Counters in questions are always STATIC.
 */

private fun countSolution(t: Thing, n: Int, cols: Int = 0): List<Step> = listOf(
    say("Let's count them together.", Counters(t.emoji, n, CounterMode.COUNT, cols = cols, showTotal = true), counting = true),
    show(
        "The last number we said is $n. So there ${if (n == 1) "is" else "are"} $n ${t.name(n)}.",
        Stack(Counters(t.emoji, n, CounterMode.STATIC, cols = cols), BigText("$n", sub = Words.number(n))),
        Mood.CHEER,
    ),
)

private fun countCard(rnd: Random, t: Thing, n: Int, lo: Int, hi: Int, cols: Int = 0): Question =
    mcNumber(
        rnd, "How many ${t.many} are there?",
        Counters(t.emoji, n, CounterMode.STATIC, cols = cols),
        n,
        "Point at each one and count. Don't skip any, and don't count one twice.",
        countSolution(t, n, cols),
        spread = 2, min = lo, max = hi,
    )

val countTo5 = lesson(
    "count-1-5", "Count 1 to 5", "Counting things, one by one", "🍎",
    teach = listOf(
        say("Hello friend! I am Sunny. Let's learn to count!", BigText("1 2 3 4 5", emoji = "🌞"), mood = Mood.CHEER),
        say("Look, two balloons. We count them one by one.", Counters("🎈", 2), counting = true),
        say("Now three apples. Count with me!", Counters("🍎", 3, showTotal = true), counting = true),
        say("Four little ducks. Let's count them.", Counters("🐤", 4, showTotal = true), counting = true),
        say("Five stars! The last number we say tells us how many.", Counters("🌟", 5, showTotal = true), counting = true),
        say("Wonderful! Now you try.", BigText("Your turn!", emoji = "🌟"), mood = Mood.CHEER),
    ),
) { rnd, i ->
    val hi = when (band(i)) { 0 -> 3; 1 -> 4; else -> 5 }
    countCard(rnd, Things.pickEasy(rnd), rnd.between(1, hi), 1, 6)
}

val countTo10 = lesson(
    "count-6-10", "Count 6 to 10", "Bigger groups, up to ten", "🎈",
    teach = listOf(
        say("We can count more now. Watch the flowers.", Counters("🌸", 6, showTotal = true), counting = true),
        say("Seven butterflies. Count with me!", Counters("🦋", 7, showTotal = true), counting = true),
        say("Eight fish in the pond.", Counters("🐟", 8, showTotal = true), counting = true),
        say("Nine cookies. Yum!", Counters("🍪", 9, showTotal = true), counting = true),
        say("Ten balloons! Five and five make ten.", Counters("🎈", 10, showTotal = true), counting = true),
        say("Great counting! Let's practise.", BigText("6 7 8 9 10", emoji = "🎉"), mood = Mood.CHEER),
    ),
) { rnd, i ->
    val lo = when (band(i)) { 0 -> 6; 1 -> 6; else -> 7 }
    countCard(rnd, Things.pickEasy(rnd), rnd.between(lo, 10), 4, 11, cols = 5)
}

private fun numberIntro(n: Int): Step {
    val t = Things.easy[n % Things.easy.size]
    return say(
        "This is the number ${Words.number(n)}. ${Words.number(n).replaceFirstChar { it.uppercase() }} ${t.name(n)}.",
        Stack(BigText("$n", sub = Words.number(n)), Counters(t.emoji, n, CounterMode.STATIC, cols = if (n > 5) 5 else 0)),
    )
}

val knowNumbers = lesson(
    "know-numbers", "Know the numbers", "What each number looks like", "🔢",
    teach = listOf(
        say("Every number has a shape and a name. Let's meet them!", BigText("1 2 3 4 5 6 7 8 9 10", emoji = "🔢"), mood = Mood.CHEER),
    ) + (1..10).map { numberIntro(it) } + listOf(
        say("Now you know all the numbers to ten!", BigText("1 - 10", emoji = "🏆"), mood = Mood.CHEER),
    ),
) { rnd, i ->
    val n = rnd.between(1, 10)
    when (if (i < 3) 0 else if (i < 6) 1 else 2) {
        0 -> { // which numeral is this word/number of things
            val t = Things.pickEasy(rnd)
            val cols = if (n > 5) 5 else 0
            mcNumber(
                rnd, "How many ${t.many}? Pick the right number.",
                Counters(t.emoji, n, CounterMode.STATIC, cols = cols), n,
                "Count each one. The last number you say is the answer.",
                countSolution(t, n, cols), spread = 2, min = 1, max = 10,
            )
        }
        1 -> { // which group shows n ? (pictures as choices)
            val t = Things.pickEasy(rnd)
            val wrongs = wrongNumbers(n, rnd, 2, 2, 1, 10)
            mcQuestion(
                rnd, "Which group has ${Words.number(n)} ${t.many}?", BigText("$n", sub = Words.number(n)),
                Choice("$n", Counters(t.emoji, n, CounterMode.STATIC, cols = if (n > 5) 5 else 0)),
                wrongs.map { Choice("$it", Counters(t.emoji, it, CounterMode.STATIC, cols = if (it > 5) 5 else 0)) },
                "Count the ${t.many} in each group. Which group has $n?",
                listOf(
                    say("We are looking for $n.", BigText("$n", sub = Words.number(n), emoji = t.emoji)),
                    say("Count the right group.", Counters(t.emoji, n, CounterMode.COUNT, cols = if (n > 5) 5 else 0, showTotal = true), counting = true),
                ),
            )
        }
        else -> { // number word -> numeral
            mcNumber(
                rnd, "Which number says \"${Words.number(n)}\"?", BigText(Words.number(n), emoji = "🔤"), n,
                "Say the word slowly. Then count up to it: 1, 2, 3...",
                listOf(
                    say("The word is ${Words.number(n)}.", BigText(Words.number(n), emoji = "🔤")),
                    show("${Words.number(n).replaceFirstChar { it.uppercase() }} is written $n.", BigText("$n", sub = Words.number(n)), Mood.CHEER),
                ),
                spread = 2, min = 1, max = 10,
            )
        }
    }
}

val zeroLesson = lesson(
    "zero", "Zero means none", "What it means when nothing is left", "0️⃣",
    teach = listOf(
        say("Three fish are swimming in the pond.", Counters("🐟", 3, CounterMode.STATIC)),
        say("Oh! They swim away, one by one.", Counters("🐟", 3, CounterMode.CROSS_OUT, crossed = 3)),
        show("Now there are none. No fish at all.", Counters("🐟", 0, CounterMode.STATIC)),
        show("We write none as zero. Zero is a number too!", BigText("0", sub = "zero", emoji = "📦"), Mood.CHEER),
        say("An empty basket has zero apples. Zero means nothing is there.", Counters("🍎", 0, CounterMode.STATIC)),
    ),
) { rnd, i ->
    val t = Things.pickEasy(rnd)
    if (i == 4) {
        mcNumber(
            rnd, "Which number means none?", BigText("none", emoji = "📦"), 0,
            "Zero is the number for nothing at all.",
            listOf(show("When there is nothing, we say zero.", BigText("0", sub = "zero", emoji = "📦"), Mood.CHEER)),
            spread = 2, min = 0, max = 4,
        )
    } else {
        val n = when (band(i)) { 0 -> rnd.between(0, 2); 1 -> rnd.between(0, 4); else -> rnd.between(0, 5) }
        mcNumber(
            rnd, "How many ${t.many} are there?", Counters(t.emoji, n, CounterMode.STATIC), n,
            "If you can't see any, the answer is zero.",
            if (n == 0) listOf(show("There is nothing in the box. That is zero.", BigText("0", sub = "zero", emoji = "📦"), Mood.CHEER))
            else countSolution(t, n),
            spread = 2, min = 0, max = 5,
        )
    }
}

private fun moreLessSolution(a: Thing, na: Int, b: Thing, nb: Int, askMore: Boolean): List<Step> {
    val ans = if (askMore) (if (na > nb) a else b) else (if (na < nb) a else b)
    val word = if (askMore) "more" else "less"
    return listOf(
        say(
            "Count each group. ${a.many.replaceFirstChar { it.uppercase() }} first, then ${b.many}.",
            Side(listOf(Counters(a.emoji, na, CounterMode.COUNT, showTotal = true), Counters(b.emoji, nb, CounterMode.COUNT, showTotal = true))),
            counting = true,
        ),
        show(
            "$na and $nb. ${ans.many.replaceFirstChar { it.uppercase() }} have $word.",
            Stack(eq("$na", if (na < nb) "<" else ">", "$nb"), BigText(ans.emoji)),
            Mood.CHEER,
        ),
    )
}

val moreLess = lesson(
    "more-less", "More, less, same", "Comparing two groups", "⚖️",
    teach = listOf(
        say("Let's look at two groups. Who has more?", Side(listOf(Counters("🍎", 5, CounterMode.STATIC), Counters("🍌", 3, CounterMode.STATIC)))),
        say("Count them. Five apples and three bananas.", Side(listOf(Counters("🍎", 5, CounterMode.COUNT), Counters("🍌", 3, CounterMode.COUNT))), counting = true),
        show("Five is more than three. The apples have more!", Stack(eq("5", ">", "3"), BigText("🍎")), Mood.CHEER),
        show("Three is less than five. The bananas have less.", Stack(eq("3", "<", "5"), BigText("🍌"))),
        show("If both groups are equal, we say they are the same.", Side(listOf(Counters("🐟", 4, CounterMode.STATIC), Counters("🐦", 4, CounterMode.STATIC)), listOf("="))),
    ),
) { rnd, i ->
    val a = Things.pickEasy(rnd)
    var b = Things.pickEasy(rnd)
    while (b == a) b = Things.pickEasy(rnd)
    val hi = if (band(i) == 0) 5 else 8
    val na = rnd.between(1, hi)
    val same = band(i) == 2 && rnd.nextInt(4) == 0
    var nb = if (same) na else rnd.between(1, hi)
    if (!same && nb == na) nb = if (na < hi) na + 1 else na - 1
    val askMore = band(i) == 0 || rnd.nextBoolean()
    val visual = Side(listOf(Counters(a.emoji, na, CounterMode.STATIC, cols = 4), Counters(b.emoji, nb, CounterMode.STATIC, cols = 4)))
    val words = if (askMore) "MORE" else "LESS"
    if (na == nb) {
        mcQuestion(
            rnd, "Which group has $words? Or are they the same?", visual,
            Choice("Same"), listOf(Choice(a.emoji), Choice(b.emoji)),
            "Count both groups. If the numbers match, they are the same.",
            listOf(
                say("Count both groups.", Side(listOf(Counters(a.emoji, na, CounterMode.COUNT, showTotal = true), Counters(b.emoji, nb, CounterMode.COUNT, showTotal = true))), counting = true),
                show("$na and $nb are the same number!", eq("$na", "=", "$nb"), Mood.CHEER),
            ),
        )
    } else {
        val answer = if (askMore) (if (na > nb) a else b) else (if (na < nb) a else b)
        val other = if (answer == a) b else a
        mcQuestion(
            rnd, "Which group has $words? Or are they the same?", visual,
            Choice(answer.emoji), listOf(Choice(other.emoji), Choice("Same")),
            "Count both groups. Which number is ${if (askMore) "bigger" else "smaller"}?",
            moreLessSolution(a, na, b, nb, askMore),
        )
    }
}

val countTo20 = lesson(
    "count-11-20", "Count 11 to 20", "Ten and some more", "🔟",
    teach = listOf(
        say("A ten frame holds ten. Let's fill it!", TenFrame(10), counting = true),
        say("Ten and one more makes eleven.", TenFrame(11, frames = 2), counting = true),
        say("Ten and two more makes twelve.", TenFrame(12, frames = 2), counting = true),
        say("Fifteen is ten and five more.", TenFrame(15, frames = 2), counting = true),
        say("Keep going. Nineteen is ten and nine more.", TenFrame(19, frames = 2), counting = true),
        say("Two full ten frames make twenty!", TenFrame(20, frames = 2), counting = true, mood = Mood.CHEER),
    ),
) { rnd, i ->
    val lo = when (band(i)) { 0 -> 11; 1 -> 11; else -> 13 }
    val n = rnd.between(lo, 20)
    mcNumber(
        rnd, "How many counters are there?", TenFrame(n, frames = 2), n,
        "One frame is full: that is ten. Count the extra ones on after ten.",
        listOf(
            say("The first frame is full. That is ten.", TenFrame(10, frames = 2), counting = true),
            say("Now count on from ten.", TenFrame(n, frames = 2), counting = true, from = 10),
            show("So there are $n.", Stack(TenFrame(n, frames = 2), BigText("$n", sub = Words.number(n))), Mood.CHEER),
        ),
        spread = 3, min = 10, max = 20,
    )
}

val whatComesNext = lesson(
    "count-on", "What comes next?", "Before, after and in between", "➡️",
    teach = listOf(
        say("Numbers go in order, like steps on a stair.", Strip(listOf("1", "2", "3", "4", "5", "6"))),
        say("After four comes five. Five is one more.", Strip(listOf("3", "4", "5"), arrows = true), from = 0),
        say("Before six comes five. Five is one less.", Strip(listOf("5", "6"), arrows = true)),
        say("Between three and five is four. It is in the middle!", Strip(listOf("3", "4", "5"), arrows = true)),
        say("Let's find the missing number.", Strip(listOf("7", "8", "?", "10"), blankAt = 2), mood = Mood.THINK),
    ),
) { rnd, i ->
    val hi = when (band(i)) { 0 -> 10; 1 -> 15; else -> 20 }
    val kind = if (band(i) == 0) 0 else rnd.nextInt(3)
    when (kind) {
        0 -> { // what comes after
            val n = rnd.between(1, hi - 1)
            mcNumber(
                rnd, "What comes after $n?", Strip(listOf("$n", "?"), blankAt = 1), n + 1,
                "Count on one more from $n.",
                listOf(
                    say("Start at $n and count one more.", Strip(listOf("$n", "?"), blankAt = 1)),
                    show("After $n comes ${n + 1}.", Strip(listOf("$n", "${n + 1}")), Mood.CHEER),
                ),
                spread = 2, min = 1, max = hi + 1,
            )
        }
        1 -> { // what comes before
            val n = rnd.between(2, hi)
            mcNumber(
                rnd, "What comes before $n?", Strip(listOf("?", "$n"), blankAt = 0), n - 1,
                "Count back one from $n.",
                listOf(
                    say("Start at $n and go back one.", Strip(listOf("?", "$n"), blankAt = 0)),
                    show("Before $n comes ${n - 1}.", Strip(listOf("${n - 1}", "$n")), Mood.CHEER),
                ),
                spread = 2, min = 0, max = hi,
            )
        }
        else -> { // between
            val n = rnd.between(1, hi - 2)
            mcNumber(
                rnd, "Which number is between $n and ${n + 2}?", Strip(listOf("$n", "?", "${n + 2}"), blankAt = 1), n + 1,
                "It is one more than $n and one less than ${n + 2}.",
                listOf(
                    say("Count from $n to ${n + 2}.", Strip(listOf("$n", "?", "${n + 2}"), blankAt = 1)),
                    show("$n, ${n + 1}, ${n + 2}. The middle one is ${n + 1}.", Strip(listOf("$n", "${n + 1}", "${n + 2}")), Mood.CHEER),
                ),
                spread = 2, min = 0, max = hi + 2,
            )
        }
    }
}

val biggerSmaller = lesson(
    "bigger-smaller", "Bigger or smaller?", "Which number is more?", "📏",
    teach = listOf(
        say("Which number is bigger, three or five?", Side(listOf(
            Stack(BigText("3"), Counters("🍎", 3, CounterMode.STATIC)),
            Stack(BigText("5"), Counters("🍎", 5, CounterMode.STATIC)),
        )), mood = Mood.THINK),
        say("Five has more apples. So five is bigger.", Side(listOf(
            Stack(BigText("3"), Counters("🍎", 3, CounterMode.STATIC)),
            Stack(BigText("5"), Counters("🍎", 5, CounterMode.STATIC)),
        ), listOf("<")), from = Step.ALL, mood = Mood.CHEER),
        say("On a number line, bigger numbers are further to the right.", NumberLine(0, 10, start = 3, hops = listOf(5))),
        say("Start at three. Hop right to reach five. Five is bigger!", NumberLine(0, 10, start = 3, hops = listOf(4, 5))),
        say("Seven or four? Seven is further right, so seven is bigger.", NumberLine(0, 10, start = 4, hops = listOf(5, 6, 7))),
    ),
) { rnd, i ->
    val hi = when (band(i)) { 0 -> 10; 1 -> 15; else -> 20 }
    val askBig = rnd.nextBoolean()
    val choicesN = if (band(i) == 0) 2 else 3
    val nums = LinkedHashSet<Int>()
    while (nums.size < choicesN) nums.add(rnd.between(1, hi))
    val list = nums.toList()
    val ans = if (askBig) list.max() else list.min()
    val lo = list.min()
    val top = list.max()
    val word = if (askBig) "BIGGEST" else "SMALLEST"
    val q = mcQuestion(
        rnd, if (choicesN == 2) "Which number is ${if (askBig) "BIGGER" else "SMALLER"}?" else "Which number is the $word?",
        if (hi <= 10 && choicesN == 2) Side(list.map { Counters("🍎", it, CounterMode.STATIC, cols = 5) }, listOf("or")) else null,
        Choice("$ans"), list.filter { it != ans }.map { Choice("$it") },
        if (askBig) "The bigger number is further to the right on the number line." else "The smaller number is further to the left on the number line.",
        listOf(
            say("Find them on the number line.", NumberLine(0, hi, start = lo, hops = listOf(top), labelEvery = if (hi > 10) 5 else 1)),
            show(
                "${if (askBig) top else lo} is ${if (askBig) "further right, so it is bigger" else "further left, so it is smaller"}.",
                Stack(eq("$lo", "<", "$top"), BigText("$ans")), Mood.CHEER,
            ),
        ),
    )
    q
}

val orderNumbers = lesson(
    "order-numbers", "Put in order", "Smallest to biggest", "🪜",
    teach = listOf(
        say("Let's put numbers in order, from smallest to biggest.", Strip(listOf("5", "2", "8")), mood = Mood.THINK),
        say("Find the smallest first. It is two.", Strip(listOf("2", "?", "?"), blankAt = -1, arrows = false)),
        say("Next comes five.", Strip(listOf("2", "5", "?"), arrows = false)),
        say("The biggest is last: eight.", Strip(listOf("2", "5", "8"), arrows = true), mood = Mood.CHEER),
        say("Smallest to biggest: two, five, eight.", Strip(listOf("2", "5", "8"), arrows = true)),
    ),
) { rnd, i ->
    val hi = when (band(i)) { 0 -> 10; 1 -> 15; else -> 20 }
    val nums = LinkedHashSet<Int>()
    while (nums.size < 3) nums.add(rnd.between(1, hi))
    val sorted = nums.sorted()
    val correct = sorted.joinToString(", ")
    val wrong = LinkedHashSet<String>()
    wrong.add(sorted.reversed().joinToString(", "))
    val perms = listOf(
        listOf(sorted[1], sorted[0], sorted[2]), listOf(sorted[0], sorted[2], sorted[1]),
        listOf(sorted[1], sorted[2], sorted[0]), listOf(sorted[2], sorted[0], sorted[1]),
    )
    for (p in perms.shuffled(rnd)) { if (wrong.size >= 3) break; wrong.add(p.joinToString(", ")) }
    mcText(
        rnd, "Which row goes from smallest to biggest?", Strip(nums.map { "$it" }, arrows = false), correct, wrong.toList(),
        "Find the smallest number first. Then the next one. The biggest goes last.",
        listOf(
            say("Find the smallest: ${sorted[0]}.", Strip(listOf("${sorted[0]}", "?", "?"), arrows = false)),
            say("Then ${sorted[1]}.", Strip(listOf("${sorted[0]}", "${sorted[1]}", "?"), arrows = false)),
            show("The biggest is ${sorted[2]}. So: $correct.", Strip(sorted.map { "$it" }, arrows = true), Mood.CHEER),
        ),
    )
}

val ordinals = lesson(
    "ordinals", "1st, 2nd, 3rd", "Who is first in the line?", "🏁",
    teach = listOf(
        say("Five friends are in a race. Who won?", Strip(listOf("🐶", "🐱", "🐰", "🐸", "🐻"), arrows = false, note = null)),
        say("The puppy is first. The kitten is second.", Strip(listOf("🐶", "🐱", "🐰", "🐸", "🐻"), arrows = false), from = Step.ALL),
        say("The bunny is third. The frog is fourth. The bear is fifth.", Strip(listOf("🐶", "🐱", "🐰", "🐸", "🐻"), arrows = false), from = Step.ALL),
        show("First, second, third, fourth, fifth. These are called ordinal numbers.", BigText("1st  2nd  3rd  4th  5th", emoji = "🏁"), Mood.CHEER),
    ),
) { rnd, i ->
    val pool = listOf("🐶", "🐱", "🐰", "🐸", "🐻", "🐼", "🐷", "🐵", "🐮", "🐯")
    val len = if (band(i) == 0) 4 else 5
    val row = pool.shuffled(rnd).take(len)
    val pos = rnd.between(1, len)
    val ans = row[pos - 1]
    mcText(
        rnd, "Who is ${Words.ordinal(pos)} from the left?", Strip(row, arrows = false), ans,
        row.filter { it != ans }.shuffled(rnd).take(3),
        "Start at the left. Say first, second, third... until you reach ${Words.ordinal(pos)}.",
        listOf(
            say("Start from the left and count: ${(1..pos).joinToString(", ") { Words.ordinal(it) }}.", Strip(row.take(pos), arrows = false), counting = false),
            show("The ${Words.ordinal(pos)} one is $ans.", Strip(row, arrows = false), Mood.CHEER),
        ),
    )
}

val world1 = World(
    id = "w1", title = "Counting Garden", tagline = "Start here! Learn to count", emoji = "🌻",
    color = 0xFF22C55E, level = "Start here",
    lessons = listOf(countTo5, countTo10, knowNumbers, zeroLesson, moreLess, countTo20, whatComesNext, biggerSmaller, orderNumbers, ordinals),
)
