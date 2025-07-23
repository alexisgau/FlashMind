package com.example.flashmind.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.flashmind.data.local.entities.FlashCardEntity
import kotlinx.coroutines.flow.Flow


    @Dao
    interface FlashCardDao {
        @Query("SELECT * FROM flashcards WHERE lessonId = :lessonId")
        fun getFlashCardsByLesson(lessonId: Int): Flow<List<FlashCardEntity>>

        @Query("SELECT * FROM flashcards WHERE id = :id")
       suspend fun getFlashCardsById(id: Int): FlashCardEntity

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insert(flashCard: FlashCardEntity)

        @Query("UPDATE flashcards SET color = :newColor")
        suspend fun updateAllColors(newColor: String)

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertAll(flashcards: List<FlashCardEntity>)

        @Update
        suspend fun update(flashCard: FlashCardEntity)

        @Delete
        suspend fun delete(flashCard: FlashCardEntity)
    }

