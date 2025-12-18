package com.alexisgau.synapai.domain.usecase.test

import com.alexisgau.synapai.domain.reposotory.QuizRepository
import javax.inject.Inject

class DeleteTestUseCase @Inject constructor(
    private val repository: QuizRepository,
) {
    suspend operator fun invoke(testId: Int) {
        repository.deleteTest(testId = testId)
    }
}