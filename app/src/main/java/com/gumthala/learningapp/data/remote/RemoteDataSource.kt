package com.gumthala.learningapp.data.remote

import com.gumthala.learningapp.data.local.ChapterEntity
import com.gumthala.learningapp.data.local.OptionEntity
import com.gumthala.learningapp.data.local.ProgressEntity
import com.gumthala.learningapp.data.local.QuestionEntity
import com.gumthala.learningapp.data.local.QuizAttemptEntity
import com.gumthala.learningapp.data.local.UserEntity

/**
 * Optional cloud mirror. Every method is allowed to fail — the app is fully
 * functional when this returns errors or is never called at all.
 */
interface RemoteDataSource {

    suspend fun isAvailable(): Boolean

    suspend fun pushUsers(users: List<UserEntity>): Result<Unit>
    suspend fun pushProgress(progress: List<ProgressEntity>): Result<Unit>
    suspend fun pushAttempts(attempts: List<QuizAttemptEntity>): Result<Unit>
    suspend fun pushContent(
        chapters: List<ChapterEntity>,
        questions: List<QuestionEntity>,
        options: List<OptionEntity>
    ): Result<Unit>

    /** Cloud-first pull; callers fall back to whatever Room already holds. */
    suspend fun pullUsers(since: Long): Result<List<UserEntity>>
    suspend fun pullContent(since: Long): Result<RemoteContent>
}

data class RemoteContent(
    val chapters: List<ChapterEntity>,
    val questions: List<QuestionEntity>,
    val options: List<OptionEntity>
)

/** Used when Firebase is not configured — keeps the app offline-only, no crashes. */
class NoOpRemoteDataSource : RemoteDataSource {
    override suspend fun isAvailable() = false
    override suspend fun pushUsers(users: List<UserEntity>) = Result.success(Unit)
    override suspend fun pushProgress(progress: List<ProgressEntity>) = Result.success(Unit)
    override suspend fun pushAttempts(attempts: List<QuizAttemptEntity>) = Result.success(Unit)
    override suspend fun pushContent(
        chapters: List<ChapterEntity>,
        questions: List<QuestionEntity>,
        options: List<OptionEntity>
    ) = Result.success(Unit)
    override suspend fun pullUsers(since: Long) = Result.success(emptyList<UserEntity>())
    override suspend fun pullContent(since: Long) =
        Result.success(RemoteContent(emptyList(), emptyList(), emptyList()))
}
