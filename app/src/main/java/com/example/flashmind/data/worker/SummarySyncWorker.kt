package com.example.flashmind.data.worker


import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.flashmind.domain.reposotory.SummaryRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SummarySyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: SummaryRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("SummarySyncWorker", "Starting summary sync work...")
        return try {
            val unsyncedSummaries = repository.getUnsyncedSummaries()
            Log.d("SummarySyncWorker", "Found ${unsyncedSummaries.size} unsynced summaries.")

            if (unsyncedSummaries.isEmpty()) {
                Log.d("SummarySyncWorker", "No summaries to sync. Work finished successfully.")
                return Result.success()
            }

            unsyncedSummaries.forEach { summary ->
                if (summary.isDeleted) {
                    try {
                        repository.deleteSummaryFromFirestore(summary.summaryId)
                        repository.deleteSummaryLocally(summary.summaryId)
                        Log.i("SummarySyncWorker", "Deleted summary ${summary.summaryId} from Firestore and locally.")
                    } catch (e: Exception) {
                        Log.e("SummarySyncWorker", "Failed to delete summary ${summary.summaryId}. Retrying later.", e)
                        return@doWork Result.retry()
                    }
                } else {
                    try {
                        repository.uploadSummaryToFirestore(summary)
                        repository.markSummaryAsSynced(summary.summaryId)
                        Log.i("SummarySyncWorker", "Synced summary ${summary.summaryId} to Firestore.")
                    } catch (e: Exception) {
                        Log.e("SummarySyncWorker", "Failed to sync summary ${summary.summaryId}. Retrying later.", e)
                        return@doWork Result.retry() // Important to retry upload failures
                    }
                }
            }

            Log.d("SummarySyncWorker", "Summary sync work finished successfully after processing summaries.")
            Result.success()

        } catch (e: Exception) {
            Log.e("SummarySyncWorker", "Summary sync work failed unexpectedly. Retrying.", e)
            Result.retry()
        }
    }
}