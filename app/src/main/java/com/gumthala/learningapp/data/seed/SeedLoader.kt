package com.gumthala.learningapp.data.seed

import android.content.Context
import com.gumthala.learningapp.core.LocalizedText
import com.gumthala.learningapp.core.PasswordHasher
import com.gumthala.learningapp.core.UserRole
import com.gumthala.learningapp.core.normalizedEmail
import com.gumthala.learningapp.core.normalizedName
import com.gumthala.learningapp.data.local.ChapterEntity
import com.gumthala.learningapp.data.local.ContentDao
import com.gumthala.learningapp.data.local.OptionEntity
import com.gumthala.learningapp.data.local.QuestionEntity
import com.gumthala.learningapp.data.local.SlideDao
import com.gumthala.learningapp.data.local.SubjectEntity
import com.gumthala.learningapp.data.local.UserDao
import com.gumthala.learningapp.data.local.UserEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.json.Json
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * First-run bootstrap: loads bundled content into Room so the app is fully usable
 * with no network and no Firebase project configured.
 */
@Singleton
class SeedLoader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val contentDao: ContentDao,
    private val slideDao: SlideDao,
    private val userDao: UserDao
) {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    suspend fun seedIfNeeded() {
        if (contentDao.subjectCount() == 0) seedContent()
        if (slideDao.defaultDeckCount() == 0) seedSlides()
        if (userDao.adminCount() == 0) seedFounderAdmin()
    }

    private suspend fun seedContent() {
        val files = context.assets.list("seed").orEmpty().filter { it.endsWith(".json") }
        files.forEach { fileName ->
            val raw = context.assets.open("seed/$fileName").bufferedReader().use { it.readText() }
            val seed = runCatching { json.decodeFromString<SeedFile>(raw) }.getOrNull() ?: return@forEach
            applySeedFile(seed)
        }
    }

    private suspend fun applySeedFile(seed: SeedFile) {
        val subjectId = "subject_${seed.subject.code}"
        contentDao.upsertSubjects(
            listOf(
                SubjectEntity(
                    id = subjectId,
                    code = seed.subject.code,
                    name = seed.subject.name,
                    iconKey = seed.subject.iconKey,
                    colorHex = seed.subject.colorHex,
                    orderIndex = seed.subject.orderIndex
                )
            )
        )

        val chapters = mutableListOf<ChapterEntity>()
        val questions = mutableListOf<QuestionEntity>()
        val options = mutableListOf<OptionEntity>()

        seed.classes.forEach { seedClass ->
            seedClass.chapters.forEachIndexed { chapterIndex, chapter ->
                chapters += ChapterEntity(
                    id = chapter.id,
                    subjectId = subjectId,
                    classLevel = seedClass.classLevel,
                    orderIndex = chapterIndex,
                    title = chapter.title,
                    blurb = chapter.blurb ?: LocalizedText(""),
                    iconKey = chapter.iconKey,
                    isSynced = true
                )
                chapter.questions.forEachIndexed { questionIndex, question ->
                    questions += QuestionEntity(
                        id = question.id,
                        chapterId = chapter.id,
                        orderIndex = questionIndex,
                        prompt = question.prompt,
                        imageRef = question.imageRef,
                        hint = question.hint ?: LocalizedText(""),
                        difficulty = question.difficulty,
                        authorUserId = null,
                        isSynced = true
                    )
                    question.options.forEachIndexed { optionIndex, option ->
                        options += OptionEntity(
                            id = "${question.id}_o$optionIndex",
                            questionId = question.id,
                            text = option.text,
                            imageRef = option.imageRef,
                            isCorrect = option.correct,
                            orderIndex = optionIndex
                        )
                    }
                }
            }
        }

        contentDao.upsertChapters(chapters)
        contentDao.upsertQuestions(questions)
        contentDao.upsertOptions(options)
    }

    private suspend fun seedSlides() {
        slideDao.upsertDecks(DefaultSlides.decks())
        slideDao.upsertSlides(DefaultSlides.slides())
    }

    /**
     * Creates the founder admin so the very first launch has someone who can log
     * in and register everyone else. The app must force a password change on
     * first sign-in — see AdminSetup in the UI layer.
     */
    private suspend fun seedFounderAdmin() {
        val salt = PasswordHasher.newSalt()
        userDao.upsert(
            UserEntity(
                id = UUID.randomUUID().toString(),
                role = UserRole.ADMIN,
                fullName = "School Admin",
                fullNameNormalized = "School Admin".normalizedName(),
                email = DEFAULT_ADMIN_EMAIL.normalizedEmail(),
                passwordHash = PasswordHasher.hash(DEFAULT_ADMIN_PASSWORD, salt),
                passwordSalt = salt,
                mustChangePassword = true,
                assignedClasses = "1,2,3,4,5,6,7"
            )
        )
    }

    companion object {
        const val DEFAULT_ADMIN_EMAIL = "educationfreedigital@gmail.com"
        /** Change on first login. */
        const val DEFAULT_ADMIN_PASSWORD = "ChangeMe@123"
    }
}
