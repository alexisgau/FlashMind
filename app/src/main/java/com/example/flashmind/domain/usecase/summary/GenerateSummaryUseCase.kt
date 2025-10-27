package com.example.flashmind.domain.usecase.summary


import com.example.flashmind.domain.reposotory.AiRepository
import javax.inject.Inject

class GenerateSummaryUseCase @Inject constructor(
    private val aiRepository: AiRepository
) {
    suspend operator fun invoke(text: String): String {
        return aiRepository.generateSummary(text)
    }
}