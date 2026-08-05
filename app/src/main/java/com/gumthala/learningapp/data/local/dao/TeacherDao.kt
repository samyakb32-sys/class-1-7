package com.gumthala.learningapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gumthala.learningapp.data.local.entity.TeacherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TeacherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(teacher: TeacherEntity)

    @Update
    suspend fun update(teacher: TeacherEntity)

    @Query("SELECT * FROM teachers WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): TeacherEntity?

    @Query("SELECT * FROM teachers ORDER BY name ASC")
    fun observeAll(): Flow<List<TeacherEntity>>

    @Query("SELECT * FROM teachers WHERE isSynced = 0")
    suspend fun getUnsynced(): List<TeacherEntity>
}
