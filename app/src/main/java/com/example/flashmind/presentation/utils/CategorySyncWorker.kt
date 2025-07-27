package com.example.flashmind.presentation.utils

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.flashmind.domain.reposotory.CategoryRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CategorySyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: CategoryRepository
) : CoroutineWorker(appContext, workerParams) {

//    override suspend fun doWork(): Result {
//        Log.d("CategorySyncWorker", "Worker ejecutándose")
//        return try {
//            // Tu lógica de sincronización aquí...
//            Result.success()
//        } catch (e: Exception) {
//            Result.retry()
//        }
//    }
//}
    override suspend fun doWork(): Result {
        return try {
            // 1. Obtener el trabajo pendiente desde el repositorio
            val unsyncedCategories = repository.getUnsyncedCategories()
            Log.d("WorkManager", "list: $unsyncedCategories")

            if (unsyncedCategories.isEmpty()) {
                // No hay nada que hacer, trabajo exitoso
                Log.d("WorkManager", "Success1")
                return Result.success()
            }
            val currentUser = FirebaseAuth.getInstance().currentUser
            Log.d("WorkManager", "Usuario actual: ${currentUser?.uid ?: "null"}")
            // 2. Realizar el trabajo para cada item pendiente
            unsyncedCategories.forEach { category ->
                repository.uploadCategoryToFirestore(category)
                repository.markCategoryAsSynced(category.id)
            }

            // 3. Informar que el trabajo se completó con éxito
            Log.d("WorkManager", "Success2")
            Result.success()

        } catch (e: Exception) {
            // Si algo falla (ej. sin internet), WorkManager lo reintentará
            // automáticamente gracias a esta línea.
            Log.d("WorkManager", "Retry")
            Result.retry()

        }
    }

//    override suspend fun doWork(): Result {
//        Log.d("DummyWorker", "¡Worker ejecutado correctamente!")
//        return Result.success()
//    }
}

//class TestWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
//    override fun doWork(): Result {
//
//        Log.d("TestWorker", "WORKER EJECUTADO CON ÉXITO")
//        return Result.success()
//    }
//}
