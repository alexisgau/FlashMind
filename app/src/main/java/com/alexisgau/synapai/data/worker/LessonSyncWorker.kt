package com.alexisgau.synapai.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.alexisgau.synapai.domain.reposotory.LessonRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class LessonSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: LessonRepository, // Hilt inyectará LessonRepositoryImpl
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val unsyncedLessons = repository.getUnsyncedLessons()
            if (unsyncedLessons.isEmpty()) return Result.success()

            unsyncedLessons.forEach { lesson ->
                if (lesson.isDeleted) {
                    repository.deleteLessonFromFirestore(lesson.id)
                    repository.deleteLessonLocally(lesson.id)
                } else {
                    repository.uploadLessonToFirestore(lesson)
                    repository.markLessonAsSynced(lesson.id)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}