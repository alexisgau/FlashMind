package com.example.flashmind.domain.reposotory

import com.example.flashmind.domain.model.Lesson
import kotlinx.coroutines.flow.Flow

interface LessonRepository {

    fun getLessonsByCategory(categoryId: Int): Flow<List<Lesson>>
    suspend fun insert(lesson: Lesson)
}