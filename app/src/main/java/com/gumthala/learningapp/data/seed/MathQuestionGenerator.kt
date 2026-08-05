package com.gumthala.learningapp.data.seed

import kotlin.random.Random

/**
 * Procedurally generates correct, class-appropriate arithmetic questions. Every value is
 * computed (never hard-coded), so results are guaranteed mathematically correct; only the
 * surrounding phrasing is templated in all three languages.
 */
object MathQuestionGenerator {

    private fun numericOptions(correct: Int, random: Random, spread: Int = 5): Pair<List<Int>, Int> {
        val distractors = mutableSetOf<Int>()
        while (distractors.size < 3) {
            val candidate = correct + random.nextInt(-spread, spread + 1)
            if (candidate != correct && candidate >= 0) distractors.add(candidate)
        }
        val options = (listOf(correct) + distractors)
        return options to 0
    }

    fun addition(random: Random, maxOperand: Int): GeneratedQuestion {
        val a = random.nextInt(1, maxOperand)
        val b = random.nextInt(1, maxOperand)
        val correct = a + b
        val (opts, idx) = numericOptions(correct, random, spread = 4)
        return GeneratedQuestion(
            textEn = "What is $a + $b?",
            textMr = "$a + $b किती आहे?",
            textHi = "$a + $b कितना है?",
            optionsEn = opts.map { it.toString() },
            optionsMr = opts.map { it.toString() },
            optionsHi = opts.map { it.toString() },
            correctIndex = idx,
            difficulty = "easy"
        )
    }

    fun subtraction(random: Random, maxOperand: Int): GeneratedQuestion {
        val a = random.nextInt(2, maxOperand)
        val b = random.nextInt(1, a + 1)
        val correct = a - b
        val (opts, idx) = numericOptions(correct, random, spread = 4)
        return GeneratedQuestion(
            textEn = "What is $a - $b?",
            textMr = "$a - $b किती उरते?",
            textHi = "$a - $b कितना बचता है?",
            optionsEn = opts.map { it.toString() },
            optionsMr = opts.map { it.toString() },
            optionsHi = opts.map { it.toString() },
            correctIndex = idx,
            difficulty = "easy"
        )
    }

    fun multiplication(random: Random, table: Int, maxMultiplier: Int = 10): GeneratedQuestion {
        val b = random.nextInt(1, maxMultiplier + 1)
        val correct = table * b
        val (opts, idx) = numericOptions(correct, random, spread = table)
        return GeneratedQuestion(
            textEn = "What is $table × $b?",
            textMr = "$table × $b किती आहे?",
            textHi = "$table × $b कितना है?",
            optionsEn = opts.map { it.toString() },
            optionsMr = opts.map { it.toString() },
            optionsHi = opts.map { it.toString() },
            correctIndex = idx,
            difficulty = "medium"
        )
    }

    fun division(random: Random, maxDivisor: Int): GeneratedQuestion {
        val divisor = random.nextInt(2, maxDivisor + 1)
        val quotient = random.nextInt(2, 12)
        val dividend = divisor * quotient
        val (opts, idx) = numericOptions(quotient, random, spread = 3)
        return GeneratedQuestion(
            textEn = "What is $dividend ÷ $divisor?",
            textMr = "$dividend ÷ $divisor किती आहे?",
            textHi = "$dividend ÷ $divisor कितना है?",
            optionsEn = opts.map { it.toString() },
            optionsMr = opts.map { it.toString() },
            optionsHi = opts.map { it.toString() },
            correctIndex = idx,
            difficulty = "medium"
        )
    }

    fun counting(random: Random, maxCount: Int, emoji: String = "⭐"): GeneratedQuestion {
        val count = random.nextInt(3, maxCount + 1)
        val (opts, idx) = numericOptions(count, random, spread = 3)
        val pictures = emoji.repeat(count)
        return GeneratedQuestion(
            textEn = "How many $emoji are there? $pictures",
            textMr = "किती $emoji आहेत? $pictures",
            textHi = "कितने $emoji हैं? $pictures",
            optionsEn = opts.map { it.toString() },
            optionsMr = opts.map { it.toString() },
            optionsHi = opts.map { it.toString() },
            correctIndex = idx,
            difficulty = "easy"
        )
    }

    fun comparison(random: Random, maxValue: Int): GeneratedQuestion {
        var a = random.nextInt(1, maxValue)
        var b = random.nextInt(1, maxValue)
        while (a == b) b = random.nextInt(1, maxValue)
        val askGreater = random.nextBoolean()
        val correct = if (askGreater) maxOf(a, b) else minOf(a, b)
        val options = listOf(correct, if (correct == a) b else a).shuffled(random).map { it.toString() }
        val correctIndex = options.indexOf(correct.toString())
        return GeneratedQuestion(
            textEn = if (askGreater) "Which number is greater: $a or $b?" else "Which number is smaller: $a or $b?",
            textMr = if (askGreater) "कोणती संख्या मोठी आहे: $a की $b?" else "कोणती संख्या लहान आहे: $a की $b?",
            textHi = if (askGreater) "कौन सी संख्या बड़ी है: $a या $b?" else "कौन सी संख्या छोटी है: $a या $b?",
            optionsEn = options,
            optionsMr = options,
            optionsHi = options,
            correctIndex = correctIndex,
            difficulty = "easy"
        )
    }

    fun placeValue(random: Random, maxValue: Int): GeneratedQuestion {
        val n = random.nextInt(100, maxValue)
        val useTens = random.nextBoolean()
        val correct = if (useTens) (n / 10) % 10 else (n / 100) % 10
        val (opts, idx) = numericOptions(correct, random, spread = 3)
        return GeneratedQuestion(
            textEn = if (useTens) "What is the tens digit in $n?" else "What is the hundreds digit in $n?",
            textMr = if (useTens) "$n मधील दशक स्थानाचा अंक कोणता आहे?" else "$n मधील शतक स्थानाचा अंक कोणता आहे?",
            textHi = if (useTens) "$n में दहाई का अंक कौन सा है?" else "$n में सैकड़ा का अंक कौन सा है?",
            optionsEn = opts.map { it.toString() },
            optionsMr = opts.map { it.toString() },
            optionsHi = opts.map { it.toString() },
            correctIndex = idx,
            difficulty = "medium"
        )
    }

    fun fractionShaded(random: Random): GeneratedQuestion {
        val total = listOf(2, 3, 4, 5).random(random)
        val shaded = random.nextInt(1, total)
        val picture = "🟩".repeat(shaded) + "⬜".repeat(total - shaded)
        val correctLabel = "$shaded/$total"
        val distractors = mutableSetOf<String>()
        while (distractors.size < 3) {
            val ds = random.nextInt(1, total)
            val label = "$ds/$total"
            if (label != correctLabel) distractors.add(label)
        }
        val options = (listOf(correctLabel) + distractors)
        return GeneratedQuestion(
            textEn = "What fraction of the shape is shaded? $picture",
            textMr = "आकृतीचा किती भाग रंगवलेला आहे? $picture",
            textHi = "आकृति का कितना भाग रंगा हुआ है? $picture",
            optionsEn = options,
            optionsMr = options,
            optionsHi = options,
            correctIndex = 0,
            difficulty = "medium"
        )
    }

    fun percentage(random: Random): GeneratedQuestion {
        val n = listOf(50, 100, 150, 200, 400).random(random)
        val p = listOf(10, 20, 25, 50).random(random)
        val correct = n * p / 100
        val (opts, idx) = numericOptions(correct, random, spread = maxOf(5, correct / 4))
        return GeneratedQuestion(
            textEn = "What is $p% of $n?",
            textMr = "$n च्या $p% किती?",
            textHi = "$n का $p% कितना है?",
            optionsEn = opts.map { it.toString() },
            optionsMr = opts.map { it.toString() },
            optionsHi = opts.map { it.toString() },
            correctIndex = idx,
            difficulty = "hard"
        )
    }

    fun ratioSimplify(random: Random): GeneratedQuestion {
        val base = random.nextInt(2, 6)
        val factor = random.nextInt(2, 6)
        val a = base * factor
        val b = (base + random.nextInt(1, 4)) * factor
        fun gcd(x: Int, y: Int): Int = if (y == 0) x else gcd(y, x % y)
        val g = gcd(a, b)
        val correct = "${a / g}:${b / g}"
        val distractors = mutableSetOf<String>()
        while (distractors.size < 3) {
            val da = a / g + random.nextInt(-1, 2)
            val db = b / g + random.nextInt(-1, 2)
            if (da > 0 && db > 0) {
                val label = "$da:$db"
                if (label != correct) distractors.add(label)
            }
        }
        val options = listOf(correct) + distractors
        return GeneratedQuestion(
            textEn = "Simplify the ratio $a:$b",
            textMr = "गुणोत्तर $a:$b सोपे करा",
            textHi = "अनुपात $a:$b को सरल करें",
            optionsEn = options,
            optionsMr = options,
            optionsHi = options,
            correctIndex = 0,
            difficulty = "hard"
        )
    }

    fun linearEquation(random: Random): GeneratedQuestion {
        val x = random.nextInt(1, 20)
        val a = random.nextInt(1, 20)
        val b = x + a
        val (opts, idx) = numericOptions(x, random, spread = 4)
        return GeneratedQuestion(
            textEn = "Solve for x: x + $a = $b",
            textMr = "x साठी सोडवा: x + $a = $b",
            textHi = "x के लिए हल करें: x + $a = $b",
            optionsEn = opts.map { it.toString() },
            optionsMr = opts.map { it.toString() },
            optionsHi = opts.map { it.toString() },
            correctIndex = idx,
            difficulty = "hard"
        )
    }

    fun rectanglePerimeter(random: Random): GeneratedQuestion {
        val l = random.nextInt(3, 20)
        val w = random.nextInt(2, l)
        val correct = 2 * (l + w)
        val (opts, idx) = numericOptions(correct, random, spread = 6)
        return GeneratedQuestion(
            textEn = "Find the perimeter of a rectangle with length $l and width $w.",
            textMr = "लांबी $l आणि रुंदी $w असलेल्या आयताची परिमिती काढा.",
            textHi = "लंबाई $l और चौड़ाई $w वाले आयत की परिधि ज्ञात करें।",
            optionsEn = opts.map { it.toString() },
            optionsMr = opts.map { it.toString() },
            optionsHi = opts.map { it.toString() },
            correctIndex = idx,
            difficulty = "medium"
        )
    }

    fun rectangleArea(random: Random): GeneratedQuestion {
        val l = random.nextInt(3, 15)
        val w = random.nextInt(2, l)
        val correct = l * w
        val (opts, idx) = numericOptions(correct, random, spread = maxOf(6, correct / 5))
        return GeneratedQuestion(
            textEn = "Find the area of a rectangle with length $l and width $w.",
            textMr = "लांबी $l आणि रुंदी $w असलेल्या आयताचे क्षेत्रफळ काढा.",
            textHi = "लंबाई $l और चौड़ाई $w वाले आयत का क्षेत्रफल ज्ञात करें।",
            optionsEn = opts.map { it.toString() },
            optionsMr = opts.map { it.toString() },
            optionsHi = opts.map { it.toString() },
            correctIndex = idx,
            difficulty = "medium"
        )
    }

    fun average(random: Random): GeneratedQuestion {
        val a = random.nextInt(1, 30)
        val b = random.nextInt(1, 30)
        val c = random.nextInt(1, 30)
        val correct = (a + b + c) / 3
        val (opts, idx) = numericOptions(correct, random, spread = 4)
        return GeneratedQuestion(
            textEn = "Find the average of $a, $b, and $c.",
            textMr = "$a, $b, आणि $c ची सरासरी काढा.",
            textHi = "$a, $b, और $c का औसत ज्ञात करें।",
            optionsEn = opts.map { it.toString() },
            optionsMr = opts.map { it.toString() },
            optionsHi = opts.map { it.toString() },
            correctIndex = idx,
            difficulty = "hard"
        )
    }

    fun moneySum(random: Random): GeneratedQuestion {
        val a = random.nextInt(5, 100)
        val b = random.nextInt(5, 100)
        val correct = a + b
        val (opts, idx) = numericOptions(correct, random, spread = 10)
        return GeneratedQuestion(
            textEn = "A pencil costs ₹$a and a book costs ₹$b. What is the total cost?",
            textMr = "पेन्सिलची किंमत ₹$a आणि पुस्तकाची किंमत ₹$b आहे. एकूण किंमत किती?",
            textHi = "एक पेंसिल की कीमत ₹$a है और एक किताब की कीमत ₹$b है। कुल कीमत कितनी है?",
            optionsEn = opts.map { "₹$it" },
            optionsMr = opts.map { "₹$it" },
            optionsHi = opts.map { "₹$it" },
            correctIndex = idx,
            difficulty = "medium"
        )
    }

    private data class Shape(val en: String, val mr: String, val hi: String, val sides: Int)
    private val shapes = listOf(
        Shape("triangle", "त्रिकोण", "त्रिभुज", 3),
        Shape("square", "चौरस", "वर्ग", 4),
        Shape("rectangle", "आयत", "आयत", 4),
        Shape("pentagon", "पंचकोन", "पंचभुज", 5),
        Shape("hexagon", "षटकोन", "षट्भुज", 6)
    )

    fun shapeSides(random: Random): GeneratedQuestion {
        val shape = shapes.random(random)
        val (opts, idx) = numericOptions(shape.sides, random, spread = 2)
        return GeneratedQuestion(
            textEn = "How many sides does a ${shape.en} have?",
            textMr = "${shape.mr}ला किती बाजू असतात?",
            textHi = "${shape.hi} की कितनी भुजाएँ होती हैं?",
            optionsEn = opts.map { it.toString() },
            optionsMr = opts.map { it.toString() },
            optionsHi = opts.map { it.toString() },
            correctIndex = idx,
            difficulty = "easy"
        )
    }
}
