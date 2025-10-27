package com.example.flashmind.domain.usecase.summary


import com.example.flashmind.domain.model.SummaryModel
import com.example.flashmind.domain.reposotory.SummaryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSummariesForLessonUseCase @Inject constructor(
    private val repository: SummaryRepository
) {
    operator fun invoke(lessonId: Int): Flow<List<SummaryModel>> {
        return repository.getSummariesForLesson(lessonId = lessonId)
    }
}