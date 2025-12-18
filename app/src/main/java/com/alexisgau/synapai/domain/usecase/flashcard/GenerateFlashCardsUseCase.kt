package com.alexisgau.synapai.domain.usecase.flashcard

import com.alexisgau.synapai.domain.model.FlashCard
import com.alexisgau.synapai.domain.reposotory.AiRepository
import javax.inject.Inject

class GenerateFlashCardsUseCase @Inject constructor(private val repository: AiRepository) {

    suspend operator fun invoke(text: String, lessonId: Int): List<FlashCard> {

        return repository.generateFlashcards(text, lessonId)
    }
}