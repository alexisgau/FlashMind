package com.example.flashmind.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.flashmind.domain.reposotory.FlashCardRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class FlashCardSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: FlashCardRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val unsyncedFlashcards = repository.getUnsyncedFlashcards()
            if (unsyncedFlashcards.isEmpty()) return Result.success()

            unsyncedFlashcards.forEach { flashcard ->
                if (flashcard.isDeleted) {
                    repository.deleteFlashcardFromFirestore(flashcard.id)
                    repository.deleteFlashcardLocally(flashcard.id)
                } else {
                    repository.uploadFlashcardToFirestore(flashcard)
                    repository.markFlashcardAsSynced(flashcard.id)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}