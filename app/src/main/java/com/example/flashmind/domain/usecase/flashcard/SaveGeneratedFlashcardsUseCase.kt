package com.example.flashmind.domain.usecase.flashcard

import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.domain.reposotory.FlashCardRepository
import javax.inject.Inject

class SaveGeneratedFlashcardsUseCase @Inject constructor(private val flashCardRepository: FlashCardRepository) {

    suspend operator fun invoke(flashcards: List<FlashCard>){
        return flashCardRepository.saveGeneratedFlashcards(flashcards)
    }
}