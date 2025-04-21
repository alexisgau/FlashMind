package com.example.flashmind.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.flashmind.data.local.entities.FlashCardEntity
import kotlinx.coroutines.flow.Flow


    @Dao
    interface FlashCardDao {
        @Query("SELECT * FROM flashcards WHERE lessonId = :lessonId")
        fun getFlashCardsByLesson(lessonId: Int): Flow<List<FlashCardEntity>>

        @Upsert
        suspend fun insert(flashCard: FlashCardEntity)

        @Update
        suspend fun update(flashCard: FlashCardEntity)

        @Delete
        suspend fun delete(flashCard: FlashCardEntity)
    }

