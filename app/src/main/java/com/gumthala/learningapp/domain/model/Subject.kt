package com.gumthala.learningapp.domain.model

enum class Subject(val subjectId: String) {
    MATHS("maths"),
    ENGLISH("english"),
    MARATHI("marathi"),
    HINDI("hindi");

    companion object {
        fun fromId(id: String): Subject = entries.first { it.subjectId == id }
    }
}

/** Classes 1 through 7. */
data class ClassLevel(val level: Int) {
    init {
        require(level in 1..7) { "Class level must be between 1 and 7, was $level" }
    }

    companion object {
        val ALL: List<ClassLevel> = (1..7).map { ClassLevel(it) }
    }
}
