package com.gumthala.learningapp.data.repository

import com.gumthala.learningapp.data.local.dao.BadgeDao
import com.gumthala.learningapp.data.local.dao.LeaderboardRow
import com.gumthala.learningapp.data.local.dao.QuizDao
import com.gumthala.learningapp.data.local.dao.StudentStatsDao
import com.gumthala.learningapp.data.local.entity.BadgeEntity
import com.gumthala.learningapp.data.local.entity.QuizAttemptEntity
import com.gumthala.learningapp.data.local.entity.StudentStatsEntity
import com.gumthala.learningapp.domain.quiz.BadgeRules
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID

/** Stars/badges/leaderboard: rolling per-student stats, awarded badges, and per-class rankings. */
class ProgressRepository(
    private val statsDao: StudentStatsDao,
    private val badgeDao: BadgeDao,
    private val quizDao: QuizDao
) {
    fun observeStats(studentId: String) = statsDao.observeForStudent(studentId)

    fun observeBadges(studentId: String) = badgeDao.observeForStudent(studentId)

    fun observeLeaderboard(classLevel: Int): Flow<List<LeaderboardRow>> = statsDao.observeLeaderboard(classLevel)

    fun observeAttempts(studentId: String) = quizDao.observeAttemptsForStudent(studentId)

    fun observeCompletedChapterIds(studentId: String) = quizDao.observeCompletedChapterIds(studentId)

    /** Applies a completed quiz's XP/coins, bumps the daily streak, and awards any newly-earned badges. */
    suspend fun applyQuizCompletion(studentId: String, classLevel: Int, xpEarned: Int, wasPerfectScore: Boolean) {
        val today = LocalDate.now(ZoneId.systemDefault()).toEpochDay()
        val existing = statsDao.getForStudent(studentId)
        val wasFirstEver = existing == null

        val newStreak = when {
            existing == null -> 1
            existing.lastActiveEpochDay == today -> existing.streakDays
            existing.lastActiveEpochDay == today - 1 -> existing.streakDays + 1
            else -> 1
        }

        val updated = (existing ?: StudentStatsEntity(studentId = studentId, classLevel = classLevel)).copy(
            classLevel = classLevel,
            xp = (existing?.xp ?: 0) + xpEarned,
            coins = (existing?.coins ?: 0) + xpEarned / 2,
            streakDays = newStreak,
            lastActiveEpochDay = today,
            lessonsCompleted = (existing?.lessonsCompleted ?: 0) + 1
        )
        statsDao.upsert(updated)

        val earnedBadgeKeys = buildList {
            if (wasFirstEver) add(BadgeRules.FIRST_QUIZ)
            if (wasPerfectScore) add(BadgeRules.PERFECT_SCORE)
            addAll(BadgeRules.badgesForStreak(newStreak))
        }
        earnedBadgeKeys.forEach { key ->
            if (!badgeDao.hasBadge(studentId, key)) {
                badgeDao.insert(
                    BadgeEntity(
                        id = UUID.randomUUID().toString(),
                        studentId = studentId,
                        badgeKey = key,
                        earnedAtMillis = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    suspend fun attemptsForClass(classLevel: Int): List<QuizAttemptEntity> = quizDao.getAttemptsForClass(classLevel)
}
