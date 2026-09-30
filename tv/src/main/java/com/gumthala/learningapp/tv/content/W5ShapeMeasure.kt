package com.gumthala.learningapp.tv.content

import kotlin.random.Random

/*
 * World 5 - Shape & Measure Bay. Flat and solid shapes, patterns, length, telling the time,
 * Indian money, units, and the calendar.
 */

private val shapeNames = mapOf(
    ShapeKind.CIRCLE to "circle", ShapeKind.OVAL to "oval", ShapeKind.SQUARE to "square", ShapeKind.RECTANGLE to "rectangle",
    ShapeKind.TRIANGLE to "triangle", ShapeKind.PENTAGON to "pentagon", ShapeKind.HEXAGON to "hexagon", ShapeKind.OCTAGON to "octagon",
)

private fun solidName(k: SolidKind) = k.name.lowercase()

val shapes2d = lesson(
    "shapes-2d", "Flat shapes", "Circles, squares, triangles...", "🔷",
    teach = listOf(
        say("Shapes are all around us. This round shape is a circle.", Shape(ShapeKind.CIRCLE, "circle")),
        say("Wheels and clocks are circles. It has no corners and no straight sides.", Shape(ShapeKind.CIRCLE, "circle")),
        say("This shape has four equal sides. It is a square.", Shape(ShapeKind.SQUARE, "square", markSides = true)),
        say("This one has four sides too, but two are longer. It is a rectangle.", Shape(ShapeKind.RECTANGLE, "rectangle", markSides = true)),
        say("Three sides and three corners make a triangle.", Shape(ShapeKind.TRIANGLE, "triangle", markSides = true)),
        show("Now you know four shapes: circle, square, rectangle and triangle.", Side(listOf(Shape(ShapeKind.CIRCLE), Shape(ShapeKind.SQUARE), Shape(ShapeKind.RECTANGLE), Shape(ShapeKind.TRIANGLE))), Mood.CHEER),
    ),
) { rnd, i ->
    val pool = when (band(i)) {
        0 -> listOf(ShapeKind.CIRCLE, ShapeKind.SQUARE, ShapeKind.TRIANGLE, ShapeKind.RECTANGLE)
        1 -> listOf(ShapeKind.CIRCLE, ShapeKind.SQUARE, ShapeKind.TRIANGLE, ShapeKind.RECTANGLE, ShapeKind.PENTAGON, ShapeKind.HEXAGON)
        else -> shapeNames.keys.toList()
    }
    val k = rnd.choose(pool)
    val wrongs = pool.filter { it != k }.shuffled(rnd).take(3).map { shapeNames.getValue(it) }
    mcText(
        rnd, "What shape is this?", Shape(k), shapeNames.getValue(k), wrongs,
        "Count the sides and corners.",
        listOf(
            say("Count the sides.", Shape(k, markSides = true)),
            show("It has ${if (k.sides == 0) "no" else "${k.sides}"} straight sides. It is a ${shapeNames.getValue(k)}.", Shape(k, shapeNames.getValue(k)), Mood.CHEER),
        ),
    )
}

val sidesCorners = lesson(
    "sides-corners", "Sides and corners", "Counting the edges of a shape", "📐",
    teach = listOf(
        say("A side is a straight edge. Let's count the sides of this triangle.", Shape(ShapeKind.TRIANGLE, markSides = true), counting = true),
        say("A corner is where two sides meet. Count the corners.", Shape(ShapeKind.TRIANGLE, markCorners = true), counting = true, from = 0),
        say("A square has four sides and four corners.", Shape(ShapeKind.SQUARE, markSides = true), counting = true),
        say("Five sides make a pentagon. Six sides make a hexagon.", Side(listOf(Shape(ShapeKind.PENTAGON, "5 sides"), Shape(ShapeKind.HEXAGON, "6 sides")))),
        show("A shape always has the same number of sides as corners.", Board(listOf(good("triangle: 3 sides, 3 corners"), good("square: 4 sides, 4 corners"), good("pentagon: 5 sides, 5 corners"))), Mood.CHEER),
    ),
) { rnd, i ->
    val pool = listOf(ShapeKind.TRIANGLE, ShapeKind.SQUARE, ShapeKind.RECTANGLE, ShapeKind.PENTAGON, ShapeKind.HEXAGON, ShapeKind.OCTAGON).let { if (band(i) == 0) it.take(3) else it }
    val k = rnd.choose(pool)
    val corners = rnd.nextBoolean()
    mcNumber(
        rnd, "How many ${if (corners) "corners" else "sides"} does this shape have?", Shape(k), k.sides,
        if (corners) "A corner is where two sides meet. Count them." else "Count the straight edges, one by one.",
        listOf(
            say("Count the ${if (corners) "corners" else "sides"}.", Shape(k, markSides = !corners, markCorners = corners), counting = true),
            show("There are ${k.sides}.", Shape(k, "${k.sides}"), Mood.CHEER),
        ),
        spread = 2, min = 3, max = 9, extra = listOf(k.sides - 1, k.sides + 1, k.sides + 2),
    )
}

val shapes3d = lesson(
    "shapes-3d", "Solid shapes", "Shapes you can hold", "🏺",
    teach = listOf(
        say("Some shapes are solid. You can pick them up. This is a cube, like a dice.", Solid(SolidKind.CUBE, "cube")),
        say("A sphere is round like a ball.", Solid(SolidKind.SPHERE, "sphere")),
        say("A cylinder is like a tin can.", Solid(SolidKind.CYLINDER, "cylinder")),
        say("A cone is like an ice-cream cone.", Solid(SolidKind.CONE, "cone")),
        say("A cuboid is like a box, and a pyramid has a pointed top.", Side(listOf(Solid(SolidKind.CUBOID, "cuboid"), Solid(SolidKind.PYRAMID, "pyramid")))),
    ),
) { rnd, i ->
    val kinds = SolidKind.values().toList()
    val k = rnd.choose(kinds)
    val object_ = mapOf(
        SolidKind.CUBE to "a dice", SolidKind.CUBOID to "a matchbox", SolidKind.SPHERE to "a ball", SolidKind.CYLINDER to "a tin can",
        SolidKind.CONE to "an ice-cream cone", SolidKind.PYRAMID to "a tent roof",
    )
    if (i % 2 == 0) {
        mcText(
            rnd, "What solid shape is this?", Solid(k), solidName(k), kinds.filter { it != k }.shuffled(rnd).take(3).map { solidName(it) },
            "Think of something at home with this shape.",
            listOf(show("This is a ${solidName(k)}. It is shaped like ${object_.getValue(k)}.", Solid(k, solidName(k)), Mood.CHEER)),
        )
    } else {
        mcText(
            rnd, "Which solid shape is like ${object_.getValue(k)}?", null, solidName(k), kinds.filter { it != k }.shuffled(rnd).take(3).map { solidName(it) },
            "Picture ${object_.getValue(k)} in your head.",
            listOf(show("${object_.getValue(k).replaceFirstChar { it.uppercase() }} is a ${solidName(k)}.", Solid(k, solidName(k)), Mood.CHEER)),
        )
    }
}

private val patternSets = listOf(
    listOf("🍎", "🍌"), listOf("🐶", "🐱"), listOf("⭐", "🌙"), listOf("🔴", "🔵"), listOf("🌸", "🍀"), listOf("🚗", "🚲"),
)

val patterns = lesson(
    "patterns", "Patterns", "What comes next?", "🎨",
    teach = listOf(
        say("A pattern repeats again and again. Apple, banana, apple, banana...", Strip(listOf("🍎", "🍌", "🍎", "🍌", "🍎", "🍌"), arrows = false)),
        say("What comes next in this pattern? Think!", Strip(listOf("🍎", "🍌", "🍎", "🍌", "🍎", "?"), blankAt = 5, arrows = false), mood = Mood.THINK),
        show("A banana! The pattern is apple, banana, apple, banana.", Strip(listOf("🍎", "🍌", "🍎", "🍌", "🍎", "🍌"), arrows = false), Mood.CHEER),
        say("Patterns can be longer. Dog, cat, cat, dog, cat, cat...", Strip(listOf("🐶", "🐱", "🐱", "🐶", "🐱", "🐱"), arrows = false)),
        say("Number patterns work too. Add two each time.", Strip(listOf("2", "4", "6", "8", "?"), blankAt = 4), counting = true),
    ),
) { rnd, i ->
    when {
        band(i) == 0 || i == 3 -> {
            val (a, b) = rnd.choose(patternSets)
            val seq = List(6) { if (it % 2 == 0) a else b }
            val ans = seq.last()
            val shown = seq.dropLast(1) + "?"
            mcText(rnd, "What comes next?", Strip(shown, blankAt = 5, arrows = false), ans, listOf(if (ans == a) b else a, "🎈", "🚀").filter { it != ans }, "The two pictures take turns. Which one came just before the last one?", listOf(show("The pattern repeats, so the next one is this.", Strip(seq, arrows = false), Mood.CHEER)))
        }
        band(i) == 1 -> {
            val (a, b) = rnd.choose(patternSets)
            val unit = listOf(a, b, b)
            val seq = List(7) { unit[it % 3] }
            val ans = seq.last()
            mcText(rnd, "What comes next?", Strip(seq.dropLast(1) + "?", blankAt = 6, arrows = false), ans, listOf(a, b, "🎈").filter { it != ans }.take(2), "Find the part that repeats: ${a} ${b} ${b}.", listOf(show("The part $a $b $b repeats. So next is this.", Strip(seq, arrows = false), Mood.CHEER)))
        }
        else -> {
            val step = rnd.choose(listOf(2, 3, 4, 5, 10))
            val start = rnd.between(1, 10)
            val items = (0 until 5).map { start + it * step }
            mcNumber(rnd, "What number comes next?", Strip(items.dropLast(1).map { "$it" } + "?", blankAt = 4), items.last(), "Find how much it goes up each time: $step.", listOf(say("It goes up by $step each time.", Strip(items.map { "$it" }), counting = true)), spread = step, min = 0, max = 60, extra = listOf(items.last() + 1, items.last() - 1, items.last() + step))
        }
    }
}

val length = lesson(
    "length", "Measuring length", "Using a ruler in centimetres", "📏",
    teach = listOf(
        say("We measure length with a ruler. Line up one end of the pencil with zero.", Ruler(7)),
        say("Now read the number where the pencil ends. It ends at seven.", Ruler(7)),
        show("The pencil is seven centimetres long. We write 7 cm.", Stack(Ruler(7), BigText("7 cm")), Mood.CHEER),
        show("Short things use centimetres. Long things, like a room, use metres. One metre is one hundred centimetres.", Board(listOf(line("small things → cm"), line("long things → m"), good("1 m = 100 cm")))),
    ),
) { rnd, i ->
    val cm = rnd.between(2, if (band(i) == 0) 8 else 11)
    val things = listOf("✏️" to "pencil", "🖍️" to "crayon", "📎" to "paper clip", "🔧" to "spanner")
    val (emoji, name) = rnd.choose(things)
    mcNumber(
        rnd, "How long is the $name?", Ruler(cm, emoji), cm,
        "Start at zero. Read the number at the other end.",
        listOf(show("The $name ends at $cm, so it is $cm centimetres long.", Stack(Ruler(cm, emoji), BigText("$cm cm")), Mood.CHEER)),
        spread = 2, min = 1, max = 12, extra = listOf(cm - 1, cm + 1),
    ) { "$it cm" }
}

private fun clockChoices(rnd: Random, h: Int, m: Int, kinds: List<Pair<Int, Int>>): Pair<String, List<String>> {
    fun fmt(hh: Int, mm: Int) = "${if (hh == 0) 12 else hh}:${mm.toString().padStart(2, '0')}"
    val ans = fmt(h, m)
    val wrongs = LinkedHashSet<String>()
    for ((hh, mm) in kinds.shuffled(rnd)) wrongs.add(fmt(hh, mm))
    wrongs.remove(ans)
    return ans to wrongs.toList().take(3)
}

val timeHour = lesson(
    "time-hour", "Telling the time: o'clock", "Hour hand and minute hand", "🕐",
    teach = listOf(
        say("A clock has two hands. The short hand shows the hour.", Clock(3, 0, digital = false)),
        say("The long hand shows the minutes. When it points to twelve, it is an o'clock time.", Clock(3, 0, digital = false)),
        show("The short hand is on three. So it is three o'clock.", Clock(3, 0), Mood.CHEER),
        show("Seven o'clock: the long hand is on twelve and the short hand is on seven.", Clock(7, 0)),
    ),
) { rnd, i ->
    val h = rnd.between(1, 12)
    val kinds = listOf((h % 12) + 1 to 0, ((h + 10) % 12) + 1 to 0, ((h + 5) % 12) + 1 to 0, h to 30, ((h + 1) % 12) + 1 to 0)
    val (ans, wrongs) = clockChoices(rnd, h, 0, kinds.map { (a, b) -> a to b })
    mcText(
        rnd, "What time does the clock show?", Clock(h, 0, digital = false), ans, wrongs,
        "The long hand is on 12. Which number is the short hand on?",
        listOf(say("The long hand is on twelve. The short hand is on $h.", Clock(h, 0, digital = true)), show("It is $h o'clock.", Clock(h, 0), Mood.CHEER)),
    )
}

val timeHalf = lesson(
    "time-half", "Half past and quarter", "Half, quarter past, quarter to", "🕧",
    teach = listOf(
        say("When the long hand points to six, half the hour has gone. That is half past.", Clock(4, 30)),
        say("Half past four is written 4:30.", Clock(4, 30)),
        say("When the long hand is on three, a quarter of the hour has gone. Quarter past!", Clock(4, 15)),
        say("When the long hand is on nine, it is a quarter to the next hour.", Clock(4, 45)),
        show("Thirty minutes is half past, fifteen is quarter past, forty-five is quarter to.", Board(listOf(line(":15  quarter past"), line(":30  half past"), line(":45  quarter to"))), Mood.CHEER),
    ),
) { rnd, i ->
    val h = rnd.between(1, 12)
    val m = rnd.choose(if (band(i) == 0) listOf(30) else listOf(15, 30, 45))
    val hours = listOf(h, h % 12 + 1)
    val kinds = listOf(h to 15, h to 30, h to 45, h % 12 + 1 to 30, h % 12 + 1 to 15, (h + 10) % 12 + 1 to 30)
    val (ans, wrongs) = clockChoices(rnd, h, m, kinds)
    val word = when (m) { 15 -> "quarter past"; 30 -> "half past"; else -> "quarter to" }
    mcText(
        rnd, "What time does the clock show?", Clock(h, m, digital = false), ans, wrongs,
        "Look at the long hand first: 3 is quarter past, 6 is half past, 9 is quarter to.",
        listOf(say("The long hand is on ${m / 5}. That is $word.", Clock(h, m, digital = true)), show("The time is $ans.", Clock(h, m), Mood.CHEER)),
    )
}

val timeMinutes = lesson(
    "time-minutes", "Minutes on the clock", "Count by fives around the clock", "⏱️",
    teach = listOf(
        say("Each number on the clock is five minutes. Let's count by fives around the dial.", Strip(listOf("5", "10", "15", "20", "25", "30")), counting = true),
        say("And on to sixty. The long hand on eight means forty minutes.", Strip(listOf("35", "40", "45", "50", "55", "60")), counting = true),
        show("Long hand on four? Four fives are twenty, so it is twenty minutes past.", Clock(2, 20), Mood.CHEER),
        show("Read the hour with the short hand, the minutes with the long hand: 2:20.", Clock(2, 20)),
    ),
) { rnd, i ->
    val h = rnd.between(1, 12)
    val m = rnd.between(1, 11) * 5 % 60
    val mins = if (m == 0) 5 else m
    fun fmt(hh: Int, mm: Int) = "${hh}:${mm.toString().padStart(2, '0')}"
    val ans = fmt(h, mins)
    val wrongs = LinkedHashSet<String>()
    // The classic mix-up: reading the two hands the wrong way round (1:20 becomes 4:05).
    wrongs.add(fmt(mins / 5, (h * 5) % 60))
    wrongs.add(fmt(h, (mins + 10) % 60)); wrongs.add(fmt(h % 12 + 1, mins))
    wrongs.add(fmt(h, if (mins >= 10) mins - 5 else mins + 5))
    wrongs.remove(ans)
    mcText(
        rnd, "What time does the clock show?", Clock(h, mins, digital = false), ans, wrongs.toList().take(3),
        "Count by fives from the top to where the long hand points.",
        listOf(
            say("The long hand points to ${mins / 5}. Count by fives: ${(1..mins / 5).joinToString(", ") { Words.number(it * 5) }}.", Clock(h, mins, digital = true)),
            show("The time is $ans.", Clock(h, mins), Mood.CHEER),
        ),
    )
}

private fun rupees(n: Int) = "₹$n"

val money = lesson(
    "money", "Indian money", "Coins and notes", "💰",
    teach = listOf(
        say("Our money is called the rupee. These are coins: one, two, five and ten rupees.", Money(listOf(1, 2, 5, 10))),
        say("And these are notes: ten, twenty, fifty and one hundred rupees.", Money(listOf(10, 20, 50, 100))),
        say("To count money, start with the biggest. Ten, then five, then two.", Money(listOf(10, 5, 2)), counting = false),
        say("Add them up as you go: ten, fifteen, seventeen.", Strip(listOf("10", "15", "17")), counting = true),
        show("So ten, five and two rupees make seventeen rupees.", Stack(Money(listOf(10, 5, 2)), BigText("₹17")), Mood.CHEER),
    ),
) { rnd, i ->
    val pool = if (band(i) == 0) listOf(1, 2, 5, 10) else listOf(1, 2, 5, 10, 20, 50, 100)
    val count = rnd.between(2, if (band(i) == 0) 3 else 5)
    val items = List(count) { rnd.choose(pool) }.sortedDescending()
    val total = items.sum()
    val running = items.runningFold(0) { a, b -> a + b }.drop(1)
    mcNumber(
        rnd, "How much money is this?", Money(items), total,
        "Start with the biggest one and keep adding.",
        listOf(
            say("Start with the biggest and add as you go.", Strip(running.map { "$it" }), counting = true),
            show("Altogether that is ${rupees(total)}.", Stack(Money(items), BigText(rupees(total))), Mood.CHEER),
        ),
        spread = 5, min = 2, max = 500, extra = listOf(total - 5, total + 5, total + 1, total - 1, total + 10).filter { it > 0 },
    ) { rupees(it) }
}

val moneyChange = lesson(
    "money-change", "Buying and change", "How much do I get back?", "🏪",
    teach = listOf(
        say("You buy a pencil for seven rupees. You give a ten rupee note.", Stack(BigText("Pencil  ₹7"), Money(listOf(10)))),
        say("How much change? Count up from seven to ten.", NumberLine(0, 10, start = 7, hops = listOf(8, 9, 10)), counting = true),
        show("You count three steps, so the change is three rupees.", eq("10", "−", "7", "=", "3"), Mood.CHEER),
        show("Change is what you paid, take away the price.", Board(listOf(line("paid − price = change"), good("₹10 − ₹7 = ₹3")))),
    ),
) { rnd, i ->
    val paid = rnd.choose(if (band(i) == 0) listOf(10, 20) else listOf(10, 20, 50, 100))
    val price = rnd.between(1, (paid * 9 / 10).coerceAtLeast(2))
    val item = rnd.choose(listOf("an eraser", "a pencil", "a biscuit", "a toffee", "a notebook", "a balloon"))
    val change = paid - price
    mcNumber(
        rnd, "$item costs ${rupees(price)}. You pay ${rupees(paid)}. How much change do you get?", null, change,
        "Change = what you paid − the price.",
        listOf(show("${rupees(paid)} take away ${rupees(price)} leaves ${rupees(change)}.", eq("$paid", "−", "$price", "=", "$change"), Mood.CHEER)),
        spread = 5, min = 1, max = 100, extra = listOf(paid + price, change + 1, change - 1, change + 10).filter { it > 0 },
    ) { rupees(it) }
}

val units = lesson(
    "measure-units", "Units of measure", "cm, m, km, g, kg, ml and litres", "⚖️",
    teach = listOf(
        say("We measure length in centimetres, metres and kilometres.", Board(listOf(line("1 m = 100 cm"), line("1 km = 1000 m")))),
        say("We measure weight in grams and kilograms.", Board(listOf(line("1 kg = 1000 g"), dim("a pencil is about 5 g"), dim("a bag of rice is 1 kg")))),
        say("And liquids in millilitres and litres.", Board(listOf(line("1 litre = 1000 ml"), dim("a spoon holds about 5 ml"), dim("a big bottle is 1 litre")))),
        show("To change to a smaller unit, multiply. Two kilograms is 2 times 1000, which is 2000 grams.", Board(listOf(line("2 kg"), dim("1 kg = 1000 g"), good("2 × 1000 = 2000 g"))), Mood.CHEER),
    ),
) { rnd, i ->
    if (i % 2 == 0) {
        val conv = listOf(Triple("m", "cm", 100), Triple("kg", "g", 1000), Triple("L", "ml", 1000), Triple("km", "m", 1000))
        val (big, small, f) = if (band(i) == 0) rnd.choose(conv.take(2)) else rnd.choose(conv)
        val n = rnd.between(2, 9)
        mcNumber(
            rnd, "$n $big = ? $small", Board(listOf(line("1 $big = $f $small"))), n * f,
            "1 $big is $f $small. Multiply by $n.",
            listOf(show("$n × $f = ${n * f}. So $n $big = ${n * f} $small.", Board(listOf(line("1 $big = $f $small"), good("$n × $f = ${n * f}"))), Mood.CHEER)),
            spread = f / 2, min = 1, max = 10000, extra = listOf(n * f / 10, n * f * 10, n + f),
        )
    } else {
        val items = listOf(
            "the length of a classroom" to "m", "the weight of a pencil" to "g", "a cup of tea" to "ml", "the weight of a sack of rice" to "kg",
            "the width of your thumb" to "cm", "the distance from home to school" to "km", "a bucket of water" to "litres", "the weight of an elephant" to "kg",
        )
        val (what, unit) = rnd.choose(items)
        val choices = listOf("cm", "m", "km", "g", "kg", "ml", "litres").filter { it != unit }.shuffled(rnd).take(3)
        mcText(rnd, "Which unit is best for $what?", null, unit, choices, "Is it small, medium or very big? Pick the unit that fits.", listOf(show("We use $unit for $what.", BigText(unit, sub = what), Mood.CHEER)))
    }
}

private val weekDays = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
private val monthNames = listOf("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December")

val calendar = lesson(
    "calendar", "Days and months", "The week and the year", "📅",
    teach = listOf(
        say("A week has seven days: Monday, Tuesday, Wednesday, Thursday, Friday, Saturday and Sunday.", Strip(listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"), arrows = false)),
        say("A year has twelve months. They go in this order.", Board(listOf(line("Jan  Feb  Mar  Apr"), line("May  Jun  Jul  Aug"), line("Sep  Oct  Nov  Dec")))),
        show("Some months have thirty days, some thirty-one. February is the shortest, with twenty-eight.", Board(listOf(line("31 days: Jan Mar May Jul Aug Oct Dec"), line("30 days: Apr Jun Sep Nov"), good("28 days: Feb")))),
    ),
) { rnd, i ->
    if (i % 2 == 0) {
        val k = rnd.between(0, 6)
        val ans = weekDays[(k + 1) % 7]
        val shown = listOf(weekDays[k].take(3), "?")
        mcText(rnd, "Which day comes after ${weekDays[k]}?", Strip(shown, blankAt = 1), ans, weekDays.filter { it != ans && it != weekDays[k] }.shuffled(rnd).take(3), "Say the days in order: ${weekDays.joinToString(", ") { it.take(3) }}.", listOf(show("After ${weekDays[k]} comes $ans.", Strip(listOf(weekDays[k].take(3), ans.take(3))), Mood.CHEER)))
    } else {
        val k = rnd.between(0, 11)
        val ans = monthNames[(k + 1) % 12]
        mcText(rnd, "Which month comes after ${monthNames[k]}?", null, ans, monthNames.filter { it != ans && it != monthNames[k] }.shuffled(rnd).take(3), "Say the months in order from January.", listOf(show("After ${monthNames[k]} comes $ans.", BigText(ans), Mood.CHEER)))
    }
}

val world5 = World(
    id = "w5", title = "Shape & Measure Bay", tagline = "Shapes, time, money and measuring", emoji = "🐠",
    color = 0xFF14B8A6, level = "Class 1 to 4",
    lessons = listOf(shapes2d, sidesCorners, shapes3d, patterns, length, timeHour, timeHalf, timeMinutes, money, moneyChange, units, calendar),
)
