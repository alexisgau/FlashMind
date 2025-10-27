package com.example.flashmind.presentation.ui.summary

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.SummaryModel
import com.example.flashmind.domain.usecase.summary.DeleteSummaryUseCase
import com.example.flashmind.domain.usecase.summary.GetSummariesForLessonUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface SummariesListUiState {
    data class Success(val summaries: List<SummaryModel>) : SummariesListUiState
    data class Error(val error: String) : SummariesListUiState
    object Loading : SummariesListUiState
    object Empty : SummariesListUiState

}

@HiltViewModel
class SummariesListViewModel @Inject constructor(
    private val getSummariesForLessonUseCase: GetSummariesForLessonUseCase,
    private val deleteSummaryUseCase: DeleteSummaryUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SummariesListUiState>(SummariesListUiState.Loading)
    val uiState: StateFlow<SummariesListUiState> = _uiState.asStateFlow()

    private var currentLessonId: Int? = null

    // Loads summaries for a given lesson
    fun loadSummaries(lessonId: Int) {
        // Avoid reloading if already loading or showing data for the same lesson
        if (currentLessonId == lessonId && _uiState.value !is SummariesListUiState.Error) {
            Log.d("SummariesListViewModel", "Skipping load for lesson $lessonId, already loaded or loading.")
            return
        }

        currentLessonId = lessonId
        _uiState.value = SummariesListUiState.Loading

        viewModelScope.launch {
            getSummariesForLessonUseCase(lessonId)
                .catch { e ->
                    Log.e("SummariesListViewModel", "Error loading summaries for lesson $lessonId", e)
                    _uiState.value = SummariesListUiState.Error(e.message ?: "Failed to load summaries")
                }
                .collectLatest { summariesList ->
                    Log.i("SummariesListViewModel", "Loaded ${summariesList.size} summaries for lesson $lessonId")
                    _uiState.value = if (summariesList.isNotEmpty()) {
                        SummariesListUiState.Success(summariesList)
                    } else {
                        SummariesListUiState.Empty
                    }
                }
        }
    }


    fun deleteSummary(summaryId: Int) {
        val lessonId = currentLessonId ?: return
        val currentState = _uiState.value

        viewModelScope.launch {
            try {
                deleteSummaryUseCase(summaryId)
                Log.i("SummariesListViewModel", "Summary $summaryId marked for deletion.")

                // Option 1: Optimistically remove from current state (faster UI)
                if (currentState is SummariesListUiState.Success) {
                    val updatedList = currentState.summaries.filterNot { it.summaryId == summaryId }
                    _uiState.value = if (updatedList.isNotEmpty()) {
                        SummariesListUiState.Success(updatedList)
                    } else {
                        SummariesListUiState.Empty
                    }
                } else {
                    // Option 2: Or simply reload the list (simpler, slightly slower UI)
                    // loadSummaries(lessonId)
                }

            } catch (e: Exception) {
                Log.e("SummariesListViewModel", "Error deleting summary $summaryId", e)
                _uiState.value = SummariesListUiState.Error("Failed to delete summary: ${e.message}")

            }
        }
    }
}