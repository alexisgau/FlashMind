package com.example.flashmind.data.repository

import com.example.flashmind.data.local.dao.LessonDao
import com.example.flashmind.data.local.entities.toDomain
import com.example.flashmind.data.local.entities.toEntity
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.reposotory.LessonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LessonRepositoryImpl @Inject constructor(private val dao: LessonDao): LessonRepository {
    override fun getLessonsByCategory(categoryId: Int): Flow<List<Lesson>> {
       return dao.getLessonsByCategory(categoryId).map { lesson->
           lesson.map { it.toDomain() }
       }
    }

    override suspend fun insert(lesson: Lesson) {
        return dao.insert(lesson.toEntity())
    }
}