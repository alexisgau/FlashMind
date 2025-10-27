package com.example.flashmind.domain.usecase.test
import com.example.flashmind.domain.model.QuizQuestionModel
import com.example.flashmind.domain.reposotory.QuizRepository
import javax.inject.Inject

class SaveGeneratedQuestionsUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(questions: List<QuizQuestionModel>, testId: Long) {
        repository.saveGeneratedQuestions(questions = questions, testId = testId)
    }
}