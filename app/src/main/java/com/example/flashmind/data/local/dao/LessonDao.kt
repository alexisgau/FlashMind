package com.example.flashmind.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.flashmind.data.local.entities.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Query("SELECT * FROM lessons WHERE categoryId = :categoryId")
    fun getLessonsByCategory(categoryId: Int): Flow<List<LessonEntity>>

    @Upsert
    suspend fun insert(lesson: LessonEntity)
}
