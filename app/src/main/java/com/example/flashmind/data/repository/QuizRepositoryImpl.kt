package com.example.flashmind.data.repository

import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.flashmind.data.local.dao.QuizDao
import com.example.flashmind.data.local.entities.MultipleChoiceQuestionEntity
import com.example.flashmind.data.local.entities.TestEntity
import com.example.flashmind.data.local.entities.toDomain
import com.example.flashmind.data.network.dto.QuestionFirestore
import com.example.flashmind.data.network.dto.TestFirestore
import com.example.flashmind.domain.model.QuizQuestionModel
import com.example.flashmind.domain.model.TestModel
import com.example.flashmind.domain.model.toDomain
import com.example.flashmind.domain.reposotory.QuizRepository
import com.example.flashmind.domain.reposotory.toEntity
import com.example.flashmind.data.worker.QuizSyncWorker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val dao: QuizDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val workManager: WorkManager,
) : QuizRepository {

    private val userId: String
        get() = auth.currentUser?.uid ?: "anonymous_user"


    override suspend fun createTest(title: String, lessonId: Int): Long {
        val testEntity = TestEntity(
            title = title,
            lessonId = lessonId,
            userId = userId,
            creationDate = System.currentTimeMillis(),
            isSynced = false,
            isDeleted = false
        )
        val testId = dao.insertTest(testEntity)
        scheduleSync()
        return testId
    }

    override suspend fun saveGeneratedQuestions(questions: List<QuizQuestionModel>, testId: Long) {
        val questionsWithUserId = questions.map {
            it.toEntity(testId = testId, userId = userId)
        }
        dao.insertQuestions(questionsWithUserId)
        scheduleSync()
    }


    override suspend fun deleteTest(testId: Int) {
        dao.markTestForDeletion(testId)
        dao.markQuestionsForDeletionByTestId(testId)
        scheduleSync()
    }

    override fun getQuestionsForTest(testId: Int): Flow<List<QuizQuestionModel>> {
        return dao.getQuestionsByTestId(testId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getTestsForLesson(lessonId: Int): Flow<List<TestModel>> {
        return dao.getTestsByLessonId(lessonId).map { testEntities ->
            testEntities.map { it.toDomain() }
        }
    }


    private fun scheduleSync() {
        val syncRequest = OneTimeWorkRequestBuilder<QuizSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag("quiz_sync_work_tag")
            .build()

        // KEEP evita que se encolen trabajos duplicados si ya hay uno pendiente
        workManager.enqueueUniqueWork(
            "sync_quiz_work",
            ExistingWorkPolicy.KEEP,
            syncRequest
        )
    }

    override suspend fun getUnsyncedTests(): List<TestEntity> {
        return dao.getUnsyncedTests(userId)
    }

    override suspend fun getUnsyncedQuestions(): List<MultipleChoiceQuestionEntity> {
        return dao.getUnsyncedQuestions(userId)
    }

    override suspend fun uploadTestToFirestore(test: TestEntity) {
        val testFirestore = TestFirestore(
            testId = test.testId,
            lessonId = test.lessonId,
            title = test.title,
            userId = test.userId,
            creationDate = test.creationDate
        )
        firestore.collection("users").document(userId)
            .collection("tests").document("Test_${test.testId}")
            .set(testFirestore).await()
    }

    override suspend fun uploadQuestionToFirestore(question: MultipleChoiceQuestionEntity) {
        val questionFirestore = QuestionFirestore(
            questionId = question.questionId,
            testId = question.testId,
            questionText = question.questionText,
            options = question.options,
            correctAnswerIndex = question.correctAnswerIndex,
            userId = question.userId
        )
        firestore.collection("users").document(userId)
            .collection("questions").document("Question_${question.questionId}")
            .set(questionFirestore).await()
    }

    override suspend fun deleteTestFromFirestore(testId: Int) {
        firestore.collection("users").document(userId)
            .collection("tests").document("Test_$testId")
            .delete().await()

        val querySnapshot = firestore.collection("users").document(userId)
            .collection("questions").whereEqualTo("testId", testId)
            .get().await()

        val batch = firestore.batch()
        querySnapshot.documents.forEach { doc ->
            batch.delete(doc.reference)
        }
        batch.commit().await()
    }


    override suspend fun deleteTestLocally(testId: Int) {
        val questionIds = dao.getQuestionIdsByTestId(testId)
        if (questionIds.isNotEmpty()) {
            dao.deleteQuestionsByIds(questionIds)
        }
        dao.deleteTestById(testId)
    }

    override suspend fun deleteQuestionsLocally(questionIds: List<Int>) {
        if (questionIds.isNotEmpty()) {
            dao.deleteQuestionsByIds(questionIds)
        }
    }


    override suspend fun markTestAsSynced(testId: Int) {
        dao.markTestAsSynced(testId)
    }

    override suspend fun markQuestionAsSynced(questionId: Int) {
        dao.markQuestionAsSynced(questionId)
    }
}