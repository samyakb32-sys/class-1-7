package com.gumthala.learningapp.data.repo

import com.gumthala.learningapp.core.LocalizedText
import com.gumthala.learningapp.data.local.ChapterEntity
import com.gumthala.learningapp.data.local.ContentDao
import com.gumthala.learningapp.data.local.OptionEntity
import com.gumthala.learningapp.data.local.QuestionEntity
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

data class OptionDraft(
    val text: LocalizedText,
    val imageRef: String? = null,
    val isCorrect: Boolean
)

@Singleton
class ContentRepository @Inject constructor(
    private val contentDao: ContentDao
) {
    fun observeSubjects() = contentDao.observeSubjects()

    fun observeChapters(subjectId: String, classLevel: Int) =
        contentDao.observeChapters(subjectId, classLevel)

    fun observeTotalQuestionCount() = contentDao.observeTotalQuestionCount()

    fun observeChapterProgress(userId: String, subjectId: String, classLevel: Int) =
        contentDao.observeChapterProgress(userId, subjectId, classLevel)

    suspend fun chapter(id: String) = contentDao.chapter(id)

    suspend fun questionsWithOptions(chapterId: String) = contentDao.questionsWithOptions(chapterId)

    fun observeTeacherQuestions(teacherId: String) = contentDao.observeQuestionsByAuthor(teacherId)

    /** Teachers add or edit their own questions; admins can edit anything. */
    suspend fun saveQuestion(
        chapterId: String,
        authorUserId: String?,
        prompt: LocalizedText,
        options: List<OptionDraft>,
        imageRef: String? = null,
        hint: LocalizedText = LocalizedText(""),
        difficulty: Int = 3,
        existingQuestionId: String? = null,
        orderIndex: Int? = null
    ): String {
        require(options.count { it.isCorrect } == 1) { "A question needs exactly one correct option." }
        require(options.size >= 2) { "A question needs at least two options." }

        val id = existingQuestionId ?: UUID.randomUUID().toString()
        val question = QuestionEntity(
            id = id,
            chapterId = chapterId,
            orderIndex = orderIndex ?: (contentDao.questionCount(chapterId) + 1),
            prompt = prompt,
            imageRef = imageRef,
            hint = hint,
            difficulty = difficulty,
            authorUserId = authorUserId,
            updatedAt = System.currentTimeMillis(),
            isSynced = false
        )
        val optionEntities = options.mapIndexed { index, draft ->
            OptionEntity(
                id = UUID.randomUUID().toString(),
                questionId = id,
                text = draft.text,
                imageRef = draft.imageRef,
                isCorrect = draft.isCorrect,
                orderIndex = index
            )
        }
        contentDao.replaceQuestion(question, optionEntities)
        return id
    }

    suspend fun deleteQuestion(questionId: String) = contentDao.deleteQuestion(questionId)

    suspend fun saveChapter(chapter: ChapterEntity) =
        contentDao.upsertChapter(chapter.copy(updatedAt = System.currentTimeMillis(), isSynced = false))
}
