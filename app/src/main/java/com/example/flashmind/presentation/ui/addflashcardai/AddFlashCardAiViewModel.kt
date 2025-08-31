package com.example.flashmind.presentation.ui.addflashcardai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.domain.usecase.flashcard.GenerateFlashCardsUseCase
import com.example.flashmind.domain.usecase.flashcard.SaveGeneratedFlashcardsUseCase
import com.example.flashmind.presentation.ui.flashcard.FlashCardAiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddFlashCardAiViewModel @Inject constructor(
    private val generateFlashCardsUseCase: GenerateFlashCardsUseCase,
    private val saveGeneratedFlashcardsUseCase: SaveGeneratedFlashcardsUseCase
) : ViewModel() {

    private val _flashCardAiState = MutableStateFlow<FlashCardAiState>(FlashCardAiState.Init)
    val flashCardAiState: StateFlow<FlashCardAiState> = _flashCardAiState.asStateFlow()


    private val _generatedFlashcards = MutableStateFlow<List<FlashCard>>(emptyList())
    val generatedFlashcards: StateFlow<List<FlashCard>> = _generatedFlashcards.asStateFlow()

    fun generateFlashCards(text: String, lessonId: Int) {
        _flashCardAiState.value = FlashCardAiState.Loading
        viewModelScope.launch {
            try {
                val generatedList = generateFlashCardsUseCase.invoke(text, lessonId)
                if (generatedList.isEmpty()) {
                    _flashCardAiState.value = FlashCardAiState.Error("The generated list is empty, please try again.")
                    return@launch
                }
                _generatedFlashcards.value = generatedList
                _flashCardAiState.value = FlashCardAiState.Success(generatedList)

            } catch (e: Exception) {
                _flashCardAiState.value = FlashCardAiState.Error(e.message ?: "Unknown error.")
            }
        }
    }

    fun saveGeneratedFlashcards() {
        if (_generatedFlashcards.value.isEmpty()) return
        viewModelScope.launch {
            saveGeneratedFlashcardsUseCase.invoke(_generatedFlashcards.value)
            _flashCardAiState.value = FlashCardAiState.Saved
        }
    }

    fun removeFromGenerated(card: FlashCard) {
        _generatedFlashcards.value = _generatedFlashcards.value.filter { it != card }
    }
}