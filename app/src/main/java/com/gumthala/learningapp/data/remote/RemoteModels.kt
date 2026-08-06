package com.gumthala.learningapp.data.remote

/**
 * Firestore document shapes. Deliberately flat maps — Firestore is a mirror of
 * Room, never the source of truth, so we avoid coupling it to Room entities.
 *
 * Collections:
 *   schools/{schoolId}/users/{userId}
 *   schools/{schoolId}/chapters/{chapterId}
 *   schools/{schoolId}/questions/{questionId}      (options nested in the doc)
 *   schools/{schoolId}/progress/{userId}_{chapterId}
 */
object RemotePaths {
    const val SCHOOLS = "schools"
    const val USERS = "users"
    const val CHAPTERS = "chapters"
    const val QUESTIONS = "questions"
    const val PROGRESS = "progress"
    const val ATTEMPTS = "attempts"
}

data class RemoteResult<out T>(
    val data: T?,
    val fromCache: Boolean,
    val error: Throwable? = null
) {
    val isSuccess: Boolean get() = error == null
}
