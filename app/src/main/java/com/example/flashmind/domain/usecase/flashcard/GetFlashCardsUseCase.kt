package com.example.flashmind.domain.usecase.flashcard

import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.domain.reposotory.FlashCardRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFlashCardsUseCase @Inject constructor(private val flashCardRepository: FlashCardRepository) {

    operator fun invoke(lessonId: Int): Flow<List<FlashCard>> {

        return flashCardRepository.getFlashCardsByLesson(lessonId)

    }
}