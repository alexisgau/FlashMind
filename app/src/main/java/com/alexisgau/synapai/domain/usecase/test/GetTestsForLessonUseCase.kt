package com.alexisgau.synapai.domain.usecase.test

import com.alexisgau.synapai.domain.model.TestModel
import com.alexisgau.synapai.domain.reposotory.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTestsForLessonUseCase @Inject constructor(
    private val repository: QuizRepository,
) {
    operator fun invoke(lessonId: Int): Flow<List<TestModel>> {
        return repository.getTestsForLesson(lessonId = lessonId)
    }
}