package com.example.flashmind.data.repository

import com.example.flashmind.data.worker.SummarySyncWorker
import com.example.flashmind.domain.model.toDomain
import com.example.flashmind.domain.model.toEntity
import com.example.flashmind.domain.reposotory.SummaryRepository


import android.util.Log
import androidx.work.*
import com.example.flashmind.data.local.dao.SummaryDao
import com.example.flashmind.data.local.entities.SummaryEntity

import com.example.flashmind.data.network.model.SummaryFirestore
import com.example.flashmind.domain.model.SummaryModel

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SummaryRepositoryImpl @Inject constructor(
    private val dao: SummaryDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val workManager: WorkManager
) : SummaryRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: "anonymous_user"



    override suspend fun createSummary(originalText: String, summaryToSave: SummaryModel): Long {
        val summaryEntity = summaryToSave.toEntity(userId = userId).copy(
            summaryId = 0,
            originalText = originalText
        )
        val newId = dao.insertSummary(summaryEntity)
        scheduleSync()
        return newId
    }

    override suspend fun deleteSummary(summaryId: Int) {
        dao.markSummaryForDeletion(summaryId)
        scheduleSync()
    }

    override fun getSummariesForLesson(lessonId: Int): Flow<List<SummaryModel>> {
        return dao.getSummariesByLessonId(lessonId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSummaryById(summaryId: Int): SummaryModel? {
        return dao.getSummaryById(summaryId)?.toDomain()
    }




    private fun scheduleSync() {
        val syncRequest = OneTimeWorkRequestBuilder<SummarySyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag("summary_sync_work_tag")
            .build()

        workManager.enqueueUniqueWork(
            "sync_summaries_work",
            ExistingWorkPolicy.KEEP,
            syncRequest
        )
        Log.d("SummaryRepositoryImpl", "Summary sync scheduled.")
    }

    // --- Internal API for Worker ---

    override suspend fun getUnsyncedSummaries(): List<SummaryEntity> {
        return dao.getUnsyncedSummaries(userId)
    }

    override suspend fun uploadSummaryToFirestore(summary: SummaryEntity) {
        val summaryFirestore = SummaryFirestore(
            summaryId = summary.summaryId,
            lessonId = summary.lessonId,
            generatedSummary = summary.generatedSummary,
            title = summary.title,
            userId = summary.userId
        )
        firestore.collection("users").document(userId)
            .collection("summaries").document("Summary_${summary.summaryId}")
            .set(summaryFirestore).await()
    }

    override suspend fun deleteSummaryFromFirestore(summaryId: Int) {
        firestore.collection("users").document(userId)
            .collection("summaries").document("Summary_$summaryId")
            .delete().await()
    }

    override suspend fun deleteSummaryLocally(summaryId: Int) {
        dao.deleteSummaryById(summaryId)
    }

    override suspend fun markSummaryAsSynced(summaryId: Int) {
        dao.markSummaryAsSynced(summaryId)
    }
}