package com.alexisgau.synapai.presentation.ui.test.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexisgau.synapai.domain.model.TestModel
import com.alexisgau.synapai.domain.usecase.test.DeleteTestUseCase
import com.alexisgau.synapai.domain.usecase.test.GetTestsForLessonUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface TestsListUiState {
    data class Success(val tests: List<TestModel>) : TestsListUiState
    data class Error(val error: String) : TestsListUiState
    object Loading : TestsListUiState
    object Empty : TestsListUiState
}

@HiltViewModel
class TestViewModel @Inject constructor(
    private val getTestsForLessonUseCase: GetTestsForLessonUseCase,
    private val deleteTestUseCase: DeleteTestUseCase,
) : ViewModel() {

    private val _testsState = MutableStateFlow<TestsListUiState>(TestsListUiState.Loading)
    val testsState: StateFlow<TestsListUiState> = _testsState.asStateFlow()

    private var currentLessonId: Int? = null

    fun loadTestsForLesson(lessonId: Int) {
        if (currentLessonId == lessonId && _testsState.value !is TestsListUiState.Error) {
            return
        }
        currentLessonId = lessonId
        _testsState.value = TestsListUiState.Loading

        viewModelScope.launch {
            getTestsForLessonUseCase(lessonId)
                .catch { e ->
                    Log.e("TestViewModel", "Error loading tests for lesson $lessonId", e)
                    _testsState.value = TestsListUiState.Error(e.message ?: "Failed to load tests")
                }
                .collectLatest { testsList ->
                    _testsState.value = if (testsList.isNotEmpty()) {
                        TestsListUiState.Success(testsList)
                    } else {
                        TestsListUiState.Empty
                    }
                }
        }
    }

    fun deleteTest(testId: Int) {
        val currentState = _testsState.value

        viewModelScope.launch {
            try {
                deleteTestUseCase(testId)
                if (currentState is TestsListUiState.Success) {
                    val updatedList = currentState.tests.filterNot { it.testId == testId }
                    _testsState.value = if (updatedList.isNotEmpty()) {
                        TestsListUiState.Success(updatedList)
                    } else {
                        TestsListUiState.Empty
                    }
                }

            } catch (e: Exception) {

                _testsState.value = TestsListUiState.Error("Failed to delete test: ${e.message}")

            }
        }
    }
}