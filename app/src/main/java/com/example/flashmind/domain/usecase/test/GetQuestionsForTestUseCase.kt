package com.example.flashmind.domain.usecase.test

import com.example.flashmind.domain.model.QuizQuestionModel
import com.example.flashmind.domain.reposotory.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuestionsForTestUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    operator fun invoke(testId: Int): Flow<List<QuizQuestionModel>> {
        return repository.getQuestionsForTest(testId = testId)
    }
}