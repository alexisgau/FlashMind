package com.alexisgau.synapai.domain.usecase.summary

import com.alexisgau.synapai.domain.model.SummaryModel
import com.alexisgau.synapai.domain.reposotory.SummaryRepository
import javax.inject.Inject

class CreateSummaryUseCase @Inject constructor(
    private val repository: SummaryRepository,
) {
    suspend operator fun invoke(originalText: String, summaryToSave: SummaryModel): Long {
        return repository.createSummary(originalText = originalText, summaryToSave = summaryToSave)
    }
}