package com.alexisgau.synapai.presentation.ui.editflashcard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexisgau.synapai.domain.model.FlashCard
import com.alexisgau.synapai.domain.usecase.flashcard.EditFlashCardUseCase
import com.alexisgau.synapai.domain.usecase.flashcard.GetFlashCardsByIdUseCase
import com.alexisgau.synapai.presentation.ui.flashcard.FlashCardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditFlashCardViewModel @Inject constructor(
    private val getFlashCardsByIdUseCase: GetFlashCardsByIdUseCase,
    private val editFlashCardUseCase: EditFlashCardUseCase,
) : ViewModel() {


    private val _flashCardState = MutableStateFlow<FlashCardUiState>(FlashCardUiState.Loading)
    val flashCardState: StateFlow<FlashCardUiState> = _flashCardState.asStateFlow()

    fun loadFlashCardById(id: Int) {
        viewModelScope.launch {
            try {
                val flashCard = getFlashCardsByIdUseCase.invoke(id)
                _flashCardState.value = FlashCardUiState.Success(flashCard)
            } catch (e: Exception) {
                _flashCardState.value = FlashCardUiState.Error("Error loading card: ${e.message}")
            }
        }
    }

    fun editFlashCard(flashCard: FlashCard) {
        viewModelScope.launch {
            try {

                editFlashCardUseCase.invoke(flashCard)
            } catch (e: Exception) {
                Log.e("EditFlashCardViewModel", "Error editing card: $e")

            }
        }
    }
}