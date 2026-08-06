package com.gumthala.learningapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gumthala.learningapp.data.local.entity.StudentStatsEntity
import kotlinx.coroutines.flow.Flow

data class LeaderboardRow(
    val studentId: String,
    val name: String,
    val avatarEmoji: String,
    val xp: Int,
    val coins: Int,
    val streakDays: Int
)

@Dao
interface StudentStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(stats: StudentStatsEntity)

    @Query("SELECT * FROM student_stats WHERE studentId = :studentId LIMIT 1")
    suspend fun getForStudent(studentId: String): StudentStatsEntity?

    @Query("SELECT * FROM student_stats WHERE studentId = :studentId LIMIT 1")
    fun observeForStudent(studentId: String): Flow<StudentStatsEntity?>

    @Query(
        """
        SELECT st.studentId as studentId, s.name as name, s.avatarEmoji as avatarEmoji,
               st.xp as xp, st.coins as coins, st.streakDays as streakDays
        FROM student_stats st
        INNER JOIN students s ON s.id = st.studentId
        WHERE st.classLevel = :classLevel
        ORDER BY st.xp DESC
        LIMIT :limit
        """
    )
    fun observeLeaderboard(classLevel: Int, limit: Int = 50): Flow<List<LeaderboardRow>>
}
