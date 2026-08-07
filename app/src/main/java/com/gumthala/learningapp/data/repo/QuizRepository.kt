package com.gumthala.learningapp.data.repo

import com.gumthala.learningapp.core.AppLanguage
import com.gumthala.learningapp.data.local.AttemptAnswerEntity
import com.gumthala.learningapp.data.local.AttemptDao
import com.gumthala.learningapp.data.local.BadgeDao
import com.gumthala.learningapp.data.local.BadgeEntity
import com.gumthala.learningapp.data.local.ContentDao
import com.gumthala.learningapp.data.local.ProgressDao
import com.gumthala.learningapp.data.local.ProgressEntity
import com.gumthala.learningapp.data.local.QuizAttemptEntity
import com.gumthala.learningapp.domain.BadgeCode
import com.gumthala.learningapp.domain.CelebrationTier
import com.gumthala.learningapp.domain.DifficultyLevel
import com.gumthala.learningapp.domain.Quiz
import com.gumthala.learningapp.domain.QuizEngine
import com.gumthala.learningapp.domain.Rewards
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class SubmittedAnswer(
    val questionId: String,
    val selectedOptionId: String?,
    val isCorrect: Boolean
)

data class QuizOutcome(
    val attemptId: String,
    val correctCount: Int,
    val totalCount: Int,
    val starsEarned: Int,
    val isNewBest: Boolean,
    val newBadges: List<BadgeCode>,
    val celebration: CelebrationTier,
    val totalStars: Int
)

@Singleton
class QuizRepository @Inject constructor(
    private val contentDao: ContentDao,
    private val attemptDao: AttemptDao,
    private val progressDao: ProgressDao,
    private val badgeDao: BadgeDao
) {

    /**
     * [difficulty] filters by QuestionEntity.difficulty band before building
     * the quiz. If that filter would leave zero questions (thin content,
     * mismatched band), falls back to the full chapter rather than handing a
     * student an empty quiz — see DifficultyLevel's kdoc for why.
     */
    suspend fun startQuiz(chapterId: String, language: AppLanguage, difficulty: DifficultyLevel? = null): Quiz {
        val rows = contentDao.questionsWithOptions(chapterId)
        val filtered = if (difficulty == null) rows else {
            rows.filter { it.question.difficulty in difficulty.range }.ifEmpty { rows }
        }
        return QuizEngine.build(chapterId, filtered, language)
    }

    /**
     * Writes the attempt, its answers, the rolled-up progress row and any newly
     * earned badges — all locally. Sync is a separate, optional step.
     */
    suspend fun finishQuiz(
        userId: String,
        chapterId: String,
        startedAt: Long,
        answers: List<SubmittedAnswer>
    ): QuizOutcome {
        val now = System.currentTimeMillis()
        val correct = answers.count { it.isCorrect }
        val total = answers.size
        val stars = Rewards.stars(correct, total)
        val attemptId = UUID.randomUUID().toString()

        attemptDao.insertAttempt(
            QuizAttemptEntity(
                id = attemptId,
                userId = userId,
                chapterId = chapterId,
                startedAt = startedAt,
                finishedAt = now,
                correctCount = correct,
                totalCount = total,
                starsEarned = stars,
                isSynced = false
            )
        )
        attemptDao.insertAnswers(
            answers.map {
                AttemptAnswerEntity(
                    id = UUID.randomUUID().toString(),
                    attemptId = attemptId,
                    questionId = it.questionId,
                    selectedOptionId = it.selectedOptionId,
                    isCorrect = it.isCorrect,
                    answeredAt = now
                )
            }
        )

        val existing = progressDao.find(userId, chapterId)
        val isNewBest = existing == null || correct > existing.bestCorrect
        progressDao.upsert(
            ProgressEntity(
                userId = userId,
                chapterId = chapterId,
                bestCorrect = maxOf(existing?.bestCorrect ?: 0, correct),
                bestTotal = maxOf(existing?.bestTotal ?: 0, total),
                stars = maxOf(existing?.stars ?: 0, stars),
                attemptCount = (existing?.attemptCount ?: 0) + 1,
                lastAttemptAt = now,
                isSynced = false
            )
        )

        val totalStars = progressDao.observeTotalStars(userId).first()
        val chaptersCompleted = progressDao.observeChaptersCompleted(userId).first()
        val earned = badgeDao.earnedCodes(userId).toSet()
        val newBadges = BadgeCode.evaluate(
            alreadyEarned = earned,
            totalStars = totalStars,
            chaptersCompleted = chaptersCompleted,
            lastAttemptStars = stars,
            lastAttemptPerfect = total > 0 && correct == total
        )
        newBadges.forEach {
            badgeDao.insert(
                BadgeEntity(id = UUID.randomUUID().toString(), userId = userId, code = it.code, earnedAt = now)
            )
        }

        return QuizOutcome(
            attemptId = attemptId,
            correctCount = correct,
            totalCount = total,
            starsEarned = stars,
            isNewBest = isNewBest,
            newBadges = newBadges,
            celebration = Rewards.celebrationTier(stars),
            totalStars = totalStars
        )
    }

    fun observeTotalStars(userId: String) = progressDao.observeTotalStars(userId)
    fun observeChaptersCompleted(userId: String) = progressDao.observeChaptersCompleted(userId)
    fun observeBadges(userId: String) = badgeDao.observeBadges(userId)
    fun observeLeaderboard(classLevel: Int) = progressDao.observeLeaderboard(classLevel)
    fun observeStudentProgress(classLevels: List<Int>) = progressDao.observeStudentProgress(classLevels)
    fun observeRecentAttempts(userId: String) = attemptDao.observeRecentAttempts(userId)
}
