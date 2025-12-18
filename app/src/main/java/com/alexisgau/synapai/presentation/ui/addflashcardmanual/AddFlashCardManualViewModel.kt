package com.alexisgau.synapai.presentation.ui.addflashcardmanual

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexisgau.synapai.domain.model.FlashCard
import com.alexisgau.synapai.domain.usecase.flashcard.InsertFlashCardUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AddFlashCardManualViewModel @Inject constructor(
    private val insertFlashCardUseCase: InsertFlashCardUseCase,
) : ViewModel() {


    private val _saveState = MutableStateFlow<Resource<Unit>>(Resource.Initial())
    val saveState: StateFlow<Resource<Unit>> = _saveState.asStateFlow()

    fun insertFlashCard(flashCard: FlashCard) {
        viewModelScope.launch {
            try {
                _saveState.value = Resource.Loading()
                insertFlashCardUseCase.invoke(flashCard)
                _saveState.value = Resource.Success(Unit)
            } catch (e: Exception) {
                _saveState.value = Resource.Error("Error saving card: ${e.message}")
            }
        }
    }
}


sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Initial<T> : Resource<T>()
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T> : Resource<T>()
}