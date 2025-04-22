package com.example.flashmind.domain.usecase

import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.domain.reposotory.AiRepository
import javax.inject.Inject

class GenerateFlashCards @Inject constructor(private val repository: AiRepository) {

    suspend operator fun invoke(text: String, lessonId: Int): List<FlashCard> {

        return repository.generateFlashcards(text,lessonId)
    }
}