package com.example.flashmind.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.domain.usecase.GenerateFlashCards
import com.example.flashmind.domain.usecase.GetFlashCardsUseCase
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
    private val saveGeneratedFlashcardsUseCase: SaveGeneratedFlashcardsUseCase
) : ViewModel() {

    private val _flashCardAiState = MutableStateFlow<FlashCardAiState>(FlashCardAiState.Loading)
    val flashCardAiState: StateFlow<FlashCardAiState> = _flashCardAiState.asStateFlow()

    private val _flashCards = MutableStateFlow<List<FlashCard>>(emptyList())
    val flashCards: StateFlow<List<FlashCard>> = _flashCards.asStateFlow()

    private var latestGeneratedFlashcards: List<FlashCard> = emptyList()


     fun getFlashCards(lessonId: Int){
        viewModelScope.launch {
            try {
                val flashCard = getFlashCardsUseCase.invoke(lessonId)
                flashCard.collect {

                    _flashCards.value = it
                }
            }catch (e: Exception){
                Log.e("FlashCardViewModel", "error: $e")
            }
        }
    }

    fun generateFlashCards(text: String, lessonId: Int) {
        viewModelScope.launch {
            try {
                val generatedList = generateFlashCards.invoke(text, lessonId)
                if (generatedList.isNotEmpty()) {
                    latestGeneratedFlashcards = generatedList
                    _flashCardAiState.value = FlashCardAiState.Success(generatedList)
                } else {
                    _flashCardAiState.value = FlashCardAiState.Error("Lista vacía.")
                }
            } catch (e: Exception) {
                _flashCardAiState.value = FlashCardAiState.Error(e.message ?: "Error desconocido.")
            }
        }
    }

    fun saveGeneratedFlashcards() {
        viewModelScope.launch {
            if (latestGeneratedFlashcards.isNotEmpty()) {
                saveGeneratedFlashcardsUseCase(latestGeneratedFlashcards)
                // Podés emitir un estado "Guardado con éxito" si querés mostrar feedback en la UI
            }
        }
    }
}



sealed interface FlashCardAiState {

    object Loading : FlashCardAiState
    object Saved : FlashCardAiState
    data class Success(val list: List<FlashCard>) : FlashCardAiState
    data class Error(val error: String) : FlashCardAiState

}

