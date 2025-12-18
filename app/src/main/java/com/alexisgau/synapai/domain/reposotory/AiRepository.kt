package com.alexisgau.synapai.domain.reposotory

import com.alexisgau.synapai.domain.model.FlashCard
import com.alexisgau.synapai.domain.model.QuizQuestionModel

interface AiRepository {

    suspend fun generateFlashcards(text: String, lessonId: Int): List<FlashCard>
    suspend fun generateTest(text: String, testId: Int): List<QuizQuestionModel>
    suspend fun generateSummary(text: String): String
}