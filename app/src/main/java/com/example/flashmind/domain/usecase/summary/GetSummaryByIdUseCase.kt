package com.example.flashmind.domain.usecase.summary


import com.example.flashmind.domain.model.SummaryModel
import com.example.flashmind.domain.reposotory.SummaryRepository
import javax.inject.Inject

class GetSummaryByIdUseCase @Inject constructor(
    private val repository: SummaryRepository
) {
    suspend operator fun invoke(summaryId: Int): SummaryModel? {
        return repository.getSummaryById(summaryId = summaryId)
    }
}