// workers/QuizSyncWorker.kt
package com.example.flashmind.workers

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
    private val repository: QuizRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("QuizSyncWorker", "Starting quiz sync work...")
        return try {
            val unsyncedTests = repository.getUnsyncedTests()
            Log.d("QuizSyncWorker", "Found ${unsyncedTests.size} unsynced tests.")

            // 1. Sincronizar Tests
            unsyncedTests.forEach { test ->
                if (test.isDeleted) {
                    try {
                        repository.deleteTestFromFirestore(test.testId)
                        repository.deleteTestLocally(test.testId)
                        Log.i(
                            "QuizSyncWorker",
                            "Deleted test ${test.testId} from Firestore and locally."
                        )
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
                        Log.i("QuizSyncWorker", "Synced test ${test.testId} to Firestore.")
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
            Log.d("QuizSyncWorker", "Found ${unsyncedQuestions.size} unsynced questions.")
            val questionsToDeleteLocally = mutableListOf<Int>()

            unsyncedQuestions.forEach { question ->
                if (question.isDeleted) {
                    // Acumula para borrado físico local al final
                    questionsToDeleteLocally.add(question.questionId)
                } else {
                    // 👇 Lógica Simplificada: Revisa si el padre está en la lista de no sincronizados Y marcado para borrar 👇
                    val parentTestMarkedForDeletion =
                        unsyncedTests.any { it.testId == question.testId && it.isDeleted }

                    if (parentTestMarkedForDeletion) {
                        // Si el test padre se va a borrar (o ya se borró de Firestore en el paso anterior), no subas la pregunta.
                        Log.w(
                            "QuizSyncWorker",
                            "Skipping sync for question ${question.questionId} as parent test ${question.testId} is marked for deletion."
                        )
                        // Podríamos marcarla como sync aquí para evitar reintentos, pero es más seguro esperar
                    } else {
                        // El padre no está marcado para borrar (o ya está sincronizado/borrado de Firestore) -> Intenta subir la pregunta
                        try {
                            repository.uploadQuestionToFirestore(question)
                            repository.markQuestionAsSynced(question.questionId)
                            Log.i(
                                "QuizSyncWorker",
                                "Synced question ${question.questionId} to Firestore."
                            )
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
                    Log.i(
                        "QuizSyncWorker",
                        "Deleted ${questionsToDeleteLocally.size} questions locally."
                    )
                } catch (e: Exception) {
                    Log.e(
                        "QuizSyncWorker",
                        "Failed to delete questions locally. Retrying later.",
                        e
                    )
                    return@doWork Result.retry()
                }
            }

            Log.d("QuizSyncWorker", "Quiz sync work finished successfully.")
            Result.success()

        } catch (e: Exception) {
            Log.e("QuizSyncWorker", "Quiz sync work failed unexpectedly. Retrying.", e)
            Result.retry()
        }
    }
}