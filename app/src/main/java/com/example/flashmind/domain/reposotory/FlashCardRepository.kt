package com.example.flashmind.domain.reposotory

import com.example.flashmind.domain.model.FlashCard
import kotlinx.coroutines.flow.Flow

interface FlashCardRepository {

    suspend fun insert(flashCard: FlashCard)
    suspend fun delete(flashCard: FlashCard)
    suspend fun update(flashCard: FlashCard)
    suspend fun saveGeneratedFlashcards(flashcards: List<FlashCard>)
    fun getFlashCardsByLesson(lessonId: Int): Flow<List<FlashCard>>
    suspend fun  getFlashCardsById(id: Int): FlashCard

}