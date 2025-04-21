package com.example.flashmind.domain.reposotory

import com.example.flashmind.domain.model.FlashCard
import kotlinx.coroutines.flow.Flow

interface FlashCardRepository {

    suspend fun insert(flashCard: FlashCard)
    suspend fun delete(flashCard: FlashCard)
    suspend fun update(flashCard: FlashCard)
    fun getFlashCardsByLesson(lessonId: Int): Flow<List<FlashCard>>

}