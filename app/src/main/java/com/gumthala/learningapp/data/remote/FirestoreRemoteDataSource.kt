package com.gumthala.learningapp.data.remote

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import com.gumthala.learningapp.core.LocalizedText
import com.gumthala.learningapp.core.UserRole
import com.gumthala.learningapp.data.local.ChapterEntity
import com.gumthala.learningapp.data.local.OptionEntity
import com.gumthala.learningapp.data.local.ProgressEntity
import com.gumthala.learningapp.data.local.QuestionEntity
import com.gumthala.learningapp.data.local.QuizAttemptEntity
import com.gumthala.learningapp.data.local.UserEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Firestore mirror. [schoolId] scopes every document so more than one Z.P. school
 * can share a project later without a migration.
 */
class FirestoreRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val schoolId: String
) : RemoteDataSource {

    private fun school() = firestore.collection(RemotePaths.SCHOOLS).document(schoolId)

    private companion object {
        const val TAG = "FirestoreSync"
    }

    /**
     * Reachability probe.
     *
     * Deliberately probes a SUBCOLLECTION rather than the school document
     * itself: nothing in this app ever writes `schools/{schoolId}` as a
     * document (only its subcollections), so that path may not exist, and its
     * read permission is easy to forget when writing rules — which is exactly
     * what made this report "Offline" on a perfectly good connection before.
     * A limit(1) query against `users` needs the same permission real syncing
     * needs, so if this passes, syncing will actually work.
     *
     * Failures are logged with the real Firestore reason (PERMISSION_DENIED,
     * UNAVAILABLE, etc.) instead of being silently swallowed — without this,
     * a rules problem and a dead network were indistinguishable.
     */
    override suspend fun isAvailable(): Boolean = runCatching {
        school().collection(RemotePaths.USERS).limit(1).get(Source.SERVER).await()
        true
    }.getOrElse { e ->
        Log.w(TAG, "Firestore unreachable: ${e::class.java.simpleName}: ${e.message}", e)
        false
    }

    override suspend fun pushUsers(users: List<UserEntity>): Result<Unit> = batched(users) { batch, user ->
        batch.set(school().collection(RemotePaths.USERS).document(user.id), user.toMap())
    }

    override suspend fun pushProgress(progress: List<ProgressEntity>): Result<Unit> =
        batched(progress) { batch, row ->
            val id = "${row.userId}_${row.chapterId}"
            batch.set(school().collection(RemotePaths.PROGRESS).document(id), row.toMap())
        }

    override suspend fun pushAttempts(attempts: List<QuizAttemptEntity>): Result<Unit> =
        batched(attempts) { batch, attempt ->
            batch.set(school().collection(RemotePaths.ATTEMPTS).document(attempt.id), attempt.toMap())
        }

    override suspend fun pushContent(
        chapters: List<ChapterEntity>,
        questions: List<QuestionEntity>,
        options: List<OptionEntity>
    ): Result<Unit> = runCatching {
        val optionsByQuestion = options.groupBy { it.questionId }
        chapters.chunked(400).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { batch.set(school().collection(RemotePaths.CHAPTERS).document(it.id), it.toMap()) }
            batch.commit().await()
        }
        questions.chunked(400).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { question ->
                val map = question.toMap() +
                    mapOf("options" to optionsByQuestion[question.id].orEmpty().map { it.toMap() })
                batch.set(school().collection(RemotePaths.QUESTIONS).document(question.id), map)
            }
            batch.commit().await()
        }
    }

    override suspend fun pullUsers(since: Long): Result<List<UserEntity>> = runCatching {
        school().collection(RemotePaths.USERS)
            .whereGreaterThan("updatedAt", since)
            .get(Source.SERVER).await()
            .documents.mapNotNull { it.data?.toUserEntity() }
    }

    override suspend fun pullContent(since: Long): Result<RemoteContent> = runCatching {
        val chapters = school().collection(RemotePaths.CHAPTERS)
            .whereGreaterThan("updatedAt", since)
            .get(Source.SERVER).await()
            .documents.mapNotNull { it.data?.toChapterEntity() }

        val questionDocs = school().collection(RemotePaths.QUESTIONS)
            .whereGreaterThan("updatedAt", since)
            .get(Source.SERVER).await().documents

        val questions = questionDocs.mapNotNull { it.data?.toQuestionEntity() }
        val options = questionDocs.flatMap { doc ->
            val data = doc.data ?: return@flatMap emptyList()
            @Suppress("UNCHECKED_CAST")
            val raw = data["options"] as? List<Map<String, Any?>> ?: emptyList()
            raw.mapNotNull { it.toOptionEntity() }
        }
        RemoteContent(chapters, questions, options)
    }

    private suspend fun <T> batched(
        items: List<T>,
        write: (com.google.firebase.firestore.WriteBatch, T) -> Unit
    ): Result<Unit> = runCatching {
        items.chunked(400).forEach { chunk ->
            val batch = firestore.batch()
            chunk.forEach { write(batch, it) }
            batch.commit().await()
        }
    }
}

// ---- mappers -------------------------------------------------------------

private fun LocalizedText.toMap() = mapOf("en" to en, "mr" to mr, "hi" to hi)

private fun Map<String, Any?>.localized(key: String): LocalizedText {
    @Suppress("UNCHECKED_CAST")
    val raw = this[key] as? Map<String, Any?> ?: return LocalizedText("")
    return LocalizedText(
        en = raw["en"] as? String ?: "",
        mr = raw["mr"] as? String,
        hi = raw["hi"] as? String
    )
}

private fun Map<String, Any?>.long(key: String): Long = (this[key] as? Number)?.toLong() ?: 0L
private fun Map<String, Any?>.int(key: String): Int = (this[key] as? Number)?.toInt() ?: 0
private fun Map<String, Any?>.intOrNull(key: String): Int? = (this[key] as? Number)?.toInt()
private fun Map<String, Any?>.bool(key: String, default: Boolean = false): Boolean =
    this[key] as? Boolean ?: default
private fun Map<String, Any?>.str(key: String): String? = this[key] as? String

/**
 * NOTE: passwordHash / passwordSalt are deliberately NOT uploaded.
 *
 * This app authenticates locally, not via Firebase Auth, so Firestore rules
 * can't scope reads per-user — any client holding the app's config could read
 * whatever is in the database. Staff credential hashes are the one thing in
 * here worth stealing, so they never leave the device.
 *
 * Consequence: students (name + class, no password) sync across devices
 * perfectly. A teacher/admin signing in on a NEW device needs an admin to set
 * their password on that device first. That's a deliberate trade — see the
 * Firebase section in README.md.
 */
private fun UserEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id, "role" to role.name, "fullName" to fullName,
    "fullNameNormalized" to fullNameNormalized, "email" to email,
    "mustChangePassword" to mustChangePassword,
    "classLevel" to classLevel, "rollNo" to rollNo, "assignedClasses" to assignedClasses,
    "avatarKey" to avatarKey, "isActive" to isActive, "createdBy" to createdBy,
    "createdAt" to createdAt, "updatedAt" to updatedAt
)

private fun Map<String, Any?>.toUserEntity(): UserEntity? {
    val id = str("id") ?: return null
    val role = runCatching { UserRole.valueOf(str("role") ?: "") }.getOrNull() ?: return null
    return UserEntity(
        id = id, role = role,
        fullName = str("fullName").orEmpty(),
        fullNameNormalized = str("fullNameNormalized").orEmpty(),
        // credentials are never synced (see UserEntity.toMap above) — SyncManager
        // restores the local hash before this row is written to Room
        email = str("email"), passwordHash = null, passwordSalt = null,
        mustChangePassword = bool("mustChangePassword", false),
        classLevel = intOrNull("classLevel"), rollNo = str("rollNo"),
        assignedClasses = str("assignedClasses"), avatarKey = str("avatarKey"),
        isActive = bool("isActive", true), createdBy = str("createdBy"),
        createdAt = long("createdAt"), updatedAt = long("updatedAt"), isSynced = true
    )
}

private fun ChapterEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id, "subjectId" to subjectId, "classLevel" to classLevel, "orderIndex" to orderIndex,
    "title" to title.toMap(), "blurb" to blurb.toMap(), "iconKey" to iconKey,
    "isPublished" to isPublished, "updatedAt" to updatedAt
)

private fun Map<String, Any?>.toChapterEntity(): ChapterEntity? {
    val id = str("id") ?: return null
    return ChapterEntity(
        id = id, subjectId = str("subjectId").orEmpty(), classLevel = int("classLevel"),
        orderIndex = int("orderIndex"), title = localized("title"), blurb = localized("blurb"),
        iconKey = str("iconKey"), isPublished = bool("isPublished", true),
        updatedAt = long("updatedAt"), isSynced = true
    )
}

private fun QuestionEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id, "chapterId" to chapterId, "orderIndex" to orderIndex,
    "prompt" to prompt.toMap(), "imageRef" to imageRef, "hint" to hint.toMap(),
    "difficulty" to difficulty, "authorUserId" to authorUserId,
    "isActive" to isActive, "updatedAt" to updatedAt
)

private fun Map<String, Any?>.toQuestionEntity(): QuestionEntity? {
    val id = str("id") ?: return null
    return QuestionEntity(
        id = id, chapterId = str("chapterId").orEmpty(), orderIndex = int("orderIndex"),
        prompt = localized("prompt"), imageRef = str("imageRef"), hint = localized("hint"),
        difficulty = int("difficulty").takeIf { it > 0 } ?: 3,
        authorUserId = str("authorUserId"), isActive = bool("isActive", true),
        updatedAt = long("updatedAt"), isSynced = true
    )
}

private fun OptionEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id, "questionId" to questionId, "text" to text.toMap(),
    "imageRef" to imageRef, "isCorrect" to isCorrect, "orderIndex" to orderIndex
)

private fun Map<String, Any?>.toOptionEntity(): OptionEntity? {
    val id = str("id") ?: return null
    return OptionEntity(
        id = id, questionId = str("questionId").orEmpty(), text = localized("text"),
        imageRef = str("imageRef"), isCorrect = bool("isCorrect"), orderIndex = int("orderIndex")
    )
}

private fun ProgressEntity.toMap(): Map<String, Any?> = mapOf(
    "userId" to userId, "chapterId" to chapterId, "bestCorrect" to bestCorrect,
    "bestTotal" to bestTotal, "stars" to stars, "attemptCount" to attemptCount,
    "lastAttemptAt" to lastAttemptAt
)

private fun QuizAttemptEntity.toMap(): Map<String, Any?> = mapOf(
    "id" to id, "userId" to userId, "chapterId" to chapterId, "startedAt" to startedAt,
    "finishedAt" to finishedAt, "correctCount" to correctCount, "totalCount" to totalCount,
    "starsEarned" to starsEarned
)
