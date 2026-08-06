package com.gumthala.learningapp.core

import androidx.room.ColumnInfo
import kotlinx.serialization.Serializable

/** The three content languages the app ships in. */
enum class AppLanguage(val code: String, val nativeLabel: String) {
    ENGLISH("en", "English"),
    MARATHI("mr", "मराठी"),
    HINDI("hi", "हिंदी");

    companion object {
        fun fromCode(code: String?): AppLanguage =
            entries.firstOrNull { it.code == code } ?: ENGLISH
    }
}

enum class UserRole { STUDENT, TEACHER, ADMIN }

/**
 * Trilingual string. Embedded into Room entities with a column prefix so a single
 * row carries all three languages — nothing needs the network to switch language.
 * Marathi/Hindi fall back to English when a translation is missing.
 */
@Serializable
data class LocalizedText(
    @ColumnInfo(name = "en") val en: String,
    @ColumnInfo(name = "mr") val mr: String? = null,
    @ColumnInfo(name = "hi") val hi: String? = null
) {
    fun get(language: AppLanguage): String = when (language) {
        AppLanguage.ENGLISH -> en
        AppLanguage.MARATHI -> mr?.takeIf { it.isNotBlank() } ?: en
        AppLanguage.HINDI -> hi?.takeIf { it.isNotBlank() } ?: en
    }
}

/** Classes 1..7. */
object ClassLevels {
    const val MIN = 1
    const val MAX = 7
    val ALL: List<Int> = (MIN..MAX).toList()
    fun isValid(level: Int) = level in MIN..MAX
}

object SubjectCodes {
    const val MATHS = "maths"
    const val ENGLISH = "english"
    const val MARATHI = "marathi"
    const val HINDI = "hindi"
    val ALL = listOf(MATHS, ENGLISH, MARATHI, HINDI)
}

/** Contact used by the Help & Support screen for password recovery. */
object SupportContact {
    const val EMAIL = "educationfreedigital@gmail.com"
}
