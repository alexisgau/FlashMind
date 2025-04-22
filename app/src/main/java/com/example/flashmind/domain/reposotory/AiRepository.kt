package com.example.flashmind.domain.reposotory

import com.example.flashmind.domain.model.FlashCard

interface AiRepository {

    suspend fun generateFlashcards(text: String, lessonId: Int): List<FlashCard>
}