package com.gumthala.learningapp.data.seed

import com.gumthala.learningapp.core.LocalizedText
import com.gumthala.learningapp.data.local.SlideDeckEntity
import com.gumthala.learningapp.data.local.SlideEntity

/**
 * The teaching slide decks that ship with the app: A–Z, multiplication tables
 * 1–10, and Marathi + English barakhadi. Generated in code so there is no giant
 * JSON to maintain and no network needed on first run.
 */
object DefaultSlides {

    private const val ALPHABET_DECK = "deck_alphabet_en"
    private const val TABLES_DECK_PREFIX = "deck_table_"
    private const val BARAKHADI_MR = "deck_barakhadi_mr"
    private const val BARAKHADI_EN = "deck_barakhadi_en"

    private val alphabetWords = mapOf(
        'A' to Triple("Apple", "सफरचंद", "सेब"),
        'B' to Triple("Ball", "चेंडू", "गेंद"),
        'C' to Triple("Cat", "मांजर", "बिल्ली"),
        'D' to Triple("Drum", "ढोल", "ढोल"),
        'E' to Triple("Elephant", "हत्ती", "हाथी"),
        'F' to Triple("Fish", "मासा", "मछली"),
        'G' to Triple("Goat", "बकरी", "बकरी"),
        'H' to Triple("House", "घर", "घर"),
        'I' to Triple("Ink", "शाई", "स्याही"),
        'J' to Triple("Jug", "सुरई", "सुराही"),
        'K' to Triple("Kite", "पतंग", "पतंग"),
        'L' to Triple("Lion", "सिंह", "शेर"),
        'M' to Triple("Mango", "आंबा", "आम"),
        'N' to Triple("Nest", "घरटे", "घोंसला"),
        'O' to Triple("Owl", "घुबड", "उल्लू"),
        'P' to Triple("Parrot", "पोपट", "तोता"),
        'Q' to Triple("Queen", "राणी", "रानी"),
        'R' to Triple("River", "नदी", "नदी"),
        'S' to Triple("Sun", "सूर्य", "सूरज"),
        'T' to Triple("Tiger", "वाघ", "बाघ"),
        'U' to Triple("Umbrella", "छत्री", "छाता"),
        'V' to Triple("Van", "व्हॅन", "वैन"),
        'W' to Triple("Water", "पाणी", "पानी"),
        'X' to Triple("Xylophone", "झायलोफोन", "जाइलोफोन"),
        'Y' to Triple("Yak", "याक", "याक"),
        'Z' to Triple("Zebra", "झेब्रा", "ज़ेबरा")
    )

    private val marathiConsonants = listOf(
        "क", "ख", "ग", "घ", "च", "छ", "ज", "झ", "ट", "ठ", "ड", "ढ", "ण",
        "त", "थ", "द", "ध", "न", "प", "फ", "ब", "भ", "म", "य", "र", "ल",
        "व", "श", "ष", "स", "ह", "ळ"
    )

    /** matra suffixes for the 12-column barakhadi row */
    private val matras = listOf("", "ा", "ि", "ी", "ु", "ू", "े", "ै", "ो", "ौ", "ं", "ः")

    private val englishBarakhadiConsonants =
        listOf("B", "C", "D", "F", "G", "H", "J", "K", "L", "M", "N", "P", "R", "S", "T", "V", "W", "Y", "Z")

    private val englishVowels = listOf("a", "e", "i", "o", "u")

    fun decks(): List<SlideDeckEntity> = buildList {
        add(
            SlideDeckEntity(
                id = ALPHABET_DECK,
                title = LocalizedText("A to Z Alphabet", "अ ते झ मुळाक्षरे (A–Z)", "A से Z वर्णमाला"),
                category = "alphabet",
                isDefault = true,
                orderIndex = 0
            )
        )
        (1..10).forEach { table ->
            add(
                SlideDeckEntity(
                    id = "$TABLES_DECK_PREFIX$table",
                    title = LocalizedText(
                        "Table of $table",
                        "$table चा पाढा",
                        "$table का पहाड़ा"
                    ),
                    category = "tables",
                    isDefault = true,
                    orderIndex = 10 + table
                )
            )
        }
        add(
            SlideDeckEntity(
                id = BARAKHADI_MR,
                title = LocalizedText("Marathi Barakhadi", "मराठी बाराखडी", "मराठी बारहखड़ी"),
                category = "barakhadi",
                isDefault = true,
                orderIndex = 30
            )
        )
        add(
            SlideDeckEntity(
                id = BARAKHADI_EN,
                title = LocalizedText("English Barakhadi", "इंग्रजी बाराखडी", "अंग्रेज़ी बारहखड़ी"),
                category = "barakhadi",
                isDefault = true,
                orderIndex = 31
            )
        )
    }

    fun slides(): List<SlideEntity> = buildList {
        // A–Z
        alphabetWords.entries.forEachIndexed { index, (letter, words) ->
            val (en, mr, hi) = words
            add(
                SlideEntity(
                    id = "slide_alpha_$letter",
                    deckId = ALPHABET_DECK,
                    orderIndex = index,
                    headline = letter.toString(),
                    caption = LocalizedText("$letter for $en", "$letter — $mr", "$letter — $hi"),
                    imageRef = "images/alphabet/${letter.lowercaseChar()}.webp"
                )
            )
        }
        // Tables 1–10
        (1..10).forEach { table ->
            (1..10).forEach { multiplier ->
                val product = table * multiplier
                add(
                    SlideEntity(
                        id = "slide_table_${table}_$multiplier",
                        deckId = "$TABLES_DECK_PREFIX$table",
                        orderIndex = multiplier - 1,
                        headline = "$table × $multiplier = $product",
                        caption = LocalizedText(
                            "$table times $multiplier is $product",
                            "$table चे $multiplier म्हणजे $product",
                            "$table का $multiplier होता है $product"
                        )
                    )
                )
            }
        }
        // Marathi barakhadi — one slide per consonant, all 12 forms on the slide headline
        marathiConsonants.forEachIndexed { index, consonant ->
            val row = matras.joinToString("  ") { consonant + it }
            add(
                SlideEntity(
                    id = "slide_bara_mr_$index",
                    deckId = BARAKHADI_MR,
                    orderIndex = index,
                    headline = row,
                    caption = LocalizedText(
                        "Barakhadi of $consonant",
                        "$consonant ची बाराखडी",
                        "$consonant की बारहखड़ी"
                    )
                )
            )
        }
        // English barakhadi — consonant + vowel blends
        englishBarakhadiConsonants.forEachIndexed { index, consonant ->
            val row = englishVowels.joinToString("  ") { consonant.lowercase() + it }
            add(
                SlideEntity(
                    id = "slide_bara_en_$index",
                    deckId = BARAKHADI_EN,
                    orderIndex = index,
                    headline = row,
                    caption = LocalizedText(
                        "$consonant with a, e, i, o, u",
                        "$consonant सोबत a, e, i, o, u",
                        "$consonant के साथ a, e, i, o, u"
                    )
                )
            )
        }
    }
}
