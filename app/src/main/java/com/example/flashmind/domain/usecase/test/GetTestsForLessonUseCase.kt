package com.example.flashmind.domain.usecase.test

import com.example.flashmind.domain.model.TestModel
import com.example.flashmind.domain.reposotory.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTestsForLessonUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    operator fun invoke(lessonId: Int): Flow<List<TestModel>> {
        return repository.getTestsForLesson(lessonId = lessonId)
    }
}