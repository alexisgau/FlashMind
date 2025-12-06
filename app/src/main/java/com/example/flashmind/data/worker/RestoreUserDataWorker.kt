package com.example.flashmind.data.worker


import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.flashmind.data.local.dao.CategoryDao
import com.example.flashmind.data.local.dao.FlashCardDao
import com.example.flashmind.data.local.dao.LessonDao
import com.example.flashmind.data.local.dao.QuizDao
import com.example.flashmind.data.local.dao.SummaryDao
import com.example.flashmind.data.local.entities.CategoryEntity
import com.example.flashmind.data.local.entities.FlashCardEntity
import com.example.flashmind.data.local.entities.LessonEntity
import com.example.flashmind.data.local.entities.MultipleChoiceQuestionEntity
import com.example.flashmind.data.local.entities.SummaryEntity
import com.example.flashmind.data.local.entities.TestEntity
import com.example.flashmind.data.network.dto.CategoryFirestore
import com.example.flashmind.data.network.dto.FlashCardFirestore
import com.example.flashmind.data.network.dto.LessonFirestore
import com.example.flashmind.data.network.dto.QuestionFirestore
import com.example.flashmind.data.network.dto.SummaryFirestore
import com.example.flashmind.data.network.dto.TestFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class RestoreUserDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val categoryDao: CategoryDao,
    private val lessonDao: LessonDao,
    private val flashCardDao: FlashCardDao,
    private val summaryDao: SummaryDao,
    private val quizDao: QuizDao,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val userId = auth.currentUser?.uid ?: return Result.failure()

        return try {
            val categoriesSnapshot = firestore.collection("users").document(userId)
                .collection("categories").get().await()

            val categories = categoriesSnapshot.documents.mapNotNull { doc ->
                val data = doc.toObject(CategoryFirestore::class.java)
                data?.let {
                    CategoryEntity(
                        id = it.id,
                        name = it.name,
                        description = it.description,
                        color = it.color,
                        userId = userId,
                        isSynced = true,
                        isDeleted = false
                    )
                }
            }

            // Insertamos Categorías PRIMERO
            if (categories.isNotEmpty()) {
                categoryDao.insertAll(categories)
            } else {
                return Result.success()
            }

            val validCategoryIds = categories.map { it.id }.toSet()


            val lessonsSnapshot = firestore.collection("users").document(userId)
                .collection("lessons").get().await()

            val lessons = lessonsSnapshot.documents.mapNotNull { doc ->
                val data = doc.toObject(LessonFirestore::class.java)
                if (data != null && data.categoryId in validCategoryIds) {
                    LessonEntity(
                        id = data.id,
                        title = data.title,
                        categoryId = data.categoryId,
                        userId = userId,
                        isSynced = true,
                        isDeleted = false
                    )
                } else null
            }

            if (lessons.isNotEmpty()) {
                lessonDao.insertAll(lessons)
            }

            val validLessonIds = lessons.map { it.id }.toSet()

            // Restaurar Flashcards
            val flashcardsSnapshot = firestore.collection("users").document(userId)
                .collection("flashcards").get().await()

            val flashcards = flashcardsSnapshot.documents.mapNotNull { doc ->
                val data = doc.toObject(FlashCardFirestore::class.java)
                if (data != null && data.lessonId in validLessonIds) {
                    FlashCardEntity(
                        id = data.id,
                        question = data.question,
                        answer = data.answer,
                        color = data.color,
                        lessonId = data.lessonId,
                        userId = userId,
                        isSynced = true,
                        isDeleted = false
                    )
                } else null
            }
            if (flashcards.isNotEmpty()) flashCardDao.insertAll(flashcards)


            // B. Restaurar Summaries
            val summariesSnapshot = firestore.collection("users").document(userId)
                .collection("summaries").get().await()

            val summaries = summariesSnapshot.documents.mapNotNull { doc ->
                val data = doc.toObject(SummaryFirestore::class.java)
                if (data != null && data.lessonId in validLessonIds) {
                    SummaryEntity(
                        summaryId = data.summaryId,
                        lessonId = data.lessonId,
                        generatedSummary = data.generatedSummary,
                        title = data.title,
                        originalText = "",
                        creationDate = data.creationDate ?: 0L,
                        userId = userId,
                        isSynced = true,
                        isDeleted = false
                    )
                } else null
            }
            if (summaries.isNotEmpty()) summaryDao.insertAll(summaries)


            // C. Restaurar Tests
            val testsSnapshot = firestore.collection("users").document(userId)
                .collection("tests").get().await()

            val tests = testsSnapshot.documents.mapNotNull { doc ->
                val data = doc.toObject(TestFirestore::class.java)
                if (data != null && data.lessonId in validLessonIds) {
                    TestEntity(
                        testId = data.testId,
                        lessonId = data.lessonId,
                        title = data.title,
                        creationDate = data.creationDate ?: 0L,
                        userId = userId,
                        isSynced = true,
                        isDeleted = false
                    )
                } else null
            }

            if (tests.isNotEmpty()) {
                quizDao.insertAllTests(tests)
            }

            val validTestIds = tests.map { it.testId }.toSet()


            val questionsSnapshot = firestore.collection("users").document(userId)
                .collection("questions").get().await()

            val questions = questionsSnapshot.documents.mapNotNull { doc ->
                val data = doc.toObject(QuestionFirestore::class.java)
                if (data != null && data.testId in validTestIds) {
                    MultipleChoiceQuestionEntity(
                        questionId = data.questionId,
                        testId = data.testId,
                        questionText = data.questionText,
                        options = data.options,
                        correctAnswerIndex = data.correctAnswerIndex,
                        userId = userId,
                        isSynced = true,
                        isDeleted = false
                    )
                } else null
            }

            if (questions.isNotEmpty()) {
                quizDao.insertAllQuestions(questions)
            }

            Result.success()

        } catch (e: Exception) {
            Log.e("RestoreWorker", "Error crítico restaurando datos", e)
            if (e is android.database.sqlite.SQLiteConstraintException) {
                return Result.failure()
            }
            Result.retry()
        }
    }
}