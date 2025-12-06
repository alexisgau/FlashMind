// workers/QuizSyncWorker.kt
package com.example.flashmind.data.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.flashmind.domain.reposotory.QuizRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class QuizSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: QuizRepository,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val unsyncedTests = repository.getUnsyncedTests()

            // 1. Sincronizar Tests
            unsyncedTests.forEach { test ->
                if (test.isDeleted) {
                    try {
                        repository.deleteTestFromFirestore(test.testId)
                        repository.deleteTestLocally(test.testId)
                    } catch (e: Exception) {
                        Log.e(
                            "QuizSyncWorker",
                            "Failed to delete test ${test.testId}. Retrying later.",
                            e
                        )
                        return@doWork Result.retry() // Reintenta si falla el borrado
                    }
                } else {
                    try {
                        repository.uploadTestToFirestore(test)
                        repository.markTestAsSynced(test.testId)
                    } catch (e: Exception) {
                        Log.e(
                            "QuizSyncWorker",
                            "Failed to sync test ${test.testId}. Retrying later.",
                            e
                        )
                        return@doWork Result.retry() // Reintenta si falla la subida
                    }
                }
            }

            // 2. Sincronizar Preguntas
            val unsyncedQuestions = repository.getUnsyncedQuestions()
            val questionsToDeleteLocally = mutableListOf<Int>()

            unsyncedQuestions.forEach { question ->
                if (question.isDeleted) {
                    questionsToDeleteLocally.add(question.questionId)
                } else {
                    val parentTestMarkedForDeletion =
                        unsyncedTests.any { it.testId == question.testId && it.isDeleted }

                    if (parentTestMarkedForDeletion) {

                    } else {
                        // El padre no está marcado para borrar (o ya está sincronizado/borrado de Firestore) -> Intenta subir la pregunta
                        try {
                            repository.uploadQuestionToFirestore(question)
                            repository.markQuestionAsSynced(question.questionId)
                        } catch (e: Exception) {
                            Log.e(
                                "QuizSyncWorker",
                                "Failed to sync question ${question.questionId}. Retrying later.",
                                e
                            )
                            return@doWork Result.retry() // Reintenta si falla la subida
                        }
                    }
                }
            }

            // 3. Borrar físicamente las preguntas marcadas localmente
            if (questionsToDeleteLocally.isNotEmpty()) {
                try {
                    repository.deleteQuestionsLocally(questionsToDeleteLocally)
                } catch (e: Exception) {
                    Log.e(
                        "QuizSyncWorker",
                        "Failed to delete questions locally. Retrying later.",
                        e
                    )
                    return Result.retry()
                }
            }

            Result.success()

        } catch (e: Exception) {
            Log.e("QuizSyncWorker", "Quiz sync work failed unexpectedly. Retrying.", e)
            Result.retry()
        }
    }
}