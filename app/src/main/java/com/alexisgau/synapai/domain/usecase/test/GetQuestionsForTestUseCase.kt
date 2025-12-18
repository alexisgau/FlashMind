package com.alexisgau.synapai.domain.usecase.test

import com.alexisgau.synapai.domain.model.QuizQuestionModel
import com.alexisgau.synapai.domain.reposotory.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuestionsForTestUseCase @Inject constructor(
    private val repository: QuizRepository,
) {
    operator fun invoke(testId: Int): Flow<List<QuizQuestionModel>> {
        return repository.getQuestionsForTest(testId = testId)
    }
}