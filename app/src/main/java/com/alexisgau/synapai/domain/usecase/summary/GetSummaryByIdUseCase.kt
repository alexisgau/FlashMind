package com.alexisgau.synapai.domain.usecase.summary


import com.alexisgau.synapai.domain.model.SummaryModel
import com.alexisgau.synapai.domain.reposotory.SummaryRepository
import javax.inject.Inject

class GetSummaryByIdUseCase @Inject constructor(
    private val repository: SummaryRepository,
) {
    suspend operator fun invoke(summaryId: Int): SummaryModel? {
        return repository.getSummaryById(summaryId = summaryId)
    }
}