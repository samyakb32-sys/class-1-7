package com.gumthala.learningapp.data.remote

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.gumthala.learningapp.data.local.AttemptDao
import com.gumthala.learningapp.data.local.ContentDao
import com.gumthala.learningapp.data.local.ProgressDao
import com.gumthala.learningapp.data.local.UserDao
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

data class SyncReport(
    val skipped: Boolean = false,
    val usersPushed: Int = 0,
    val progressPushed: Int = 0,
    val attemptsPushed: Int = 0,
    val contentPushed: Int = 0,
    val contentPulled: Int = 0,
    val usersPulled: Int = 0,
    val error: Throwable? = null
)

/**
 * Offline-first sync. Room is always the source of truth for reads; this only
 * mirrors outward and merges newer cloud content back in. Nothing here blocks
 * the UI, and every failure degrades silently to local-only.
 */
@Singleton
class SyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remote: RemoteDataSource,
    private val userDao: UserDao,
    private val contentDao: ContentDao,
    private val progressDao: ProgressDao,
    private val attemptDao: AttemptDao,
    private val io: CoroutineDispatcher
) {

    fun isOnline(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /** Called right after a quiz finishes. Safe to call when offline — it no-ops. */
    suspend fun pushProgressNow(): SyncReport = withContext(io) {
        if (!isOnline() || !remote.isAvailable()) return@withContext SyncReport(skipped = true)
        runCatching {
            val progress = progressDao.unsynced()
            val attempts = attemptDao.unsyncedAttempts()

            if (progress.isNotEmpty()) {
                remote.pushProgress(progress).getOrThrow()
                progress.groupBy { it.userId }.forEach { (userId, rows) ->
                    progressDao.markSynced(userId, rows.map { it.chapterId })
                }
            }
            if (attempts.isNotEmpty()) {
                remote.pushAttempts(attempts).getOrThrow()
                attemptDao.markAttemptsSynced(attempts.map { it.id })
            }
            SyncReport(progressPushed = progress.size, attemptsPushed = attempts.size)
        }.getOrElse { SyncReport(error = it) }
    }

    /** Admin content push: local edits (chapters, questions, options) go up. */
    suspend fun pushContent(): SyncReport = withContext(io) {
        if (!isOnline() || !remote.isAvailable()) return@withContext SyncReport(skipped = true)
        runCatching {
            val chapters = contentDao.unsyncedChapters()
            val questions = contentDao.unsyncedQuestions()
            val options = contentDao.optionsFor(questions.map { it.id })
            if (chapters.isEmpty() && questions.isEmpty()) return@runCatching SyncReport()

            remote.pushContent(chapters, questions, options).getOrThrow()
            contentDao.markChaptersSynced(chapters.map { it.id })
            contentDao.markQuestionsSynced(questions.map { it.id })
            SyncReport(contentPushed = chapters.size + questions.size)
        }.getOrElse { SyncReport(error = it) }
    }

    /** Admin roster push. */
    suspend fun pushUsers(): SyncReport = withContext(io) {
        if (!isOnline() || !remote.isAvailable()) return@withContext SyncReport(skipped = true)
        runCatching {
            val users = userDao.unsynced()
            if (users.isEmpty()) return@runCatching SyncReport()
            remote.pushUsers(users).getOrThrow()
            userDao.markSynced(users.map { it.id })
            SyncReport(usersPushed = users.size)
        }.getOrElse { SyncReport(error = it) }
    }

    /**
     * Cloud-first pull with local fallback: if the network call fails, Room keeps
     * serving what it already has and the caller sees no error state.
     */
    suspend fun pullAll(since: Long = 0L): SyncReport = withContext(io) {
        if (!isOnline() || !remote.isAvailable()) return@withContext SyncReport(skipped = true)
        runCatching {
            val users = remote.pullUsers(since).getOrElse { emptyList() }
            if (users.isNotEmpty()) userDao.upsertAll(users.map { it.copy(isSynced = true) })

            val content = remote.pullContent(since).getOrElse {
                RemoteContent(emptyList(), emptyList(), emptyList())
            }
            if (content.chapters.isNotEmpty()) {
                contentDao.upsertChapters(content.chapters.map { it.copy(isSynced = true) })
            }
            if (content.questions.isNotEmpty()) {
                contentDao.upsertQuestions(content.questions.map { it.copy(isSynced = true) })
                contentDao.upsertOptions(content.options)
            }
            SyncReport(
                usersPulled = users.size,
                contentPulled = content.chapters.size + content.questions.size
            )
        }.getOrElse { SyncReport(error = it) }
    }

    suspend fun fullSync(): SyncReport {
        pushUsers(); pushContent(); pushProgressNow()
        return pullAll()
    }
}
