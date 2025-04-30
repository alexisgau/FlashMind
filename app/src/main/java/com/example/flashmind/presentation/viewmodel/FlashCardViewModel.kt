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
import com.example.flashmind.presentation.ui.flashcard.FlashCardAiState
import com.example.flashmind.presentation.ui.flashcard.FlashCardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
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

    private val _flashCards = MutableStateFlow<List<FlashCard>>(emptyList())
    val flashCards: StateFlow<List<FlashCard>> = _flashCards.asStateFlow()

    private val _flashCardState = MutableStateFlow<FlashCardUiState>(FlashCardUiState.Loading)
    val flashCardState: StateFlow<FlashCardUiState> = _flashCardState.asStateFlow()

    private val _generatedFlashcards = mutableStateListOf<FlashCard>()
    val generatedFlashcards: List<FlashCard> get() = _generatedFlashcards

    private var latestGeneratedFlashcards: List<FlashCard> = emptyList()

    fun loadFlashCardsByLesson(lessonId: Int) {
        viewModelScope.launch {
            getFlashCardsUseCase.invoke(lessonId)
                .catch { e ->
                    Log.e("FlashCardViewModel", "Error al cargar tarjetas: $e")
                }
                .collect { cards ->
                    _flashCards.value = cards
                }
        }
    }

    fun loadFlashCardById(id: Int) {
        viewModelScope.launch {
            try {
                val flashCard = getFlashCardsByIdUseCase.invoke(id)
                _flashCardState.value = FlashCardUiState.Success(flashCard)
            } catch (e: Exception) {
                _flashCardState.value = FlashCardUiState.Error("Error al cargar tarjeta: ${e.message}")
            }
        }
    }

    fun generateFlashCards(text: String, lessonId: Int) {
        _flashCardAiState.value = FlashCardAiState.Loading
        viewModelScope.launch {
            try {
                val generatedList = generateFlashCards.invoke(text, lessonId)
                if (generatedList.isEmpty()) {
                    _flashCardAiState.value = FlashCardAiState.Error("La lista generada está vacía.")
                    return@launch
                }

                latestGeneratedFlashcards = generatedList
                _generatedFlashcards.clear()
                _generatedFlashcards.addAll(generatedList)
                _flashCardAiState.value = FlashCardAiState.Success(generatedList)

            } catch (e: Exception) {
                _flashCardAiState.value = FlashCardAiState.Error(e.message ?: "Error desconocido.")
            }
        }
    }

    fun saveGeneratedFlashcards() {
        if (_generatedFlashcards.isEmpty()) return
        viewModelScope.launch {
            saveGeneratedFlashcardsUseCase.invoke(_generatedFlashcards.toList())
            _flashCardAiState.value = FlashCardAiState.Saved
        }
    }

    fun insertFlashCard(flashCard: FlashCard) {
        viewModelScope.launch {
            try {
                _flashCardState.value = FlashCardUiState.Loading
                insertFlashCardUseCase.invoke(flashCard)
                _flashCardState.value = FlashCardUiState.Success(flashCard)

            } catch (e: Exception) {
                _flashCardState.value = FlashCardUiState.Error("Error al guardar: ${e.message}")
            }
        }
    }


    fun editFlashCard(flashCard: FlashCard) {
        viewModelScope.launch {
            try {
                editFlashCardUseCase.invoke(flashCard)
            } catch (e: Exception) {
                Log.e("FlashCardViewModel", "Error al editar tarjeta: $e")
            }
        }
    }

    fun deleteFlashCard(flashCard: FlashCard) {
        viewModelScope.launch {
            try {
                deleteFlashCardUseCase.invoke(flashCard)
            } catch (e: Exception) {
                Log.e("FlashCardViewModel", "Error al eliminar tarjeta: $e")
            }
        }
    }

    fun removeFromGenerated(card: FlashCard) {
        _generatedFlashcards.remove(card)
    }
}





