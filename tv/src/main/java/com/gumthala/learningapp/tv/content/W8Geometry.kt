package com.gumthala.learningapp.tv.content

import kotlin.random.Random

/*
 * World 8 - Geometry Galaxy. Perimeter and area, angles, triangles and quadrilaterals, circles,
 * symmetry, volume, and the parts of solid shapes.
 */

private fun sq(n: Int) = "$n cm²"

val perimeter = lesson(
    "perimeter", "Perimeter", "The distance all the way round", "🚶",
    teach = listOf(
        say("Perimeter is the distance all the way around a shape. Let's walk round this rectangle.", RectGrid(5, 3, GridMode.PERIMETER, "m"), counting = false),
        say("Add up all four sides: five, three, five and three.", Board(listOf(line("5 + 3 + 5 + 3"), dim("5 + 3 = 8"), dim("8 + 8 = 16"), good("perimeter = 16 m")))),
        show("A rectangle has two long sides and two short sides. So perimeter = 2 × (length + width).", Board(listOf(line("2 × (5 + 3)"), line("2 × 8"), good("= 16 m"))), Mood.CHEER),
        show("A square has four equal sides. A square with sides of 4 cm has a perimeter of 4 × 4 = 16 cm.", Shape(ShapeKind.SQUARE, "4 cm each side")),
    ),
) { rnd, i ->
    val w = rnd.between(2, if (band(i) == 0) 6 else 10)
    val h = rnd.between(2, if (band(i) == 0) 5 else 8)
    val unit = rnd.choose(listOf("cm", "m"))
    val ans = 2 * (w + h)
    mcNumber(
        rnd, "What is the perimeter of this rectangle?", RectGrid(w, h, GridMode.STATIC, unit), ans,
        "Add all four sides, or double (length + width).",
        listOf(
            say("Walk all the way round the shape.", RectGrid(w, h, GridMode.PERIMETER, unit)),
            show("$w + $h + $w + $h = $ans $unit.", Board(listOf(line("$w + $h + $w + $h"), good("= $ans $unit"))), Mood.CHEER),
        ),
        spread = 3, min = 4, max = 60, extra = listOf(w * h, w + h, 2 * w + h, w + 2 * h).filter { it != ans },
    ) { "$it $unit" }
}

val areaCount = lesson(
    "area-count", "Area: counting squares", "How much flat space?", "🟩",
    teach = listOf(
        say("Area is how much flat space a shape covers. We measure it in square units.", RectGrid(5, 3, GridMode.STATIC)),
        say("Fill the shape with squares, one row at a time. Count: five, ten, fifteen.", RectGrid(5, 3, GridMode.AREA), counting = true),
        show("Three rows of five squares. The area is fifteen squares.", Stack(RectGrid(5, 3, GridMode.STATIC), eq("3", "×", "5", "=", "15")), Mood.CHEER),
    ),
) { rnd, i ->
    val w = rnd.between(2, if (band(i) == 0) 5 else 8)
    val h = rnd.between(2, if (band(i) == 0) 4 else 6)
    mcNumber(
        rnd, "How many squares cover this shape?", RectGrid(w, h, GridMode.STATIC), w * h,
        "Count the squares in one row, then multiply by the number of rows.",
        listOf(
            say("Count one row at a time: ${(1..h).joinToString(", ") { Words.number(it * w) }}.", RectGrid(w, h, GridMode.AREA), counting = true),
            show("$h rows of $w make ${w * h} squares.", eq("$h", "×", "$w", "=", "${w * h}"), Mood.CHEER),
        ),
        spread = 3, min = 4, max = 60, extra = listOf(2 * (w + h), w + h, w * h + w, w * h - h),
    )
}

val areaFormula = lesson(
    "area-formula", "Area of rectangles", "Length times breadth", "📐",
    teach = listOf(
        say("We don't need to count every square. Area = length × breadth.", Board(listOf(good("area = length × breadth")))),
        say("A rectangle is 8 centimetres long and 5 centimetres wide.", RectGrid(8, 5, GridMode.STATIC, "cm")),
        show("Eight times five is forty. The area is 40 square centimetres.", Board(listOf(line("8 × 5 = 40"), good("area = 40 cm²"))), Mood.CHEER),
        show("A square has equal sides, so its area is side × side. A square of side 6 has area 36.", Board(listOf(line("6 × 6 = 36"), good("area = 36 cm²")))),
    ),
) { rnd, i ->
    val w = rnd.between(3, if (band(i) == 0) 8 else 12)
    val h = rnd.between(2, if (band(i) == 0) 6 else 9)
    val square = band(i) == 2 && rnd.nextBoolean()
    val ww = if (square) h else w
    val ans = ww * h
    val prompt = if (square) "A square has sides of $h cm. What is its area?" else "A rectangle is $ww cm long and $h cm wide. What is its area?"
    mcNumber(
        rnd, prompt, null, ans, "Area = length × breadth.",
        listOf(show("$ww × $h = $ans. The area is $ans square centimetres.", Board(listOf(line("$ww × $h = $ans"), good("area = $ans cm²"))), Mood.CHEER)),
        spread = 4, min = 4, max = 150, extra = listOf(2 * (ww + h), ww + h, ans + ww, ans - h),
    ) { sq(it) }
}

val areaTriangle = lesson(
    "area-triangle", "Area of a triangle", "Half of a rectangle", "🔺",
    teach = listOf(
        say("A triangle is half of a rectangle. Cut a rectangle along its diagonal.", Shape(ShapeKind.RIGHT_TRIANGLE, "base 6, height 4")),
        say("The rectangle has area six times four, which is twenty-four.", Board(listOf(line("rectangle: 6 × 4 = 24")))),
        show("The triangle is half of that. So the area is twelve.", Board(listOf(line("rectangle: 6 × 4 = 24"), good("triangle: 24 ÷ 2 = 12"))), Mood.CHEER),
        show("Area of a triangle = half × base × height.", Board(listOf(good("area = ½ × base × height"), line("= ½ × 6 × 4 = 12 cm²")))),
    ),
) { rnd, i ->
    val b = rnd.between(2, if (band(i) == 0) 8 else 14)
    val h = 2 * rnd.between(1, if (band(i) == 0) 4 else 7)
    val ans = b * h / 2
    mcNumber(
        rnd, "A triangle has a base of $b cm and a height of $h cm. What is its area?", Shape(ShapeKind.RIGHT_TRIANGLE, "base $b, height $h"), ans,
        "Multiply base × height, then halve it.",
        listOf(show("$b × $h = ${b * h}. Half of ${b * h} is $ans.", Board(listOf(line("$b × $h = ${b * h}"), good("÷ 2 = $ans cm²"))), Mood.CHEER)),
        spread = 3, min = 1, max = 100, extra = listOf(b * h, b + h, ans + b, ans - 1).filter { it != ans && it > 0 },
    ) { sq(it) }
}

private fun angleType(d: Int) = when {
    d < 90 -> "acute"
    d == 90 -> "right"
    d < 180 -> "obtuse"
    else -> "straight"
}

val angles = lesson(
    "angles", "Kinds of angles", "Acute, right, obtuse, straight", "📏",
    teach = listOf(
        say("An angle is how far a line turns. A small turn, less than a right angle, is acute.", AngleShape(45, "acute")),
        say("A quarter turn is a right angle. It is exactly ninety degrees, like the corner of a book.", AngleShape(90, "90°")),
        say("More than a right angle, but less than a straight line, is obtuse.", AngleShape(130, "obtuse")),
        say("A straight line is one hundred and eighty degrees.", AngleShape(180, "straight")),
        show("Acute is small, right is square, obtuse is wide.", Board(listOf(line("less than 90°: acute"), line("exactly 90°: right"), line("between 90° and 180°: obtuse"), line("180°: straight"))), Mood.CHEER),
    ),
) { rnd, i ->
    val kinds = if (band(i) == 0) listOf("acute", "right", "obtuse") else listOf("acute", "right", "obtuse", "straight")
    val k = rnd.choose(kinds)
    val d = when (k) { "acute" -> rnd.between(20, 80); "right" -> 90; "obtuse" -> rnd.between(100, 160); else -> 180 }
    val why = when (k) {
        "acute" -> "It is smaller than a right angle, so it is acute."
        "right" -> "It is exactly the same as a right angle, so it is right."
        "obtuse" -> "It is bigger than a right angle but smaller than a straight line, so it is obtuse."
        else -> "It is a flat, straight line, so it is straight."
    }
    mcText(
        rnd, "What kind of angle is this?", AngleShape(d), k, listOf("acute", "right", "obtuse", "straight").filter { it != k },
        "Compare it with a square corner (a right angle).",
        listOf(show(why, AngleShape(d, if (k == "right") "90°" else k), Mood.CHEER)),
    )
}

val angleSums = lesson(
    "angle-sums", "Angles that add up", "Straight lines and triangles", "➕",
    teach = listOf(
        say("Angles on a straight line always add up to one hundred and eighty degrees.", Board(listOf(good("straight line: 180°"), line("120° + ?° = 180°")))),
        say("So the missing angle is 180 take away 120, which is sixty.", Board(listOf(line("120° + ?° = 180°"), good("180 − 120 = 60°")))),
        say("The three angles inside any triangle add up to one hundred and eighty too.", Board(listOf(good("triangle: 180°"), line("50° + 60° + ?° = 180°"), dim("50 + 60 = 110"), good("180 − 110 = 70°")))),
        show("A right angle is 90°, and a full turn is 360°.", Board(listOf(line("right angle: 90°"), line("straight line: 180°"), line("full turn: 360°"))), Mood.CHEER),
    ),
) { rnd, i ->
    when (i % 3) {
        0 -> { val a = rnd.between(2, 16) * 10; mcNumber(rnd, "Two angles on a straight line: $a° and ?°. What is the missing angle?", AngleShape(a, "$a°"), 180 - a, "A straight line is 180°. Take $a away.", listOf(show("180 − $a = ${180 - a}.", Board(listOf(line("$a° + ?° = 180°"), good("180 − $a = ${180 - a}°"))), Mood.CHEER)), spread = 10, min = 10, max = 170, extra = listOf(90 - a.coerceAtMost(80), a, 360 - a).filter { it > 0 && it != 180 - a }) { "$it°" } }
        1 -> { val a = rnd.between(3, 10) * 10; val b = rnd.between(3, (170 - a) / 10) * 10; val c = 180 - a - b
            mcNumber(rnd, "A triangle has angles $a° and $b°. What is the third angle?", null, c, "The three angles of a triangle add up to 180°.", listOf(show("$a + $b = ${a + b}. 180 − ${a + b} = $c.", Board(listOf(line("$a + $b = ${a + b}"), good("180 − ${a + b} = $c°"))), Mood.CHEER)), spread = 10, min = 10, max = 140, extra = listOf(a + b, 360 - a - b, c + 10, c - 10).filter { it > 0 && it != c }) { "$it°" } }
        else -> { val a = rnd.between(1, 8) * 10; mcNumber(rnd, "Two angles make a right angle. One is $a°. What is the other?", AngleShape(a, "$a°"), 90 - a, "A right angle is 90°.", listOf(show("90 − $a = ${90 - a}.", Board(listOf(line("$a° + ?° = 90°"), good("90 − $a = ${90 - a}°"))), Mood.CHEER)), spread = 10, min = 10, max = 80, extra = listOf(180 - a, a, 90 - a + 10).filter { it > 0 && it != 90 - a }) { "$it°" } }
    }
}

private val triangleNames = mapOf(ShapeKind.EQUILATERAL to "equilateral", ShapeKind.ISOSCELES to "isosceles", ShapeKind.SCALENE to "scalene", ShapeKind.RIGHT_TRIANGLE to "right-angled")
private val triangleFacts = mapOf(
    ShapeKind.EQUILATERAL to "All three sides are equal.",
    ShapeKind.ISOSCELES to "Two sides are equal.",
    ShapeKind.SCALENE to "No sides are equal.",
    ShapeKind.RIGHT_TRIANGLE to "It has one square corner (90°).",
)

val triangles = lesson(
    "triangles", "Kinds of triangles", "Equilateral, isosceles, scalene", "🔺",
    teach = listOf(
        say("Triangles are named by their sides. Three equal sides: equilateral.", Shape(ShapeKind.EQUILATERAL, "equilateral")),
        say("Two equal sides: isosceles.", Shape(ShapeKind.ISOSCELES, "isosceles")),
        say("No equal sides: scalene.", Shape(ShapeKind.SCALENE, "scalene")),
        say("A triangle with one square corner is right-angled.", Shape(ShapeKind.RIGHT_TRIANGLE, "right-angled")),
    ),
) { rnd, i ->
    val kinds = if (band(i) == 0) triangleNames.keys.take(3) else triangleNames.keys.toList()
    val k = rnd.choose(kinds)
    mcText(
        rnd, "What kind of triangle is this?", Shape(k), triangleNames.getValue(k), triangleNames.filterKeys { it != k }.values.shuffled(rnd).take(3),
        "Look at the sides: how many are equal?",
        listOf(show("${triangleFacts.getValue(k)} It is ${triangleNames.getValue(k)}.", Shape(k, triangleNames.getValue(k)), Mood.CHEER)),
    )
}

private val quadNames = mapOf(ShapeKind.SQUARE to "square", ShapeKind.RECTANGLE to "rectangle", ShapeKind.RHOMBUS to "rhombus", ShapeKind.PARALLELOGRAM to "parallelogram", ShapeKind.TRAPEZIUM to "trapezium")
private val quadFacts = mapOf(
    ShapeKind.SQUARE to "Four equal sides and four right angles.",
    ShapeKind.RECTANGLE to "Opposite sides equal and four right angles.",
    ShapeKind.RHOMBUS to "Four equal sides, but the angles are not square.",
    ShapeKind.PARALLELOGRAM to "Two pairs of parallel sides, slanted.",
    ShapeKind.TRAPEZIUM to "Just one pair of parallel sides.",
)

val quadrilaterals = lesson(
    "quadrilaterals", "Four-sided shapes", "Square, rectangle, rhombus...", "🔲",
    teach = listOf(
        say("Shapes with four sides are called quadrilaterals. A square has four equal sides and four square corners.", Shape(ShapeKind.SQUARE, "square")),
        say("A rectangle has opposite sides equal.", Shape(ShapeKind.RECTANGLE, "rectangle")),
        say("A rhombus is like a squashed square: four equal sides.", Shape(ShapeKind.RHOMBUS, "rhombus")),
        say("A parallelogram has two pairs of parallel sides.", Shape(ShapeKind.PARALLELOGRAM, "parallelogram")),
        say("A trapezium has just one pair of parallel sides.", Shape(ShapeKind.TRAPEZIUM, "trapezium")),
    ),
) { rnd, i ->
    val kinds = if (band(i) == 0) quadNames.keys.take(3) else quadNames.keys.toList()
    val k = rnd.choose(kinds)
    mcText(
        rnd, "What is the name of this shape?", Shape(k), quadNames.getValue(k), quadNames.filterKeys { it != k }.values.shuffled(rnd).take(3),
        "Count the equal sides and look for square corners.",
        listOf(show("${quadFacts.getValue(k)} It is a ${quadNames.getValue(k)}.", Shape(k, quadNames.getValue(k)), Mood.CHEER)),
    )
}

val circles = lesson(
    "circles", "Parts of a circle", "Radius, diameter, circumference", "⭕",
    teach = listOf(
        say("The radius goes from the centre to the edge. The diameter goes all the way across, through the centre.", Shape(ShapeKind.CIRCLE, "radius and diameter")),
        show("The diameter is always twice the radius.", Board(listOf(line("diameter = 2 × radius"), line("radius = diameter ÷ 2"), good("radius 5 → diameter 10"))), Mood.CHEER),
        show("The distance all the way round a circle is the circumference. It is about three and a bit times the diameter.", Board(listOf(line("circumference = 2 × π × radius"), dim("π is about 22/7"), line("radius 7: 2 × 22/7 × 7 = 44")))),
    ),
) { rnd, i ->
    when {
        i % 3 == 0 -> { val r = rnd.between(2, 15); mcNumber(rnd, "The radius of a circle is $r cm. What is the diameter?", Shape(ShapeKind.CIRCLE), 2 * r, "The diameter is twice the radius.", listOf(show("2 × $r = ${2 * r}.", Board(listOf(line("diameter = 2 × $r"), good("= ${2 * r} cm"))), Mood.CHEER)), spread = 3, min = 2, max = 40, extra = listOf(r, r + 2, r * r).filter { it != 2 * r }) { "$it cm" } }
        i % 3 == 1 -> { val r = rnd.between(2, 15); mcNumber(rnd, "The diameter of a circle is ${2 * r} cm. What is the radius?", Shape(ShapeKind.CIRCLE), r, "The radius is half the diameter.", listOf(show("${2 * r} ÷ 2 = $r.", Board(listOf(line("radius = ${2 * r} ÷ 2"), good("= $r cm"))), Mood.CHEER)), spread = 3, min = 1, max = 20, extra = listOf(2 * r, r + 2, r - 1).filter { it != r && it > 0 }) { "$it cm" } }
        else -> { val r = 7 * rnd.between(1, 3); val c = 2 * 22 * r / 7
            mcNumber(rnd, "The radius is $r cm. Using π = 22/7, what is the circumference?", Shape(ShapeKind.CIRCLE), c, "Circumference = 2 × π × radius.", listOf(show("2 × 22/7 × $r = $c.", Board(listOf(line("2 × 22/7 × $r"), dim("$r ÷ 7 = ${r / 7}"), good("2 × 22 × ${r / 7} = $c cm"))), Mood.CHEER)), spread = 6, min = 6, max = 200, extra = listOf(c / 2, c * 2, 22 * r / 7, r * r).filter { it != c && it > 0 }) { "$it cm" } }
    }
}

/** "a square", "an isosceles triangle". */
private fun an(noun: String) = (if (noun.first() in "aeiou") "an " else "a ") + noun

private class SymShape(val kind: ShapeKind, val name: String, val axes: Int)
private val symShapes = listOf(
    SymShape(ShapeKind.SQUARE, "square", 4), SymShape(ShapeKind.RECTANGLE, "rectangle", 2), SymShape(ShapeKind.EQUILATERAL, "equilateral triangle", 3),
    SymShape(ShapeKind.ISOSCELES, "isosceles triangle", 1), SymShape(ShapeKind.RHOMBUS, "rhombus", 2), SymShape(ShapeKind.PENTAGON, "pentagon", 5),
    SymShape(ShapeKind.HEXAGON, "hexagon", 6), SymShape(ShapeKind.SCALENE, "scalene triangle", 0), SymShape(ShapeKind.PARALLELOGRAM, "parallelogram", 0),
)

val symmetry = lesson(
    "symmetry", "Lines of symmetry", "Fold it so both halves match", "🦋",
    teach = listOf(
        say("A line of symmetry splits a shape into two halves that match exactly, like a mirror.", Shape(ShapeKind.SQUARE, "square", axes = 1)),
        say("A square has four lines of symmetry. Watch them appear.", Shape(ShapeKind.SQUARE, "4 lines", axes = 4), counting = false),
        say("A rectangle has only two.", Shape(ShapeKind.RECTANGLE, "2 lines", axes = 2)),
        say("A triangle with three equal sides has three.", Shape(ShapeKind.EQUILATERAL, "3 lines", axes = 3)),
        say("A regular pentagon, with five equal sides, has five lines. A regular hexagon has six.", Shape(ShapeKind.HEXAGON, "6 lines", axes = 6), counting = false),
        show("Some shapes have none. A slanted parallelogram cannot be folded to match.", Shape(ShapeKind.PARALLELOGRAM, "0 lines"), Mood.THINK),
    ),
) { rnd, i ->
    val pool = if (band(i) == 0) symShapes.take(4) else symShapes
    val s = rnd.choose(pool)
    mcNumber(
        rnd, "How many lines of symmetry does this ${s.name} have?", Shape(s.kind), s.axes,
        "Imagine folding it so the two halves match exactly.",
        listOf(say("Fold it every way that makes the halves match.", Shape(s.kind, axes = s.axes), counting = false), show(
            when (s.axes) {
                0 -> "${an(s.name).capitalized()} has no lines of symmetry."
                1 -> "${an(s.name).capitalized()} has 1 line of symmetry."
                else -> "${an(s.name).capitalized()} has ${s.axes} lines of symmetry."
            },
            Shape(s.kind, if (s.axes == 1) "1 line" else "${s.axes} lines", axes = s.axes), Mood.CHEER,
        )),
        spread = 2, min = 0, max = 8, extra = listOf(s.axes + 1, s.axes - 1, s.axes + 2, 1, 2).filter { it != s.axes && it >= 0 },
    )
}

val volume = lesson(
    "volume", "Volume of a box", "How much space inside?", "📦",
    teach = listOf(
        say("Volume is the space inside a solid shape. We count in cubic units.", Solid(SolidKind.CUBOID, "a box")),
        say("A box is 4 long, 3 wide and 2 high. The bottom layer has 4 times 3, which is twelve cubes.", Board(listOf(line("bottom layer: 4 × 3 = 12 cubes")))),
        say("There are two layers, so twenty-four cubes altogether.", Board(listOf(line("bottom layer: 4 × 3 = 12"), line("2 layers: 12 × 2"), good("= 24 cubes")))),
        show("Volume = length × breadth × height.", Board(listOf(good("volume = l × b × h"), line("4 × 3 × 2 = 24 cm³"))), Mood.CHEER),
    ),
) { rnd, i ->
    val l = rnd.between(2, if (band(i) == 0) 5 else 8)
    val b = rnd.between(2, if (band(i) == 0) 4 else 6)
    val h = rnd.between(2, if (band(i) == 0) 3 else 5)
    val v = l * b * h
    mcNumber(
        rnd, "A box is $l cm long, $b cm wide and $h cm high. What is its volume?", Solid(SolidKind.CUBOID), v,
        "Volume = length × breadth × height.",
        listOf(show("$l × $b = ${l * b}, and ${l * b} × $h = $v.", Board(listOf(line("$l × $b × $h"), dim("$l × $b = ${l * b}"), good("${l * b} × $h = $v cm³"))), Mood.CHEER)),
        spread = 5, min = 4, max = 400, extra = listOf(l + b + h, l * b, 2 * (l * b + b * h + l * h), v + l).filter { it != v },
    ) { "$it cm³" }
}

private class SolidFacts(val kind: SolidKind, val name: String, val faces: Int, val edges: Int, val vertices: Int)
private val solidFacts = listOf(
    SolidFacts(SolidKind.CUBE, "cube", 6, 12, 8), SolidFacts(SolidKind.CUBOID, "cuboid", 6, 12, 8), SolidFacts(SolidKind.PYRAMID, "square pyramid", 5, 8, 5),
)

val solidParts = lesson(
    "solid-parts", "Faces, edges, corners", "The parts of solid shapes", "🧊",
    teach = listOf(
        say("A face is a flat side. An edge is where two faces meet. A vertex is a corner.", Solid(SolidKind.CUBE, "cube")),
        say("A cube has six faces, twelve edges and eight vertices.", Board(listOf(line("faces: 6"), line("edges: 12"), line("vertices: 8")))),
        say("A square pyramid has five faces, eight edges and five vertices.", Board(listOf(line("faces: 5"), line("edges: 8"), line("vertices: 5")))),
        show("A cuboid, like a matchbox, has the same numbers as a cube.", Solid(SolidKind.CUBOID, "6 faces, 12 edges, 8 vertices"), Mood.CHEER),
    ),
) { rnd, i ->
    val s = rnd.choose(if (band(i) == 0) solidFacts.take(2) else solidFacts)
    val what = rnd.choose(listOf("faces", "edges", "vertices"))
    val ans = when (what) { "faces" -> s.faces; "edges" -> s.edges; else -> s.vertices }
    mcNumber(
        rnd, "How many $what does a ${s.name} have?", Solid(s.kind), ans,
        when (what) { "faces" -> "Faces are the flat sides."; "edges" -> "Edges are the lines where faces meet."; else -> "Vertices are the corners." },
        listOf(show("A ${s.name} has ${s.faces} faces, ${s.edges} edges and ${s.vertices} vertices.", Board(listOf(line("${s.name}"), line("faces: ${s.faces}"), line("edges: ${s.edges}"), line("vertices: ${s.vertices}"))), Mood.CHEER)),
        spread = 2, min = 3, max = 14, extra = listOf(s.faces, s.edges, s.vertices, ans + 1, ans - 1).filter { it != ans },
    )
}

val world8 = World(
    id = "w8", title = "Geometry Galaxy", tagline = "Area, angles, shapes and symmetry", emoji = "🚀",
    color = 0xFF6366F1, level = "Class 4 to 7",
    lessons = listOf(perimeter, areaCount, areaFormula, areaTriangle, angles, angleSums, triangles, quadrilaterals, circles, symmetry, volume, solidParts),
)
