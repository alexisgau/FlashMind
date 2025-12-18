package com.alexisgau.synapai.domain.usecase.flashcard

import com.alexisgau.synapai.domain.model.FlashCard
import com.alexisgau.synapai.domain.reposotory.FlashCardRepository
import javax.inject.Inject

class SaveGeneratedFlashcardsUseCase @Inject constructor(private val flashCardRepository: FlashCardRepository) {

    suspend operator fun invoke(flashcards: List<FlashCard>) {
        return flashCardRepository.saveGeneratedFlashcards(flashcards)
    }
}