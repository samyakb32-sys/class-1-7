package com.gumthala.learningapp.tv.content

/** Number words and the "say it aloud" clean-up used by the narrator. */
object Words {
    private val ones = arrayOf(
        "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten",
        "eleven", "twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen",
    )
    private val tens = arrayOf("", "", "twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety")

    /** 0..999,999,999 in words ("one hundred twenty three"). Negative numbers get "minus". */
    fun number(n: Int): String {
        if (n < 0) return "minus " + number(-n)
        if (n < 20) return ones[n]
        if (n < 100) return tens[n / 10] + if (n % 10 != 0) " " + ones[n % 10] else ""
        if (n < 1000) return ones[n / 100] + " hundred" + if (n % 100 != 0) " " + number(n % 100) else ""
        if (n < 1_000_000) return number(n / 1000) + " thousand" + if (n % 1000 != 0) " " + number(n % 1000) else ""
        return number(n / 1_000_000) + " million" + if (n % 1_000_000 != 0) " " + number(n % 1_000_000) else ""
    }

    fun ordinal(n: Int): String = when (n) {
        1 -> "first"; 2 -> "second"; 3 -> "third"; 4 -> "fourth"; 5 -> "fifth"
        6 -> "sixth"; 7 -> "seventh"; 8 -> "eighth"; 9 -> "ninth"; 10 -> "tenth"
        else -> "${n}th"
    }

    /** "1,23,456": Indian digit grouping (last three, then pairs). */
    fun indian(n: Long): String {
        val s = n.toString()
        if (s.length <= 3) return s
        val last3 = s.takeLast(3)
        var rest = s.dropLast(3)
        val parts = ArrayList<String>()
        while (rest.length > 2) {
            parts.add(0, rest.takeLast(2))
            rest = rest.dropLast(2)
        }
        if (rest.isNotEmpty()) parts.add(0, rest)
        return parts.joinToString(",") + "," + last3
    }

    private val emoji = Regex("[\\uD83C-\\uDBFF\\uDC00-\\uDFFF\\u2600-\\u27BF\\u2B00-\\u2BFF\\u2764\\uFE0F\\u200D]+")
    private val rupee = Regex("₹\\s?(\\d[\\d,]*)")
    private val fraction = Regex("(\\d+)/(\\d+)")
    private val spaces = Regex("\\s+")

    /** Turns board symbols into words and drops emoji so the voice doesn't read "red apple". */
    fun speakable(text: String): String {
        var s = text
        s = rupee.replace(s) { "${it.groupValues[1]} rupees" }
        s = fraction.replace(s) { "${it.groupValues[1]} over ${it.groupValues[2]}" }
        s = s.replace("+", " plus ")
            .replace("−", " minus ")
            .replace(" - ", " minus ")
            .replace("×", " times ")
            .replace("÷", " divided by ")
            .replace("=", " equals ")
            .replace("%", " percent")
            .replace("°", " degrees")
            .replace("²", " squared")
            .replace("^", " to the power of ")
            .replace("√", " square root of ")
            .replace("³", " cubed")
            .replace(" : ", " to ")
            .replace("π", " pi ")
            .replace("½", " half ")
            .replace("≠", " is not equal to ")
            .replace("<", " is less than ")
            .replace(">", " is more than ")
        s = emoji.replace(s, " ")
        return spaces.replace(s, " ").trim()
    }
}
