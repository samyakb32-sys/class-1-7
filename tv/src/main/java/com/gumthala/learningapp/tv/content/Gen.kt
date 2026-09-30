package com.gumthala.learningapp.tv.content

import kotlin.random.Random

/*
 * Small toolkit shared by every lesson: things to count, wrong-answer makers, and the
 * multiple-choice builder. Emoji are restricted to long-established ones (Unicode <= 8) because
 * plenty of cheap TV boxes run Android 7-9 and would draw newer emoji as empty boxes.
 */

data class Thing(val emoji: String, val one: String, val many: String)

object Things {
    val apple = Thing("🍎", "apple", "apples")
    val banana = Thing("🍌", "banana", "bananas")
    val grapes = Thing("🍇", "bunch of grapes", "bunches of grapes")
    val strawberry = Thing("🍓", "strawberry", "strawberries")
    val orange = Thing("🍊", "orange", "oranges")
    val cherry = Thing("🍒", "cherry", "cherries")
    val balloon = Thing("🎈", "balloon", "balloons")
    val star = Thing("🌟", "star", "stars")
    val cookie = Thing("🍪", "cookie", "cookies")
    val candy = Thing("🍬", "sweet", "sweets")
    val flower = Thing("🌸", "flower", "flowers")
    val fish = Thing("🐟", "fish", "fish")
    val bird = Thing("🐦", "bird", "birds")
    val duck = Thing("🐤", "duckling", "ducklings")
    val dog = Thing("🐶", "puppy", "puppies")
    val cat = Thing("🐱", "kitten", "kittens")
    val car = Thing("🚗", "car", "cars")
    val ball = Thing("⚽", "ball", "balls")
    val caterpillar = Thing("🐛", "caterpillar", "caterpillars")
    val bee = Thing("🐝", "bee", "bees")
    val ladybird = Thing("🐞", "ladybird", "ladybirds")
    val pizza = Thing("🍕", "pizza", "pizzas")
    val iceCream = Thing("🍦", "ice cream", "ice creams")
    val heart = Thing("❤️", "heart", "hearts")
    val cake = Thing("🎂", "cake", "cakes")
    val gift = Thing("🎁", "present", "presents")

    /** Things that are safe on old Android emoji fonts. */
    val all: List<Thing> = listOf(
        apple, banana, grapes, strawberry, orange, cherry, balloon, star, cookie, candy, flower,
        fish, bird, duck, dog, cat, car, ball, caterpillar, bee, ladybird, pizza, iceCream, heart, cake, gift,
    )

    /** Small, easy-to-tell-apart objects for early counting (they read well at small sizes). */
    val easy: List<Thing> = listOf(apple, balloon, star, ball, flower, fish, duck, car, cookie, heart, cat, dog)

    fun pick(rnd: Random): Thing = all[rnd.nextInt(all.size)]
    fun pickEasy(rnd: Random): Thing = easy[rnd.nextInt(easy.size)]
}

fun Thing.name(n: Int): String = if (n == 1) one else many

/** "3 apples" */
fun Thing.count(n: Int): String = "$n ${name(n)}"

fun <T> Random.choose(list: List<T>): T = list[nextInt(list.size)]

/** Inclusive on both ends. */
fun Random.between(lo: Int, hi: Int): Int = if (hi <= lo) lo else lo + nextInt(hi - lo + 1)

/**
 * Plausible wrong numbers for [correct]: your [extra] guesses first (typical mistakes such as the
 * operation swapped), then close neighbours. Always distinct, never equal to [correct], in [min, max].
 */
fun wrongNumbers(
    correct: Int,
    rnd: Random,
    count: Int = 3,
    spread: Int = 3,
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    extra: List<Int> = emptyList(),
): List<Int> {
    val out = LinkedHashSet<Int>()
    for (e in extra.shuffled(rnd)) {
        if (out.size >= count) break
        if (e != correct && e in min..max) out.add(e)
    }
    var s = spread.coerceAtLeast(1)
    var guard = 0
    while (out.size < count && guard < 400) {
        guard++
        val d = rnd.between(1, s) * (if (rnd.nextBoolean()) 1 else -1)
        val v = correct + d
        if (v != correct && v in min..max) out.add(v)
        if (guard % 25 == 0) s++ // the neighbourhood is crowded (e.g. near 0): widen it
    }
    // Last resort so a question can never ship with fewer options than asked for.
    var v = correct
    while (out.size < count) {
        v++
        if (v != correct && v in min..max) out.add(v)
        if (v > correct + 1000) break
    }
    return out.toList().take(count)
}

/** Builds a multiple-choice question with the correct answer in a random slot. */
fun mcQuestion(
    rnd: Random,
    prompt: String,
    visual: Visual?,
    correct: Choice,
    wrongs: List<Choice>,
    hint: String,
    solution: List<Step>,
): Question {
    val uniqueWrongs = LinkedHashMap<String, Choice>()
    for (w in wrongs) {
        if (w.text != correct.text && w.text !in uniqueWrongs) uniqueWrongs[w.text] = w
    }
    val all = ArrayList<Choice>()
    all.add(correct)
    all.addAll(uniqueWrongs.values)
    all.shuffle(rnd)
    return Question(prompt, visual, all, all.indexOf(correct), hint, solution)
}

fun mcText(
    rnd: Random,
    prompt: String,
    visual: Visual?,
    correct: String,
    wrongs: List<String>,
    hint: String,
    solution: List<Step>,
): Question = mcQuestion(rnd, prompt, visual, Choice(correct), wrongs.map { Choice(it) }, hint, solution)

/** Numeric answer with automatically generated neighbours. */
fun mcNumber(
    rnd: Random,
    prompt: String,
    visual: Visual?,
    correct: Int,
    hint: String,
    solution: List<Step>,
    spread: Int = 3,
    min: Int = 0,
    max: Int = Int.MAX_VALUE,
    extra: List<Int> = emptyList(),
    show: (Int) -> String = { it.toString() },
): Question = mcText(
    rnd, prompt, visual, show(correct),
    wrongNumbers(correct, rnd, 3, spread, min, max, extra).map(show),
    hint, solution,
)

/** A lesson shorthand. */
fun lesson(
    id: String,
    title: String,
    blurb: String,
    icon: String,
    teach: List<Step>,
    questionCount: Int = 8,
    quiz: (Random, Int) -> Question,
): Lesson = Lesson(id, title, blurb, icon, teach, questionCount, quiz)

/** Which of three difficulty bands question [i] of 8 falls in: 0 (first three), 1 (next three), 2 (last two). */
fun band(i: Int): Int = when {
    i < 3 -> 0
    i < 6 -> 1
    else -> 2
}

// ---- Small authoring shorthands -------------------------------------------------------------

/** Captions always start with a capital, even when a template begins with a number word. */
internal fun String.capitalized(): String = replaceFirstChar { if (it.isLowerCase()) it.uppercase() else it.toString() }

fun say(text: String, visual: Visual, counting: Boolean = false, from: Int = 0, mood: Mood = Mood.HAPPY, to: Int = Int.MAX_VALUE) =
    Step(text.capitalized(), visual, counting, from, mood, to)

/** A step whose visual is fully drawn straight away (no reveal animation). */
fun show(text: String, visual: Visual, mood: Mood = Mood.HAPPY) = Step(text.capitalized(), visual, false, Step.ALL, mood)

fun board(vararg lines: String): Board = Board(lines.map { BoardLine(it) })

fun line(text: String) = BoardLine(text)
fun good(text: String) = BoardLine(text, Tone.GOOD)
fun hot(text: String) = BoardLine(text, Tone.HIGHLIGHT)
fun dim(text: String) = BoardLine(text, Tone.DIM)

fun eq(vararg tokens: String) = Equation(tokens.toList())
