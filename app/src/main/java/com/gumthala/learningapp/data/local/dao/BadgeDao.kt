package com.gumthala.learningapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gumthala.learningapp.data.local.entity.BadgeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(badge: BadgeEntity)

    @Query("SELECT * FROM badges WHERE studentId = :studentId ORDER BY earnedAtMillis DESC")
    fun observeForStudent(studentId: String): Flow<List<BadgeEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM badges WHERE studentId = :studentId AND badgeKey = :badgeKey)")
    suspend fun hasBadge(studentId: String, badgeKey: String): Boolean
}
