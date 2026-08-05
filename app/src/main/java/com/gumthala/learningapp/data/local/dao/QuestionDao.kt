package com.gumthala.learningapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gumthala.learningapp.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(questions: List<QuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(question: QuestionEntity)

    @Query("SELECT * FROM questions WHERE chapterId = :chapterId ORDER BY orderIndex ASC")
    fun observeForChapter(chapterId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE chapterId = :chapterId ORDER BY orderIndex ASC")
    suspend fun getForChapter(chapterId: String): List<QuestionEntity>

    @Query("SELECT * FROM questions WHERE createdByTeacherId = :teacherId ORDER BY orderIndex ASC")
    fun observeByTeacher(teacherId: String): Flow<List<QuestionEntity>>

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM questions WHERE isSynced = 0")
    suspend fun getUnsynced(): List<QuestionEntity>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun count(): Int
}
