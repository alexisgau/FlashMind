package com.example.flashmind

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.flashmind.presentation.utils.CategorySyncWorker

import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@HiltAndroidApp
class FlashMindApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
//
//    override fun onCreate() {
//        super.onCreate()
//        Log.d("WorkManager", "onCreate ejecutado")
//        Log.d("WorkManager", "Factory inyectada? ${::workerFactory.isInitialized}")
//   WorkManager.initialize(this, workManagerConfiguration)
//        val request = OneTimeWorkRequestBuilder<CategorySyncWorker>().build()
//        WorkManager.getInstance(this).enqueue(request)
//    }


    override fun onCreate() {
        super.onCreate()
        Log.d("WorkManager", "oncreate")

        val testRequest = OneTimeWorkRequestBuilder<CategorySyncWorker>()
            .build()

        WorkManager.getInstance(this).enqueue(testRequest)

        scheduleCategorySync()
    }

    private fun scheduleCategorySync() {
        val request = OneTimeWorkRequestBuilder<CategorySyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueue(request)

        // Definimos las REGLAS para que el worker se ejecute
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Creamos la petición de trabajo PERIÓDICO (ej. cada 6 horas)
        val syncRequest = PeriodicWorkRequestBuilder<CategorySyncWorker>(2, TimeUnit.MINUTES) // Ca
            .setConstraints(constraints)
            .build()

        // Lo ponemos en la cola de WorkManager, asegurando que solo haya una
        // instancia de este trabajo periódico activo a la vez.
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "CategorySync",
            ExistingPeriodicWorkPolicy.KEEP, // Si ya existe uno, lo mantiene
            syncRequest
        )
    }
}

