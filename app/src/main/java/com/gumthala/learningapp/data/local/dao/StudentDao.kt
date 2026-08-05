package com.gumthala.learningapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gumthala.learningapp.data.local.entity.StudentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(student: StudentEntity)

    @Update
    suspend fun update(student: StudentEntity)

    @Query("SELECT * FROM students WHERE classLevel = :classLevel ORDER BY name ASC")
    fun observeByClass(classLevel: Int): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE registeredByUserId = :teacherId ORDER BY name ASC")
    fun observeByRegisteredBy(teacherId: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students ORDER BY classLevel ASC, name ASC")
    fun observeAll(): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): StudentEntity?

    @Query("SELECT * FROM students WHERE name = :name AND classLevel = :classLevel LIMIT 1")
    suspend fun findByNameAndClass(name: String, classLevel: Int): StudentEntity?

    @Query("SELECT * FROM students WHERE isSynced = 0")
    suspend fun getUnsynced(): List<StudentEntity>
}
