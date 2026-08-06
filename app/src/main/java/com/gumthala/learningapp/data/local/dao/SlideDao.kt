package com.gumthala.learningapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gumthala.learningapp.data.local.entity.SlideDeckEntity
import com.gumthala.learningapp.data.local.entity.SlideEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SlideDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDecks(decks: List<SlideDeckEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDeck(deck: SlideDeckEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSlides(slides: List<SlideEntity>)

    @Query("SELECT * FROM slide_decks ORDER BY orderIndex ASC")
    fun observeAllDecks(): Flow<List<SlideDeckEntity>>

    @Query("SELECT * FROM slide_decks WHERE createdByTeacherId = :teacherId ORDER BY orderIndex ASC")
    fun observeDecksByTeacher(teacherId: String): Flow<List<SlideDeckEntity>>

    @Query("SELECT * FROM slides WHERE deckId = :deckId ORDER BY orderIndex ASC")
    fun observeSlidesForDeck(deckId: String): Flow<List<SlideEntity>>

    @Query("SELECT COUNT(*) FROM slide_decks")
    suspend fun deckCount(): Int
}
