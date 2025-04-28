package com.example.flashmind.presentation.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.domain.usecase.DeleteFlashCardUseCase
import com.example.flashmind.domain.usecase.EditFlashCardUseCase
import com.example.flashmind.domain.usecase.GenerateFlashCards
import com.example.flashmind.domain.usecase.GetFlashCardsByIdUseCase
import com.example.flashmind.domain.usecase.GetFlashCardsUseCase
import com.example.flashmind.domain.usecase.InsertFlashCardUseCase
import com.example.flashmind.domain.usecase.SaveGeneratedFlashcardsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashCardViewModel @Inject constructor(
    private val getFlashCardsUseCase: GetFlashCardsUseCase,
    private val generateFlashCards: GenerateFlashCards,
    private val saveGeneratedFlashcardsUseCase: SaveGeneratedFlashcardsUseCase,
    private val deleteFlashCardUseCase: DeleteFlashCardUseCase,
    private val editFlashCardUseCase: EditFlashCardUseCase,
    private val getFlashCardsByIdUseCase: GetFlashCardsByIdUseCase,
    private val insertFlashCardUseCase: InsertFlashCardUseCase
) : ViewModel() {

    private val _flashCardAiState = MutableStateFlow<FlashCardAiState>(FlashCardAiState.Init)
    val flashCardAiState: StateFlow<FlashCardAiState> = _flashCardAiState.asStateFlow()

    private val _generatedFlashcards = mutableStateListOf<FlashCard>()
    val generatedFlashcards: List<FlashCard> get() = _generatedFlashcards

    private val _flashCards = MutableStateFlow<List<FlashCard>>(emptyList())
    val flashCards: StateFlow<List<FlashCard>> = _flashCards.asStateFlow()

    private val _flashCardState = MutableStateFlow<FlashCardUiState>(FlashCardUiState.Loading)
    val flashCardState: StateFlow<FlashCardUiState> = _flashCardState.asStateFlow()

    private val _addCardState = MutableStateFlow<FlashCardUiState>(FlashCardUiState.Loading)
    val addCardState: StateFlow<FlashCardUiState> = _addCardState.asStateFlow()

    private var latestGeneratedFlashcards: List<FlashCard> = emptyList()


    fun getFlashCards(lessonId: Int) {
        viewModelScope.launch {
            try {
                val flashCard = getFlashCardsUseCase.invoke(lessonId)
                flashCard.collect {

                    _flashCards.value = it
                }
            } catch (e: Exception) {
                Log.e("FlashCardViewModel", "error: $e")
            }
        }
    }

    fun getFlashCardById(id: Int) {
        viewModelScope.launch {
            try {
                val flashCard = getFlashCardsByIdUseCase(id)
                _flashCardState.value = FlashCardUiState.Success(flashCard)
            } catch (e: Exception) {
                _flashCardState.value =
                    FlashCardUiState.Error("Error al cargar tarjeta, ${e.message}")
            }
        }
    }

    fun generateFlashCards(text: String, lessonId: Int) {
        _flashCardAiState.value = FlashCardAiState.Loading
        viewModelScope.launch {
            try {
                val generatedList = generateFlashCards.invoke(text, lessonId)
                if (generatedList.isNotEmpty()) {
                    latestGeneratedFlashcards = generatedList
                    _generatedFlashcards.clear()
                    _generatedFlashcards.addAll(generatedList)
                    _flashCardAiState.value = FlashCardAiState.Success(generatedList)
                } else {
                    _flashCardAiState.value = FlashCardAiState.Error("Lista vacía.")
                }
            } catch (e: Exception) {
                _flashCardAiState.value = FlashCardAiState.Error(e.message ?: "Error desconocido.")
            }
        }
    }

    fun editFlashcard(flashCard: FlashCard) {

        viewModelScope.launch {

            try {
                editFlashCardUseCase.invoke(flashCard)

            } catch (e: Exception) {

            }


        }

    }

    fun saveGeneratedFlashcards() {
        viewModelScope.launch {
            if (_generatedFlashcards.isNotEmpty()) {
                saveGeneratedFlashcardsUseCase(_generatedFlashcards.toList())
                _flashCardAiState.value = FlashCardAiState.Saved
            }
        }
    }

    fun saveFlashCard(flashCard: FlashCard) {

        viewModelScope.launch {

            try {
                insertFlashCardUseCase.invoke(flashCard)
                _addCardState.value = FlashCardUiState.Success(flashCard)
            } catch (e: Exception) {
                _addCardState.value = FlashCardUiState.Error("error $e.message")
            }

        }


    }


    fun removeFlashCard(card: FlashCard) {
        _generatedFlashcards.remove(card)
    }

    fun deleleteFlashCard(flashCard: FlashCard) {

        viewModelScope.launch {

            try {
                deleteFlashCardUseCase.invoke(flashCard)

            } catch (e: Exception) {
                Log.e("ViewModel", "error: $e")
            }

        }

    }
}


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


