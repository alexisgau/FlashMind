package com.alexisgau.synapai.domain.usecase.test

import com.alexisgau.synapai.domain.model.QuizQuestionModel
import com.alexisgau.synapai.domain.reposotory.AiRepository
import javax.inject.Inject

class GenerateTestUseCase @Inject constructor(private val repository: AiRepository) {

    suspend operator fun invoke(text: String, testId: Int): List<QuizQuestionModel> {
        return repository.generateTest(text, testId)
    }
}