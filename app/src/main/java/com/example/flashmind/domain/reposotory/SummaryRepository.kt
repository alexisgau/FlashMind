package com.example.flashmind.domain.reposotory


import com.example.flashmind.data.local.entities.SummaryEntity
import com.example.flashmind.domain.model.SummaryModel
import kotlinx.coroutines.flow.Flow

interface SummaryRepository {

    // --- Public API for UseCases/ViewModels ---
    suspend fun createSummary(originalText: String, summaryToSave: SummaryModel): Long
    suspend fun deleteSummary(summaryId: Int)
    fun getSummariesForLesson(lessonId: Int): Flow<List<SummaryModel>>
    suspend fun getSummaryById(summaryId: Int): SummaryModel?

    // --- Internal API for Worker ---
    suspend fun getUnsyncedSummaries(): List<SummaryEntity>
    suspend fun uploadSummaryToFirestore(summary: SummaryEntity)
    suspend fun deleteSummaryFromFirestore(summaryId: Int)
    suspend fun deleteSummaryLocally(summaryId: Int)
    suspend fun markSummaryAsSynced(summaryId: Int)
}