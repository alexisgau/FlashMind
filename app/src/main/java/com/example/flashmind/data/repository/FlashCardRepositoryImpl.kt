package com.example.flashmind.data.repository

import com.example.flashmind.data.local.dao.FlashCardDao
import com.example.flashmind.data.local.entities.toDomain
import com.example.flashmind.data.local.entities.toEntity
import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.domain.reposotory.FlashCardRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FlashCardRepositoryImpl @Inject constructor(private val dao: FlashCardDao):
    FlashCardRepository {
    override suspend fun insert(flashCard: FlashCard) {
        return dao.insert(flashCard.toEntity())
    }

    override suspend fun delete(flashCard: FlashCard) {
        return dao.delete(flashCard.toEntity())
    }

    override suspend fun update(flashCard: FlashCard) {
        return dao.update(flashCard.toEntity())
    }

    override suspend fun saveGeneratedFlashcards(flashcards: List<FlashCard>) {
        return dao.insertAll(flashcards.map {it.toEntity()})
    }

    override fun getFlashCardsByLesson(lessonId: Int): Flow<List<FlashCard>> {
        return  dao.getFlashCardsByLesson(lessonId).map { flashCard->
            flashCard.map { it.toDomain() }
        }
    }
}