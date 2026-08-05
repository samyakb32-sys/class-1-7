package com.gumthala.learningapp.data.seed

/** Generator output before it's wrapped into a QuestionEntity with id/chapterId/orderIndex by the seeder. */
data class GeneratedQuestion(
    val textEn: String,
    val textMr: String,
    val textHi: String,
    val optionsEn: List<String>,
    val optionsMr: List<String>,
    val optionsHi: List<String>,
    val correctIndex: Int,
    val difficulty: String
)
