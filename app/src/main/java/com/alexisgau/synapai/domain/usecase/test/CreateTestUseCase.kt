package com.alexisgau.synapai.domain.usecase.test

import com.alexisgau.synapai.domain.reposotory.QuizRepository
import javax.inject.Inject

class CreateTestUseCase @Inject constructor(
    private val repository: QuizRepository,
) {
    suspend operator fun invoke(title: String, lessonId: Int): Long {
        return repository.createTest(title = title, lessonId = lessonId)
    }
}