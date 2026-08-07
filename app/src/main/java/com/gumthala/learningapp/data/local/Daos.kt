package com.gumthala.learningapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.gumthala.learningapp.core.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Upsert
    suspend fun upsert(user: UserEntity)

    @Upsert
    suspend fun upsertAll(users: List<UserEntity>)

    @Update
    suspend fun update(user: UserEntity)

    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun findById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id")
    fun observeById(id: String): Flow<UserEntity?>

    /** Student sign-in: name + class must already exist and be active. */
    @Query(
        """
        SELECT * FROM users
        WHERE role = 'STUDENT'
          AND fullNameNormalized = :normalizedName
          AND classLevel = :classLevel
          AND isActive = 1
        LIMIT 1
        """
    )
    suspend fun findStudent(normalizedName: String, classLevel: Int): UserEntity?

    @Query("SELECT * FROM users WHERE email = :email AND role IN ('TEACHER','ADMIN') AND isActive = 1 LIMIT 1")
    suspend fun findStaffByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE role = 'STUDENT' AND classLevel = :classLevel AND isActive = 1 ORDER BY fullName")
    fun observeStudentsInClass(classLevel: Int): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = :role ORDER BY fullName")
    fun observeByRole(role: UserRole): Flow<List<UserEntity>>

    @Query("SELECT COUNT(*) FROM users WHERE role = 'ADMIN'")
    suspend fun adminCount(): Int

    @Query("UPDATE users SET isActive = :active, updatedAt = :now, isSynced = 0 WHERE id = :id")
    suspend fun setActive(id: String, active: Boolean, now: Long = System.currentTimeMillis())

    @Query("UPDATE users SET passwordHash = :hash, passwordSalt = :salt, mustChangePassword = :mustChange, updatedAt = :now, isSynced = 0 WHERE id = :id")
    suspend fun setPassword(id: String, hash: String, salt: String, mustChange: Boolean, now: Long = System.currentTimeMillis())

    @Query("SELECT * FROM users WHERE isSynced = 0")
    suspend fun unsynced(): List<UserEntity>

    @Query("UPDATE users SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markSynced(ids: List<String>)
}

@Dao
interface ContentDao {

    @Upsert suspend fun upsertSubjects(items: List<SubjectEntity>)
    @Upsert suspend fun upsertChapters(items: List<ChapterEntity>)
    @Upsert suspend fun upsertQuestions(items: List<QuestionEntity>)
    @Upsert suspend fun upsertOptions(items: List<OptionEntity>)

    @Upsert suspend fun upsertQuestion(item: QuestionEntity)
    @Upsert suspend fun upsertChapter(item: ChapterEntity)

    @Query("SELECT * FROM subjects ORDER BY orderIndex")
    fun observeSubjects(): Flow<List<SubjectEntity>>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun subject(id: String): SubjectEntity?

    @Query("SELECT COUNT(*) FROM subjects")
    suspend fun subjectCount(): Int

    @Query(
        """
        SELECT * FROM chapters
        WHERE subjectId = :subjectId AND classLevel = :classLevel AND isPublished = 1
        ORDER BY orderIndex
        """
    )
    fun observeChapters(subjectId: String, classLevel: Int): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE id = :id")
    suspend fun chapter(id: String): ChapterEntity?

    @Transaction
    @Query("SELECT * FROM questions WHERE chapterId = :chapterId AND isActive = 1 ORDER BY orderIndex")
    suspend fun questionsWithOptions(chapterId: String): List<QuestionWithOptions>

    @Transaction
    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun questionWithOptions(id: String): QuestionWithOptions?

    @Query("SELECT COUNT(*) FROM questions WHERE chapterId = :chapterId AND isActive = 1")
    suspend fun questionCount(chapterId: String): Int

    @Query("SELECT COUNT(*) FROM questions WHERE isActive = 1")
    fun observeTotalQuestionCount(): Flow<Int>

    @Query("SELECT * FROM questions WHERE authorUserId = :teacherId ORDER BY updatedAt DESC")
    fun observeQuestionsByAuthor(teacherId: String): Flow<List<QuestionEntity>>

    @Query("DELETE FROM questions WHERE id = :questionId")
    suspend fun deleteQuestion(questionId: String)

    @Query("DELETE FROM options WHERE questionId = :questionId")
    suspend fun deleteOptionsFor(questionId: String)

    @Transaction
    suspend fun replaceQuestion(question: QuestionEntity, options: List<OptionEntity>) {
        upsertQuestion(question)
        deleteOptionsFor(question.id)
        upsertOptions(options)
    }

    @Query(
        """
        SELECT c.*,
               (SELECT COUNT(*) FROM questions q WHERE q.chapterId = c.id AND q.isActive = 1) AS questionCount,
               COALESCE(p.stars, 0)        AS stars,
               COALESCE(p.bestCorrect, 0)  AS bestCorrect,
               COALESCE(p.bestTotal, 0)    AS bestTotal,
               COALESCE(p.attemptCount, 0) AS attemptCount
        FROM chapters c
        LEFT JOIN progress p ON p.chapterId = c.id AND p.userId = :userId
        WHERE c.subjectId = :subjectId AND c.classLevel = :classLevel AND c.isPublished = 1
        ORDER BY c.orderIndex
        """
    )
    fun observeChapterProgress(userId: String, subjectId: String, classLevel: Int): Flow<List<ChapterProgressRow>>

    @Query("SELECT * FROM chapters WHERE isSynced = 0")
    suspend fun unsyncedChapters(): List<ChapterEntity>

    @Query("SELECT * FROM questions WHERE isSynced = 0")
    suspend fun unsyncedQuestions(): List<QuestionEntity>

    @Query("SELECT * FROM options WHERE questionId IN (:questionIds)")
    suspend fun optionsFor(questionIds: List<String>): List<OptionEntity>

    @Query("UPDATE chapters SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markChaptersSynced(ids: List<String>)

    @Query("UPDATE questions SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markQuestionsSynced(ids: List<String>)
}

@Dao
interface AttemptDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuizAttemptEntity)

    @Update
    suspend fun updateAttempt(attempt: QuizAttemptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<AttemptAnswerEntity>)

    @Query("SELECT * FROM quiz_attempts WHERE id = :id")
    suspend fun attempt(id: String): QuizAttemptEntity?

    @Query("SELECT * FROM quiz_attempts WHERE userId = :userId ORDER BY startedAt DESC LIMIT :limit")
    fun observeRecentAttempts(userId: String, limit: Int = 20): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM quiz_attempts WHERE isSynced = 0 AND finishedAt IS NOT NULL")
    suspend fun unsyncedAttempts(): List<QuizAttemptEntity>

    @Query("UPDATE quiz_attempts SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markAttemptsSynced(ids: List<String>)
}

@Dao
interface ProgressDao {

    @Upsert
    suspend fun upsert(progress: ProgressEntity)

    @Query("SELECT * FROM progress WHERE userId = :userId AND chapterId = :chapterId")
    suspend fun find(userId: String, chapterId: String): ProgressEntity?

    @Query("SELECT COALESCE(SUM(stars), 0) FROM progress WHERE userId = :userId")
    fun observeTotalStars(userId: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM progress WHERE userId = :userId AND stars > 0")
    fun observeChaptersCompleted(userId: String): Flow<Int>

    @Query(
        """
        SELECT u.id AS userId,
               u.fullName AS fullName,
               u.avatarKey AS avatarKey,
               u.classLevel AS classLevel,
               COALESCE(SUM(p.stars), 0) AS totalStars,
               COALESCE(SUM(CASE WHEN p.stars > 0 THEN 1 ELSE 0 END), 0) AS chaptersCompleted
        FROM users u
        LEFT JOIN progress p ON p.userId = u.id
        WHERE u.role = 'STUDENT' AND u.classLevel = :classLevel AND u.isActive = 1
        GROUP BY u.id
        ORDER BY totalStars DESC, chaptersCompleted DESC, u.fullName ASC
        """
    )
    fun observeLeaderboard(classLevel: Int): Flow<List<LeaderboardRow>>

    @Query(
        """
        SELECT u.id AS userId,
               u.fullName AS fullName,
               u.classLevel AS classLevel,
               COALESCE(SUM(CASE WHEN p.stars > 0 THEN 1 ELSE 0 END), 0) AS chaptersCompleted,
               COALESCE(SUM(p.stars), 0) AS totalStars,
               MAX(p.lastAttemptAt) AS lastActiveAt
        FROM users u
        LEFT JOIN progress p ON p.userId = u.id
        WHERE u.role = 'STUDENT' AND u.isActive = 1 AND u.classLevel IN (:classLevels)
        GROUP BY u.id
        ORDER BY u.classLevel, u.fullName
        """
    )
    fun observeStudentProgress(classLevels: List<Int>): Flow<List<StudentProgressRow>>

    @Query("SELECT * FROM progress WHERE isSynced = 0")
    suspend fun unsynced(): List<ProgressEntity>

    @Query("UPDATE progress SET isSynced = 1 WHERE userId = :userId AND chapterId IN (:chapterIds)")
    suspend fun markSynced(userId: String, chapterIds: List<String>)
}

@Dao
interface BadgeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(badge: BadgeEntity)

    @Query("SELECT * FROM badges WHERE userId = :userId ORDER BY earnedAt DESC")
    fun observeBadges(userId: String): Flow<List<BadgeEntity>>

    @Query("SELECT code FROM badges WHERE userId = :userId")
    suspend fun earnedCodes(userId: String): List<String>
}

@Dao
interface SlideDao {

    @Upsert suspend fun upsertDecks(decks: List<SlideDeckEntity>)
    @Upsert suspend fun upsertDeck(deck: SlideDeckEntity)
    @Upsert suspend fun upsertSlides(slides: List<SlideEntity>)

    @Query("SELECT COUNT(*) FROM slide_decks WHERE isDefault = 1")
    suspend fun defaultDeckCount(): Int

    @Query("SELECT * FROM slide_decks WHERE isDefault = 1 OR ownerUserId = :teacherId ORDER BY isDefault DESC, orderIndex")
    fun observeDecksFor(teacherId: String): Flow<List<SlideDeckEntity>>

    @Transaction
    @Query("SELECT * FROM slide_decks WHERE id = :deckId")
    fun observeDeck(deckId: String): Flow<DeckWithSlides?>

    @Query("DELETE FROM slide_decks WHERE id = :deckId AND isDefault = 0")
    suspend fun deleteCustomDeck(deckId: String)

    @Query("DELETE FROM slides WHERE deckId = :deckId")
    suspend fun deleteSlidesFor(deckId: String)
}
