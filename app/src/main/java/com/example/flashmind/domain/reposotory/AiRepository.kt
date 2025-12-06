package com.example.flashmind.domain.reposotory

import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.domain.model.QuizQuestionModel

interface AiRepository {

    suspend fun generateFlashcards(text: String, lessonId: Int): List<FlashCard>
    suspend fun generateTest(text: String, testId: Int): List<QuizQuestionModel>
    suspend fun generateSummary(text: String): String
}