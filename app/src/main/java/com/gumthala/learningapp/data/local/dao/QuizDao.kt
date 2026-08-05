package com.gumthala.learningapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gumthala.learningapp.data.local.entity.QuizAnswerEntity
import com.gumthala.learningapp.data.local.entity.QuizAttemptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: QuizAttemptEntity)

    @Update
    suspend fun updateAttempt(attempt: QuizAttemptEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<QuizAnswerEntity>)

    @Query("SELECT * FROM quiz_attempts WHERE studentId = :studentId ORDER BY startedAtMillis DESC")
    fun observeAttemptsForStudent(studentId: String): Flow<List<QuizAttemptEntity>>

    @Query("SELECT * FROM quiz_attempts WHERE chapterId = :chapterId AND studentId = :studentId ORDER BY startedAtMillis DESC LIMIT 1")
    suspend fun getLatestAttempt(studentId: String, chapterId: String): QuizAttemptEntity?

    @Query("SELECT * FROM quiz_attempts WHERE isSynced = 0")
    suspend fun getUnsyncedAttempts(): List<QuizAttemptEntity>

    @Query("SELECT DISTINCT chapterId FROM quiz_attempts WHERE studentId = :studentId AND completedAtMillis IS NOT NULL")
    fun observeCompletedChapterIds(studentId: String): Flow<List<String>>

    @Query(
        """
        SELECT qa.* FROM quiz_attempts qa
        INNER JOIN students s ON s.id = qa.studentId
        WHERE s.classLevel = :classLevel
        """
    )
    suspend fun getAttemptsForClass(classLevel: Int): List<QuizAttemptEntity>
}
