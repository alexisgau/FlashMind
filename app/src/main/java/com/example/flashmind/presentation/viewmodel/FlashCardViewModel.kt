package com.example.flashmind.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FlashCardViewModel @Inject constructor(): ViewModel() {




}


sealed interface FlashCardState{

    object Loading:FlashCardState
    object Success:FlashCardState
    data class Error(val error: String):FlashCardState

}