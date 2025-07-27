package com.example.flashmind.data.repository

import com.example.flashmind.data.local.dao.LessonDao
import com.example.flashmind.data.local.entities.toDomain
import com.example.flashmind.data.local.entities.toEntity
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.reposotory.LessonRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LessonRepositoryImpl @Inject constructor(private val dao: LessonDao,private val auth: FirebaseAuth): LessonRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?:  throw AuthException("Usuario no autenticado")

    override fun getLessonsByCategory(categoryId: Int): Flow<List<Lesson>> {
       return dao.getLessonsByCategory(categoryId).map { lesson->
           lesson.map { it.toDomain() }
       }
    }

    override suspend fun insert(lesson: Lesson) {
       val lesson =  lesson.copy(userId = userId )
        return dao.insert(lesson.toEntity())
    }
}