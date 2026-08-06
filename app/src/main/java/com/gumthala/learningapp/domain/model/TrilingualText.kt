package com.gumthala.learningapp.domain.model

import androidx.room.ColumnInfo
import com.gumthala.learningapp.domain.model.AppLanguage.ENGLISH
import com.gumthala.learningapp.domain.model.AppLanguage.HINDI
import com.gumthala.learningapp.domain.model.AppLanguage.MARATHI

/** Text available in all three languages the app supports. */
data class TrilingualText(
    @ColumnInfo(name = "en") val en: String,
    @ColumnInfo(name = "mr") val mr: String,
    @ColumnInfo(name = "hi") val hi: String
) {
    fun forLanguage(language: AppLanguage): String = when (language) {
        ENGLISH -> en
        MARATHI -> mr
        HINDI -> hi
    }
}
