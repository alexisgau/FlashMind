package com.example.flashmind.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.flashmind.data.local.entities.LessonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LessonDao {

    @Query("SELECT * FROM lessons WHERE categoryId = :categoryId AND isDeleted = 0")
    fun getLessonsForCategory(categoryId: String): Flow<List<LessonEntity>>

    @Query("UPDATE lessons SET isDeleted = 1, isSynced = 0 WHERE id = :lessonId")
    suspend fun markLessonForDeletion(lessonId: Int)

    @Query("DELETE FROM lessons WHERE id = :lessonId")
    suspend fun deleteLessonById(lessonId: Int)

    @Query("SELECT * FROM lessons WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedLessons(userId: String): List<LessonEntity>

    @Query("UPDATE lessons SET isSynced = 1 WHERE id = :lessonId")
    suspend fun markLessonAsSynced(lessonId: Int)


    @Query("SELECT * FROM lessons WHERE categoryId = :categoryId")
    fun getLessonsByCategory(categoryId: Int): Flow<List<LessonEntity>>

    @Upsert
    suspend fun insert(lesson: LessonEntity)

    @Delete
    suspend fun deleteLesson(lesson: LessonEntity)


}
