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

/** Why a sync didn't run, when it didn't. */
enum class SyncSkipReason {
    /** Device has no validated internet connection. */
    NO_NETWORK,
    /** Firebase isn't configured in this build (no google-services.json). */
    NOT_CONFIGURED,
    /** Online and configured, but Firestore couldn't be reached or refused us. */
    UNREACHABLE
}

data class SyncReport(
    val skipped: Boolean = false,
    val skipReason: SyncSkipReason? = null,
    val usersPushed: Int = 0,
    val progressPushed: Int = 0,
    val attemptsPushed: Int = 0,
    val contentPushed: Int = 0,
    val contentPulled: Int = 0,
    val usersPulled: Int = 0,
    val error: Throwable? = null
) {
    /** True when nothing went wrong — used by the UI to decide the message. */
    val isSuccess: Boolean get() = !skipped && error == null
}

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

    /**
     * Checks the three things sync needs, in order, and says which one failed.
     * Previously each entry point did `if (!isOnline() || !remote.isAvailable())`
     * and reported a bare "skipped", so a Firestore rules rejection was
     * indistinguishable from having no signal — the app said "Offline" on a
     * perfectly good connection and there was no way to tell from the UI.
     */
    private suspend fun checkPreconditions(): SyncSkipReason? = when {
        !isOnline() -> SyncSkipReason.NO_NETWORK
        remote is NoOpRemoteDataSource -> SyncSkipReason.NOT_CONFIGURED
        !remote.isAvailable() -> SyncSkipReason.UNREACHABLE
        else -> null
    }

    /** Called right after a quiz finishes. Safe to call when offline — it no-ops. */
    suspend fun pushProgressNow(): SyncReport = withContext(io) {
        checkPreconditions()?.let { return@withContext SyncReport(skipped = true, skipReason = it) }
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
        checkPreconditions()?.let { return@withContext SyncReport(skipped = true, skipReason = it) }
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
        checkPreconditions()?.let { return@withContext SyncReport(skipped = true, skipReason = it) }
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
        checkPreconditions()?.let { return@withContext SyncReport(skipped = true, skipReason = it) }
        runCatching {
            val users = remote.pullUsers(since).getOrElse { emptyList() }
            if (users.isNotEmpty()) {
                // Credentials are never synced, so every pulled row has a null
                // passwordHash. Writing that straight to Room would wipe the local
                // hash and lock a teacher out of their own device on the next sync.
                // Carry the local credentials forward instead.
                val merged = users.map { incoming ->
                    val local = userDao.findById(incoming.id)
                    incoming.copy(
                        passwordHash = local?.passwordHash,
                        passwordSalt = local?.passwordSalt,
                        isSynced = true
                    )
                }
                userDao.upsertAll(merged)
            }

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

    /**
     * Runs every stage and aggregates the outcome. The old version discarded the
     * three push results and returned only pullAll()'s, so a push that failed
     * outright still surfaced as "Synced ✓" in the UI.
     */
    suspend fun fullSync(): SyncReport {
        val stages = listOf(pushUsers(), pushContent(), pushProgressNow(), pullAll())

        stages.firstOrNull { it.error != null }?.let { failed ->
            return SyncReport(error = failed.error, skipReason = failed.skipReason)
        }
        stages.firstOrNull { it.skipped }?.let { skipped ->
            return SyncReport(skipped = true, skipReason = skipped.skipReason)
        }
        return SyncReport(
            usersPushed = stages.sumOf { it.usersPushed },
            progressPushed = stages.sumOf { it.progressPushed },
            attemptsPushed = stages.sumOf { it.attemptsPushed },
            contentPushed = stages.sumOf { it.contentPushed },
            contentPulled = stages.sumOf { it.contentPulled },
            usersPulled = stages.sumOf { it.usersPulled }
        )
    }
}
