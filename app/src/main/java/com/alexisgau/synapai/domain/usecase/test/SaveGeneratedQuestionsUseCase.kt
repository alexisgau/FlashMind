package com.alexisgau.synapai.domain.usecase.test

import com.alexisgau.synapai.domain.model.QuizQuestionModel
import com.alexisgau.synapai.domain.reposotory.QuizRepository
import javax.inject.Inject

class SaveGeneratedQuestionsUseCase @Inject constructor(
    private val repository: QuizRepository,
) {
    suspend operator fun invoke(questions: List<QuizQuestionModel>, testId: Long) {
        repository.saveGeneratedQuestions(questions = questions, testId = testId)
    }
}