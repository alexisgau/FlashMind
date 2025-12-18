package com.alexisgau.synapai.data.repository

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.alexisgau.synapai.data.local.dao.LessonDao
import com.alexisgau.synapai.data.local.entities.toDomain
import com.alexisgau.synapai.data.local.entities.toEntity
import com.alexisgau.synapai.data.network.dto.LessonFirestore
import com.alexisgau.synapai.data.worker.LessonSyncWorker
import com.alexisgau.synapai.domain.model.Lesson
import com.alexisgau.synapai.domain.reposotory.LessonRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LessonRepositoryImpl @Inject constructor(
    private val dao: LessonDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val workManager: WorkManager,
) : LessonRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: throw AuthException("Usuario no autenticado")

    override fun getLessonsByCategory(categoryId: Int): Flow<List<Lesson>> {
        return dao.getLessonsByCategory(categoryId).map { lesson ->
            lesson.map { it.toDomain() }
        }
    }

    override suspend fun insert(lesson: Lesson) {
        dao.insert(lesson.copy(userId = userId, isSynced = false, isDeleted = false).toEntity())
        scheduleSync()
    }

    override suspend fun updateLesson(lesson: Lesson) {
        val entity = lesson.toEntity().copy(
            userId = userId,
            isSynced = false,
            isDeleted = false
        )
        dao.updateLesson(entity)
        scheduleSync()
    }

    override suspend fun deleteLesson(lesson: Lesson) {
        dao.markLessonForDeletion(lesson.id)
        scheduleSync()
    }

    private fun scheduleSync() {
        val syncRequest = OneTimeWorkRequestBuilder<LessonSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(
            "sync_lessons_work",
            ExistingWorkPolicy.KEEP,
            syncRequest
        )
    }

    override suspend fun getUnsyncedLessons(): List<Lesson> {
        return dao.getUnsyncedLessons(userId).map { it.toDomain() }
    }

    override suspend fun uploadLessonToFirestore(lesson: Lesson) {
        val lessonFirestore =
            LessonFirestore(id = lesson.id, title = lesson.tittle, categoryId = lesson.categoryId)
        firestore.collection("users").document(userId)
            .collection("lessons").document(lesson.id.toString())
            .set(lessonFirestore).await()
    }

    override suspend fun deleteLessonFromFirestore(lessonId: Int) {
        firestore.collection("users").document(userId)
            .collection("lessons").document(lessonId.toString())
            .delete().await()
    }

    override suspend fun deleteLessonLocally(lessonId: Int) {
        dao.deleteLessonById(lessonId)
    }

    override suspend fun markLessonAsSynced(lessonId: Int) {
        dao.markLessonAsSynced(lessonId)
    }
}