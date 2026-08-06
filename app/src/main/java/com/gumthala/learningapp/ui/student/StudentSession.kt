package com.gumthala.learningapp.ui.student

import com.gumthala.learningapp.domain.model.AppLanguage

/** The signed-in student's identity, threaded through the whole student nav graph. */
data class StudentSession(
    val studentId: String,
    val name: String,
    val classLevel: Int,
    val language: AppLanguage
)
