package com.alexisgau.synapai.data.worker


import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alexisgau.synapai.domain.reposotory.SummaryRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SummarySyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: SummaryRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val unsyncedSummaries = repository.getUnsyncedSummaries()
            if (unsyncedSummaries.isEmpty()) {
                return Result.success()
            }

            unsyncedSummaries.forEach { summary ->
                if (summary.isDeleted) {
                    try {
                        repository.deleteSummaryFromFirestore(summary.summaryId)
                        repository.deleteSummaryLocally(summary.summaryId)
                    } catch (e: Exception) {
                        Log.e(
                            "SummarySyncWorker",
                            "Failed to delete summary ${summary.summaryId}. Retrying later.",
                            e
                        )
                        return@doWork Result.retry()
                    }
                } else {
                    try {
                        repository.uploadSummaryToFirestore(summary)
                        repository.markSummaryAsSynced(summary.summaryId)
                    } catch (e: Exception) {
                        Log.e(
                            "SummarySyncWorker",
                            "Failed to sync summary ${summary.summaryId}. Retrying later.",
                            e
                        )
                        return@doWork Result.retry() // Important to retry upload failures
                    }
                }
            }

            Result.success()

        } catch (e: Exception) {
            Log.e("SummarySyncWorker", "Summary sync work failed unexpectedly. Retrying.", e)
            Result.retry()
        }
    }
}