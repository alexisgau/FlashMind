package com.example.flashmind.domain.reposotory

import com.example.flashmind.data.local.entities.MultipleChoiceQuestionEntity
import com.example.flashmind.data.local.entities.TestEntity
import com.example.flashmind.domain.model.QuizQuestionModel
import com.example.flashmind.domain.model.TestModel
import kotlinx.coroutines.flow.Flow


interface QuizRepository {

    suspend fun createTest(title: String, lessonId: Int): Long

    suspend fun saveGeneratedQuestions(questions: List<QuizQuestionModel>, testId: Long)
    suspend fun deleteTest(testId: Int)
    fun getQuestionsForTest(testId: Int): Flow<List<QuizQuestionModel>>
    fun getTestsForLesson(lessonId: Int): Flow<List<TestModel>>


    suspend fun getUnsyncedTests(): List<TestEntity>
    suspend fun getUnsyncedQuestions(): List<MultipleChoiceQuestionEntity>
    suspend fun uploadTestToFirestore(test: TestEntity)
    suspend fun uploadQuestionToFirestore(question: MultipleChoiceQuestionEntity)
    suspend fun deleteTestFromFirestore(testId: Int)
    suspend fun deleteTestLocally(testId: Int)
    suspend fun deleteQuestionsLocally(questionIds: List<Int>)
    suspend fun markTestAsSynced(testId: Int)
    suspend fun markQuestionAsSynced(questionId: Int)


}


fun QuizQuestionModel.toEntity(testId: Long, userId: String): MultipleChoiceQuestionEntity {
    return MultipleChoiceQuestionEntity(
        testId = testId.toInt(),
        userId = userId,
        questionText = this.question,
        options = this.options,
        correctAnswerIndex = this.correctResponseIndex,
        isSynced = false,
        isDeleted = false
    )
}