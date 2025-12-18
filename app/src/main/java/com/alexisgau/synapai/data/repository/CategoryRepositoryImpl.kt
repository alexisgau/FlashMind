package com.alexisgau.synapai.data.repository

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.alexisgau.synapai.data.local.dao.CategoryDao
import com.alexisgau.synapai.data.local.entities.toDomain
import com.alexisgau.synapai.data.local.entities.toEntity
import com.alexisgau.synapai.data.network.dto.CategoryFirestore
import com.alexisgau.synapai.data.worker.CategorySyncWorker
import com.alexisgau.synapai.domain.model.Category
import com.alexisgau.synapai.domain.reposotory.CategoryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val categoryDao: CategoryDao,
    private val firestore: FirebaseFirestore,
    private val workManager: WorkManager,
) : CategoryRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: "debug-user"


    override suspend fun insertCategory(category: Category) {

        categoryDao.insertCategory(category.copy(userId = userId, isSynced = false).toEntity())
        scheduleSync()
    }

    private fun scheduleSync() {
        val syncRequest = OneTimeWorkRequestBuilder<CategorySyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(
            "sync_categories_work",
            ExistingWorkPolicy.KEEP,
            syncRequest
        )
    }

    override suspend fun deleteCategory(category: Category) {
        categoryDao.markCategoryForDeletion(category.id)
        scheduleSync()

    }

    override suspend fun deleteCategoryFromFirestore(categoryId: Int) {
        firestore.collection("users").document(userId)
            .collection("categories").document(categoryId.toString())
            .delete().await()
    }

    override suspend fun deleteCategoryLocally(categoryId: Int) {
        categoryDao.deleteCategoryById(categoryId)
    }

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories(userId).map { categories ->
            categories.map { it.toDomain() }
        }
    }

    override suspend fun getUnsyncedCategories(): List<Category> {
        return categoryDao.getUnsyncedCategories(userId).map { it.toDomain() }
    }

    override suspend fun uploadCategoryToFirestore(category: Category) {
        val categoryFirestore = CategoryFirestore(
            id = category.id,
            name = category.name,
            color = category.color
        )

        try {
            firestore.collection("users").document(userId)
                .collection("categories").document(category.id.toString())
                .set(categoryFirestore).await()
        } catch (e: Exception) {
            Log.e("WorkManager", "Error subiendo categoría: ${category.name}", e)
        }
    }

    override fun getLessonCountByCategory(categoryId: Int): Flow<Int> {
        return categoryDao.getLessonCountByCategory(categoryId)
    }

    override suspend fun markCategoryAsSynced(categoryId: Int) {
        categoryDao.markAsSynced(categoryId)
    }
}

class AuthException(message: String) : Exception(message)




