package com.example.flashmind.presentation.ui.test.run

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.QuizQuestionModel
import com.example.flashmind.domain.usecase.test.CreateTestUseCase
import com.example.flashmind.domain.usecase.test.DeleteTestUseCase
import com.example.flashmind.domain.usecase.test.GenerateTestUseCase
import com.example.flashmind.domain.usecase.test.GetQuestionsForTestUseCase
import com.example.flashmind.domain.usecase.test.SaveGeneratedQuestionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val generateTestUseCase: GenerateTestUseCase,
    private val saveGeneratedQuestionsUseCase: SaveGeneratedQuestionsUseCase,
    private val createTestUseCase: CreateTestUseCase,
    private val getQuestionsForTestUseCase: GetQuestionsForTestUseCase,
    private val deleteTestUseCase: DeleteTestUseCase,
) : ViewModel() {

    private val _quizState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val quizState: StateFlow<QuizUiState> = _quizState.asStateFlow()

    private var allQuestions: List<QuizQuestionModel> = emptyList()
    private var currentQuestionIndex = 0
    private var correctAnswers = 0

    private var timerJob: Job? = null
    private val _elapsedTimeSeconds = MutableStateFlow(0L)

    init {
        viewModelScope.launch {
            _elapsedTimeSeconds.collect { time ->
                val currentState = _quizState.value
                if (currentState is QuizUiState.Success) {
                    _quizState.value = currentState.copy(elapsedTimeSeconds = time)
                } else if (currentState is QuizUiState.Finished) {
                    _quizState.value = currentState.copy(elapsedTimeSeconds = time)
                    stopTimer()
                }
            }
        }
    }

    private fun startTimer() {
        stopTimer()
        _elapsedTimeSeconds.value = 0L
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _elapsedTimeSeconds.value++
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun generateAndSaveTest(
        contentFile: String,
        lessonId: Int,
        testTitle: String = "Test Generado",
    ) {
        _quizState.value = QuizUiState.Loading
        stopTimer()

        viewModelScope.launch {
            var newTestId: Long? = null

            try {
                newTestId = createTestUseCase(title = testTitle, lessonId = lessonId)
                if (newTestId <= 0) throw Exception("Error al crear el test en la BD.")

                val generatedQuestions = generateTestUseCase.invoke(contentFile, newTestId.toInt())

                if (generatedQuestions.isNotEmpty()) {
                    saveGeneratedQuestionsUseCase(
                        questions = generatedQuestions,
                        testId = newTestId
                    )
                    allQuestions = generatedQuestions
                    currentQuestionIndex = 0
                    correctAnswers = 0
                    _quizState.value = QuizUiState.Success(
                        test = allQuestions[currentQuestionIndex],
                        currentIndex = currentQuestionIndex,
                        totalQuestions = allQuestions.size
                    )
                    startTimer()
                } else {
                    deleteTestUseCase(newTestId.toInt())
                    _quizState.value =
                        QuizUiState.Error("No questions were raised.\nPlease try again")
                }
            } catch (e: CancellationException) {
                newTestId?.let {
                    try {
                        deleteTestUseCase(it.toInt())
                    } catch (e: Exception) {
                    }
                }
                _quizState.value = QuizUiState.Error("Canceled generation.")
            } catch (e: Exception) {
                Log.e(
                    "QuizViewModel",
                    "Error en generateAndSaveTest. Borrando test huérfano: $newTestId",
                    e
                )
                newTestId?.let {
                    try {
                        deleteTestUseCase(it.toInt())
                    } catch (e: Exception) {
                    }
                }
                _quizState.value = QuizUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadTest(testId: Int) {
        _quizState.value = QuizUiState.Loading
        stopTimer()
        viewModelScope.launch {
            try {
                getQuestionsForTestUseCase(testId)
                    .collectLatest { questionsList ->
                        if (questionsList.isNotEmpty()) {
                            allQuestions = questionsList.shuffled()
                            currentQuestionIndex = 0
                            correctAnswers = 0
                            _quizState.value = QuizUiState.Success(
                                test = allQuestions[currentQuestionIndex],
                                currentIndex = currentQuestionIndex,
                                totalQuestions = allQuestions.size
                            )
                            startTimer()
                        } else {
                            _quizState.value =
                                QuizUiState.Error("Test not found or has no questions.")
                        }
                    }
            } catch (e: Exception) {
                _quizState.value = QuizUiState.Error(e.message ?: "Unknown error loading test")
            }
        }
    }

    fun answerSelected(selectedIndex: Int) {
        val currentQuestion = allQuestions[currentQuestionIndex]
        val isCorrect = selectedIndex == currentQuestion.correctResponseIndex
        val currentState = _quizState.value

        if (isCorrect) correctAnswers++

        if (currentState is QuizUiState.Success && currentState.selectedAnswerIndex == null) {
            _quizState.value = currentState.copy(
                selectedAnswerIndex = selectedIndex,
                isCorrect = isCorrect,
                elapsedTimeSeconds = _elapsedTimeSeconds.value
            )
        }
    }

    fun moveToNextQuestion() {
        val currentState = _quizState.value
        if (currentState is QuizUiState.Success) {
            currentQuestionIndex++
            if (currentQuestionIndex < allQuestions.size) {
                _quizState.value = QuizUiState.Success(
                    test = allQuestions[currentQuestionIndex],
                    currentIndex = currentQuestionIndex,
                    totalQuestions = allQuestions.size,
                    selectedAnswerIndex = null,
                    isCorrect = null,
                    elapsedTimeSeconds = _elapsedTimeSeconds.value
                )
            } else {
                _quizState.value = QuizUiState.Finished(
                    correctAnswers = correctAnswers,
                    totalQuestions = allQuestions.size,
                    elapsedTimeSeconds = _elapsedTimeSeconds.value
                )
            }
        }
    }

    fun restartQuiz() {
        if (allQuestions.isEmpty()) {
            _quizState.value = QuizUiState.Error("Cannot restart, no questions loaded.")
            return
        }
        stopTimer()
        correctAnswers = 0
        currentQuestionIndex = 0
        allQuestions = allQuestions.shuffled()
        _quizState.value = QuizUiState.Success(
            test = allQuestions[currentQuestionIndex],
            currentIndex = currentQuestionIndex,
            totalQuestions = allQuestions.size
        )
        startTimer()
    }


    override fun onCleared() {
        super.onCleared()
        stopTimer()
    }
}

sealed interface QuizUiState {
    data class Success(
        val test: QuizQuestionModel,
        val currentIndex: Int,
        val totalQuestions: Int,
        val selectedAnswerIndex: Int? = null,
        val elapsedTimeSeconds: Long = 0L,
        val isCorrect: Boolean? = null,
    ) : QuizUiState

    data class Error(val error: String) : QuizUiState
    object Loading : QuizUiState
    data class Finished(
        val correctAnswers: Int,
        val totalQuestions: Int,
        val elapsedTimeSeconds: Long = 0L,
    ) : QuizUiState
}