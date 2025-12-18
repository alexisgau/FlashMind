package com.alexisgau.synapai.domain.usecase.summary


import com.alexisgau.synapai.domain.model.SummaryModel
import com.alexisgau.synapai.domain.reposotory.SummaryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSummariesForLessonUseCase @Inject constructor(
    private val repository: SummaryRepository,
) {
    operator fun invoke(lessonId: Int): Flow<List<SummaryModel>> {
        return repository.getSummariesForLesson(lessonId = lessonId)
    }
}