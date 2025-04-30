package com.example.flashmind.presentation.ui.flashcard

import com.example.flashmind.domain.model.FlashCard

sealed interface FlashCardAiState {

    object Loading : FlashCardAiState
    object Saved : FlashCardAiState
    object Init : FlashCardAiState
    data class Success(val list: List<FlashCard>) : FlashCardAiState
    data class Error(val error: String) : FlashCardAiState

}

sealed class FlashCardUiState {
    object Loading : FlashCardUiState()
    data class Success(val flashCard: FlashCard) : FlashCardUiState()
    data class Error(val message: String) : FlashCardUiState()
}
