package com.example.flashmind.domain.usecase.test

import com.example.flashmind.domain.reposotory.QuizRepository
import javax.inject.Inject

class CreateTestUseCase @Inject constructor(
    private val repository: QuizRepository,
) {
    suspend operator fun invoke(title: String, lessonId: Int): Long {
        return repository.createTest(title = title, lessonId = lessonId)
    }
}