package com.example.flashmind.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.flashmind.data.local.entities.MultipleChoiceQuestionEntity
import com.example.flashmind.data.local.entities.TestEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTests(tests: List<TestEntity>)

    @Query("UPDATE tests SET isDeleted = 1, isSynced = 0 WHERE testId = :testId")
    suspend fun markTestForDeletion(testId: Int)

    @Query("SELECT * FROM tests WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedTests(userId: String): List<TestEntity>

    @Query("UPDATE tests SET isSynced = 1 WHERE testId = :testId")
    suspend fun markTestAsSynced(testId: Int)

    @Query("DELETE FROM tests WHERE testId = :testId")
    suspend fun deleteTestById(testId: Int)

    @Query("SELECT questionId FROM questions WHERE testId = :testId")
    suspend fun getQuestionIdsByTestId(testId: Int): List<Int>

    @Query("SELECT * FROM tests WHERE lessonId = :lessonId AND isDeleted = 0")
    fun getTestsByLessonId(lessonId: Int): Flow<List<TestEntity>>

    // --- Operaciones para Preguntas ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<MultipleChoiceQuestionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllQuestions(questions: List<MultipleChoiceQuestionEntity>)

    @Query("UPDATE questions SET isDeleted = 1, isSynced = 0 WHERE testId = :testId")
    suspend fun markQuestionsForDeletionByTestId(testId: Int)

    @Query("SELECT * FROM questions WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedQuestions(userId: String): List<MultipleChoiceQuestionEntity>

    @Query("UPDATE questions SET isSynced = 1 WHERE questionId = :questionId")
    suspend fun markQuestionAsSynced(questionId: Int)

    @Query("DELETE FROM questions WHERE questionId IN (:questionIds)")
    suspend fun deleteQuestionsByIds(questionIds: List<Int>)

    @Query("SELECT * FROM questions WHERE testId = :testId AND isDeleted = 0")
    fun getQuestionsByTestId(testId: Int): Flow<List<MultipleChoiceQuestionEntity>>
}