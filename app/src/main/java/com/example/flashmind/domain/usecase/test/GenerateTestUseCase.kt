package com.example.flashmind.domain.usecase.test

import com.example.flashmind.domain.model.QuizQuestionModel
import com.example.flashmind.domain.reposotory.AiRepository
import javax.inject.Inject

class GenerateTestUseCase @Inject constructor(private val repository: AiRepository) {

    suspend operator fun invoke(text: String, testId: Int): List<QuizQuestionModel>  {
        return repository.generateTest(text,testId)
    }
}