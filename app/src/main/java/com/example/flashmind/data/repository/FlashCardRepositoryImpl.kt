package com.example.flashmind.data.repository

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.flashmind.data.local.dao.FlashCardDao
import com.example.flashmind.data.local.entities.toDomain
import com.example.flashmind.data.local.entities.toEntity
import com.example.flashmind.data.network.dto.FlashCardFirestore
import com.example.flashmind.data.worker.FlashCardSyncWorker
import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.domain.reposotory.FlashCardRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FlashCardRepositoryImpl @Inject constructor(
    private val dao: FlashCardDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val workManager: WorkManager,
) :
    FlashCardRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: throw AuthException("Usuario no autenticado")

    override suspend fun insert(flashCard: FlashCard) {
        dao.insert(flashCard.copy(userId = userId).toEntity())
        scheduleSync()
    }

    override suspend fun delete(flashCard: FlashCard) {
        dao.delete(flashCard.toEntity())
        scheduleSync()
    }

    override suspend fun update(flashCard: FlashCard) {
        dao.update(flashCard.toEntity())
        scheduleSync()
    }

    override suspend fun saveGeneratedFlashcards(flashcards: List<FlashCard>) {
        val currentUserId = auth.currentUser?.uid ?: run {

            throw Exception("Usuario no autenticado al guardar flashcards")
        }
        val entities = flashcards.map { flashCard ->
            flashCard.toEntity().copy(
                userId = currentUserId,
                isSynced = false,
                isDeleted = false
            )
        }

        dao.insertAll(entities)

        scheduleSync()
    }

    private fun scheduleSync() {
        val syncRequest = OneTimeWorkRequestBuilder<FlashCardSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(
            "sync_flashcards_work",
            ExistingWorkPolicy.KEEP,
            syncRequest
        )
    }

    override fun getFlashCardsByLesson(lessonId: Int): Flow<List<FlashCard>> {
        return dao.getFlashCardsByLesson(lessonId).map { flashCard ->
            flashCard.map { it.toDomain() }
        }
    }

    override suspend fun getFlashCardsById(id: Int): FlashCard {
        return dao.getFlashCardsById(id).toDomain()
    }

    override suspend fun getUnsyncedFlashcards(): List<FlashCard> {
        return dao.getUnsyncedFlashcards(userId).map { it.toDomain() }
    }

    override suspend fun uploadFlashcardToFirestore(flashCard: FlashCard) {
        val flashCardFirestore = FlashCardFirestore(
            id = flashCard.id,
            question = flashCard.question,
            answer = flashCard.answer,
            lessonId = flashCard.lessonId,
            color = flashCard.color
        )
        firestore.collection("users").document(userId)
            .collection("flashcards").document("FlashCard: ${flashCard.id}")
            .set(flashCardFirestore).await()
    }

    override suspend fun deleteFlashcardFromFirestore(flashcardId: Int) {
        firestore.collection("users").document(userId)
            .collection("flashcards").document("FlashCard: $flashcardId")
            .delete().await()
    }

    override suspend fun deleteFlashcardLocally(flashcardId: Int) {
        dao.deleteFlashcardById(flashcardId)
    }

    override suspend fun markFlashcardAsSynced(flashcardId: Int) {
        dao.markFlashcardAsSynced(flashcardId)
    }
}