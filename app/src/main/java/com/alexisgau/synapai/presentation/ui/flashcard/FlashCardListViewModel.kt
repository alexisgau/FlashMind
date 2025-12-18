package com.alexisgau.synapai.presentation.ui.flashcard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexisgau.synapai.domain.model.FlashCard
import com.alexisgau.synapai.domain.usecase.flashcard.DeleteFlashCardUseCase
import com.alexisgau.synapai.domain.usecase.flashcard.GetFlashCardsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class FlashCardListViewModel @Inject constructor(
    private val getFlashCardsUseCase: GetFlashCardsUseCase,
    private val deleteFlashCardUseCase: DeleteFlashCardUseCase,
) : ViewModel() {

    private val _flashCards = MutableStateFlow<List<FlashCard>>(emptyList())
    val flashCards: StateFlow<List<FlashCard>> = _flashCards.asStateFlow()

    fun loadFlashCardsByLesson(lessonId: Int) {
        viewModelScope.launch {
            getFlashCardsUseCase.invoke(lessonId)
                .catch { e ->
                    Log.e("FlashCardListViewModel", "Error loading cards: $e")
                }
                .collect { cards ->
                    _flashCards.value = cards
                }
        }
    }

    fun deleteFlashCard(flashCard: FlashCard) {
        viewModelScope.launch {
            try {
                deleteFlashCardUseCase.invoke(flashCard)
            } catch (e: Exception) {
                Log.e("FlashCardListViewModel", "Error deleting card: $e")
            }
        }
    }
}