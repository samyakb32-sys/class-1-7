package com.gumthala.learningapp.data.seed

/** English A-Z and the Devanagari varnamala (शared by Marathi & Hindi) used for phonics chapters and default Teaching Slides. */
object AlphabetData {

    val englishLetters: List<String> = ('A'..'Z').map { it.toString() }

    /** Devanagari vowels (स्वर) — identical script for both Marathi and Hindi. */
    val devanagariVowels: List<String> = listOf(
        "अ", "आ", "इ", "ई", "उ", "ऊ", "ए", "ऐ", "ओ", "औ", "अं", "अः"
    )

    /** Devanagari consonants (व्यंजन). */
    val devanagariConsonants: List<String> = listOf(
        "क", "ख", "ग", "घ", "ङ",
        "च", "छ", "ज", "झ", "ञ",
        "ट", "ठ", "ड", "ढ", "ण",
        "त", "थ", "द", "ध", "न",
        "प", "फ", "ब", "भ", "म",
        "य", "र", "ल", "व",
        "श", "ष", "स", "ह"
    )
}
