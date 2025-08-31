package com.example.flashmind.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.flashmind.domain.reposotory.CategoryRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CategorySyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CategoryRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val unsyncedCategories = repository.getUnsyncedCategories()

            if (unsyncedCategories.isEmpty()) return Result.success()

            unsyncedCategories.forEach { category ->

                if (category.isDeleted) {
                    repository.deleteCategoryFromFirestore(category.id)
                    repository.deleteCategoryLocally(category.id)
                } else {

                    repository.uploadCategoryToFirestore(category)
                    repository.markCategoryAsSynced(category.id)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
