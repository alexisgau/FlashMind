package com.alexisgau.synapai.domain.reposotory

import com.alexisgau.synapai.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    fun getLessonsByCategory(categoryId: Int): Flow<List<Lesson>>
    suspend fun insert(lesson: Lesson)

    suspend fun updateLesson(lesson: Lesson)
    suspend fun deleteLesson(lesson: Lesson)
    suspend fun getUnsyncedLessons(): List<Lesson>
    suspend fun uploadLessonToFirestore(lesson: Lesson)
    suspend fun deleteLessonFromFirestore(lessonId: Int)
    suspend fun deleteLessonLocally(lessonId: Int)
    suspend fun markLessonAsSynced(lessonId: Int)
}