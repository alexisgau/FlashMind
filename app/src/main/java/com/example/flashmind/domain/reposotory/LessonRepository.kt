package com.example.flashmind.domain.reposotory

import com.example.flashmind.data.local.entities.LessonEntity
import com.example.flashmind.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    fun getLessonsByCategory(categoryId: Int): Flow<List<Lesson>>
    suspend fun insert(lesson: Lesson)
    suspend fun deleteLesson(lesson: Lesson)
    suspend fun getUnsyncedLessons(): List<Lesson>
    suspend fun uploadLessonToFirestore(lesson: Lesson)
    suspend fun deleteLessonFromFirestore(lessonId: Int)
    suspend fun deleteLessonLocally(lessonId: Int)
    suspend fun markLessonAsSynced(lessonId: Int)
}