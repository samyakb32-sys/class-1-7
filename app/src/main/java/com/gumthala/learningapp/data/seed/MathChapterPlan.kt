package com.gumthala.learningapp.data.seed

import kotlin.random.Random

/** One Maths chapter's title/description plus the generator(s) used to fill it with 12 questions. */
data class MathChapterPlan(
    val titleEn: String, val titleMr: String, val titleHi: String,
    val generators: List<(Random) -> GeneratedQuestion>
)

/**
 * Five Maths chapters per class, each backed by one or more procedural generators so every
 * question is computed (and therefore guaranteed correct) rather than hand-typed. Ranges step
 * up class over class, deliberately pitched a notch above the traditional grade level.
 */
object MathChapterPlans {

    fun forClass(classLevel: Int): List<MathChapterPlan> = when (classLevel) {
        1 -> listOf(
            MathChapterPlan("Numbers up to 20", "20 पर्यंतच्या संख्या", "20 तक की संख्याएँ",
                listOf({ r -> MathQuestionGenerator.counting(r, 20) })),
            MathChapterPlan("Addition within 20", "20 पर्यंत बेरीज", "20 तक जोड़",
                listOf({ r -> MathQuestionGenerator.addition(r, 16) })),
            MathChapterPlan("Subtraction within 20", "20 पर्यंत वजाबाकी", "20 तक घटाव",
                listOf({ r -> MathQuestionGenerator.subtraction(r, 20) })),
            MathChapterPlan("Comparing Numbers", "संख्यांची तुलना", "संख्याओं की तुलना",
                listOf({ r -> MathQuestionGenerator.comparison(r, 30) })),
            MathChapterPlan("Shapes & Sides", "आकार आणि बाजू", "आकृतियाँ और भुजाएँ",
                listOf({ r -> MathQuestionGenerator.shapeSides(r) }))
        )
        2 -> listOf(
            MathChapterPlan("Numbers up to 200", "200 पर्यंतच्या संख्या", "200 तक की संख्याएँ",
                listOf({ r -> MathQuestionGenerator.placeValue(r, 200) })),
            MathChapterPlan("Addition & Subtraction to 100", "100 पर्यंत बेरीज-वजाबाकी", "100 तक जोड़-घटाव",
                listOf({ r -> MathQuestionGenerator.addition(r, 100) }, { r -> MathQuestionGenerator.subtraction(r, 100) })),
            MathChapterPlan("Multiplication Tables 2-5", "पाढे २ ते ५", "पहाड़े 2 से 5",
                listOf({ r -> MathQuestionGenerator.multiplication(r, (2..5).random(r)) })),
            MathChapterPlan("Money Basics", "पैशांची ओळख", "पैसों की जानकारी",
                listOf({ r -> MathQuestionGenerator.moneySum(r) })),
            MathChapterPlan("Simple Fractions", "साध्या अपूर्णांक", "सरल भिन्न",
                listOf({ r -> MathQuestionGenerator.fractionShaded(r) }))
        )
        3 -> listOf(
            MathChapterPlan("Numbers up to 1000", "1000 पर्यंतच्या संख्या", "1000 तक की संख्याएँ",
                listOf({ r -> MathQuestionGenerator.placeValue(r, 1000) })),
            MathChapterPlan("Multiplication Tables 6-10", "पाढे ६ ते १०", "पहाड़े 6 से 10",
                listOf({ r -> MathQuestionGenerator.multiplication(r, (6..10).random(r)) })),
            MathChapterPlan("Division Basics", "भागाकाराची ओळख", "भाग के मूल सिद्धांत",
                listOf({ r -> MathQuestionGenerator.division(r, 10) })),
            MathChapterPlan("Fractions in Shapes", "आकृतीतील अपूर्णांक", "आकृतियों में भिन्न",
                listOf({ r -> MathQuestionGenerator.fractionShaded(r) })),
            MathChapterPlan("Perimeter Basics", "परिमितीची ओळख", "परिधि के मूल सिद्धांत",
                listOf({ r -> MathQuestionGenerator.rectanglePerimeter(r) }))
        )
        4 -> listOf(
            MathChapterPlan("Large Numbers", "मोठ्या संख्या", "बड़ी संख्याएँ",
                listOf({ r -> MathQuestionGenerator.placeValue(r, 10000) })),
            MathChapterPlan("Multiplication & Division", "गुणाकार आणि भागाकार", "गुणा और भाग",
                listOf({ r -> MathQuestionGenerator.multiplication(r, (2..12).random(r)) }, { r -> MathQuestionGenerator.division(r, 12) })),
            MathChapterPlan("Perimeter & Area", "परिमिती आणि क्षेत्रफळ", "परिधि और क्षेत्रफल",
                listOf({ r -> MathQuestionGenerator.rectanglePerimeter(r) }, { r -> MathQuestionGenerator.rectangleArea(r) })),
            MathChapterPlan("Money & Averages", "पैसे आणि सरासरी", "पैसे और औसत",
                listOf({ r -> MathQuestionGenerator.moneySum(r) }, { r -> MathQuestionGenerator.average(r) })),
            MathChapterPlan("Comparing Large Numbers", "मोठ्या संख्यांची तुलना", "बड़ी संख्याओं की तुलना",
                listOf({ r -> MathQuestionGenerator.comparison(r, 10000) }))
        )
        5 -> listOf(
            MathChapterPlan("Ratios", "गुणोत्तर", "अनुपात",
                listOf({ r -> MathQuestionGenerator.ratioSimplify(r) })),
            MathChapterPlan("Percentages", "टक्केवारी", "प्रतिशत",
                listOf({ r -> MathQuestionGenerator.percentage(r) })),
            MathChapterPlan("Perimeter, Area & Averages", "परिमिती, क्षेत्रफळ आणि सरासरी", "परिधि, क्षेत्रफल और औसत",
                listOf({ r -> MathQuestionGenerator.rectanglePerimeter(r) }, { r -> MathQuestionGenerator.rectangleArea(r) }, { r -> MathQuestionGenerator.average(r) })),
            MathChapterPlan("Simple Equations", "सोप्या समीकरणे", "सरल समीकरण",
                listOf({ r -> MathQuestionGenerator.linearEquation(r) })),
            MathChapterPlan("Fractions Advanced", "प्रगत अपूर्णांक", "उन्नत भिन्न",
                listOf({ r -> MathQuestionGenerator.fractionShaded(r) }))
        )
        6 -> listOf(
            MathChapterPlan("Ratio & Proportion", "गुणोत्तर आणि प्रमाण", "अनुपात और समानुपात",
                listOf({ r -> MathQuestionGenerator.ratioSimplify(r) })),
            MathChapterPlan("Percentages", "टक्केवारी", "प्रतिशत",
                listOf({ r -> MathQuestionGenerator.percentage(r) })),
            MathChapterPlan("Simple Equations", "समीकरणे", "समीकरण",
                listOf({ r -> MathQuestionGenerator.linearEquation(r) })),
            MathChapterPlan("Area & Perimeter", "क्षेत्रफळ आणि परिमिती", "क्षेत्रफल और परिधि",
                listOf({ r -> MathQuestionGenerator.rectangleArea(r) }, { r -> MathQuestionGenerator.rectanglePerimeter(r) })),
            MathChapterPlan("Averages & Data", "सरासरी आणि माहिती", "औसत और आँकड़े",
                listOf({ r -> MathQuestionGenerator.average(r) }))
        )
        else -> listOf(
            MathChapterPlan("Ratios & Percentages", "गुणोत्तर आणि टक्केवारी", "अनुपात और प्रतिशत",
                listOf({ r -> MathQuestionGenerator.ratioSimplify(r) }, { r -> MathQuestionGenerator.percentage(r) })),
            MathChapterPlan("Equations", "समीकरणे", "समीकरण",
                listOf({ r -> MathQuestionGenerator.linearEquation(r) })),
            MathChapterPlan("Area & Perimeter Advanced", "प्रगत क्षेत्रफळ आणि परिमिती", "उन्नत क्षेत्रफल और परिधि",
                listOf({ r -> MathQuestionGenerator.rectangleArea(r) }, { r -> MathQuestionGenerator.rectanglePerimeter(r) })),
            MathChapterPlan("Averages & Data Handling", "सरासरी आणि माहिती हाताळणी", "औसत और आँकड़ा प्रबंधन",
                listOf({ r -> MathQuestionGenerator.average(r) })),
            MathChapterPlan("Mixed Review", "संमिश्र उजळणी", "मिश्रित अभ्यास",
                listOf(
                    { r -> MathQuestionGenerator.percentage(r) },
                    { r -> MathQuestionGenerator.ratioSimplify(r) },
                    { r -> MathQuestionGenerator.linearEquation(r) },
                    { r -> MathQuestionGenerator.division(r, 12) }
                ))
        )
    }
}
