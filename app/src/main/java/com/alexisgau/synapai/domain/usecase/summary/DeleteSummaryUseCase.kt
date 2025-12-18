package com.alexisgau.synapai.domain.usecase.summary


import com.alexisgau.synapai.domain.reposotory.SummaryRepository
import javax.inject.Inject

class DeleteSummaryUseCase @Inject constructor(
    private val repository: SummaryRepository,
) {
    suspend operator fun invoke(summaryId: Int) {
        repository.deleteSummary(summaryId = summaryId)
    }
}