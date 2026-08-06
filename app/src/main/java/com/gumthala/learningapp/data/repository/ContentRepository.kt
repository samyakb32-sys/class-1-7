package com.gumthala.learningapp.data.repository

import android.content.Context
import com.gumthala.learningapp.data.local.dao.ChapterDao
import com.gumthala.learningapp.data.local.dao.QuestionDao
import com.gumthala.learningapp.data.local.dao.SubjectDao
import com.gumthala.learningapp.data.local.entity.ChapterEntity
import com.gumthala.learningapp.data.local.entity.QuestionEntity
import com.gumthala.learningapp.data.local.entity.SubjectEntity
import com.gumthala.learningapp.data.remote.NetworkUtils
import com.gumthala.learningapp.data.remote.firebase.FirestoreSyncManager
import kotlinx.coroutines.flow.Flow
import java.util.UUID

/**
 * Offline-first content access: Room is always read from directly (instant, works offline).
 * Writes go to Room first, then best-effort push to Firestore when online so Admin/Teacher
 * content edits propagate to other devices next time they sync.
 */
class ContentRepository(
    private val context: Context,
    private val subjectDao: SubjectDao,
    private val chapterDao: ChapterDao,
    private val questionDao: QuestionDao,
    private val syncManager: FirestoreSyncManager
) {
    fun observeSubjects(classLevel: Int): Flow<List<SubjectEntity>> = subjectDao.observeForClass(classLevel)

    fun observeChapters(subjectId: String): Flow<List<ChapterEntity>> = chapterDao.observeForSubject(subjectId)

    fun observeQuestions(chapterId: String): Flow<List<QuestionEntity>> = questionDao.observeForChapter(chapterId)

    fun observeTeacherQuestions(teacherId: String): Flow<List<QuestionEntity>> = questionDao.observeByTeacher(teacherId)

    suspend fun getChapter(chapterId: String): ChapterEntity? = chapterDao.getById(chapterId)

    fun observeChapter(chapterId: String): Flow<ChapterEntity?> = chapterDao.observeById(chapterId)

    suspend fun getSubject(subjectId: String): SubjectEntity? = subjectDao.getById(subjectId)

    /** Cloud-first pull for a chapter's questions, falling back to whatever's cached locally. */
    suspend fun refreshQuestionsFromCloud(chapterId: String) {
        if (!NetworkUtils.isOnline(context)) return
        runCatching { syncManager.pullQuestionsForChapter(chapterId) }
            .onSuccess { cloudQuestions -> if (cloudQuestions.isNotEmpty()) questionDao.upsertAll(cloudQuestions) }
        // On failure, Room already holds the last-known-good local copy — nothing to do.
    }

    /** Teacher or Admin authoring their own quiz question. */
    suspend fun saveQuestion(question: QuestionEntity) {
        questionDao.upsert(question)
        if (NetworkUtils.isOnline(context)) {
            runCatching { syncManager.pushQuestion(question) }
        }
    }

    suspend fun deleteQuestion(questionId: String) = questionDao.deleteById(questionId)

    suspend fun newQuestionId(): String = UUID.randomUUID().toString()

    suspend fun seedSubjectsIfEmpty(subjects: List<SubjectEntity>) {
        if (subjectDao.count() == 0) subjectDao.upsertAll(subjects)
    }

    suspend fun seedChaptersIfEmpty(chapters: List<ChapterEntity>) {
        if (chapterDao.count() == 0) chapterDao.upsertAll(chapters)
    }

    suspend fun seedQuestionsIfEmpty(questions: List<QuestionEntity>) {
        if (questionDao.count() == 0) questionDao.upsertAll(questions)
    }
}
