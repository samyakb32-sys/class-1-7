package com.gumthala.learningapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gumthala.learningapp.data.local.entity.AdminEntity

@Dao
interface AdminDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(admin: AdminEntity)

    @Query("SELECT * FROM admins WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): AdminEntity?

    @Query("SELECT COUNT(*) FROM admins")
    suspend fun count(): Int

    @Query("SELECT * FROM admins WHERE isSynced = 0")
    suspend fun getUnsynced(): List<AdminEntity>
}
