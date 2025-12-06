package com.example.flashmind.domain.usecase.summary

import com.example.flashmind.domain.model.SummaryModel
import com.example.flashmind.domain.reposotory.SummaryRepository
import javax.inject.Inject

class CreateSummaryUseCase @Inject constructor(
    private val repository: SummaryRepository,
) {
    suspend operator fun invoke(originalText: String, summaryToSave: SummaryModel): Long {
        return repository.createSummary(originalText = originalText, summaryToSave = summaryToSave)
    }
}