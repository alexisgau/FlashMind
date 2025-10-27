package com.example.flashmind.domain.usecase.test

import com.example.flashmind.domain.reposotory.QuizRepository
import javax.inject.Inject

class DeleteTestUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(testId: Int) {
        repository.deleteTest(testId = testId)
    }
}