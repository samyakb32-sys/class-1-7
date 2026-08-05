package com.gumthala.learningapp.data.repository

import android.content.Context
import com.gumthala.learningapp.data.local.dao.QuizDao
import com.gumthala.learningapp.data.local.entity.QuizAnswerEntity
import com.gumthala.learningapp.data.local.entity.QuizAttemptEntity
import com.gumthala.learningapp.data.remote.NetworkUtils
import com.gumthala.learningapp.data.remote.firebase.FirestoreSyncManager
import com.gumthala.learningapp.domain.quiz.QuizEngine
import com.gumthala.learningapp.domain.quiz.QuizResult
import java.util.UUID

class QuizRepository(
    private val context: Context,
    private val quizDao: QuizDao,
    private val progressRepository: ProgressRepository,
    private val syncManager: FirestoreSyncManager
) {
    suspend fun startAttempt(studentId: String, chapterId: String, totalQuestions: Int): QuizAttemptEntity {
        val attempt = QuizAttemptEntity(
            id = UUID.randomUUID().toString(),
            studentId = studentId,
            chapterId = chapterId,
            startedAtMillis = System.currentTimeMillis(),
            completedAtMillis = null,
            correctCount = 0,
            totalQuestions = totalQuestions,
            starsEarned = 0,
            xpEarned = 0
        )
        quizDao.insertAttempt(attempt)
        return attempt
    }

    /**
     * Finalizes a quiz attempt: scores it, persists per-question answers, updates the
     * student's stars/XP/streak/badges, and (if online) pushes progress to Firestore.
     */
    suspend fun completeAttempt(
        attempt: QuizAttemptEntity,
        classLevel: Int,
        answers: List<QuizAnswerEntity>
    ): QuizResult {
        val correctCount = answers.count { it.wasCorrect }
        val result = QuizEngine.scoreAttempt(correctCount, attempt.totalQuestions)

        val completed = attempt.copy(
            completedAtMillis = System.currentTimeMillis(),
            correctCount = correctCount,
            starsEarned = result.starsEarned,
            xpEarned = result.xpEarned
        )
        quizDao.updateAttempt(completed)
        quizDao.insertAnswers(answers)

        progressRepository.applyQuizCompletion(
            studentId = attempt.studentId,
            classLevel = classLevel,
            xpEarned = result.xpEarned,
            wasPerfectScore = correctCount == attempt.totalQuestions && attempt.totalQuestions > 0
        )

        if (NetworkUtils.isOnline(context)) {
            runCatching { syncManager.pushQuizAttempt(completed, answers) }
                .onSuccess { quizDao.updateAttempt(completed.copy(isSynced = true)) }
        }

        return result
    }
}
