package com.example.flashmind.presentation.ui.editflashcard

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.presentation.ui.addflashcard.AddFlashCardForm
import com.example.flashmind.presentation.viewmodel.FlashCardUiState
import com.example.flashmind.presentation.viewmodel.FlashCardViewModel

@Composable
fun EditFlashCardScreen(flashCardId: Int, navigateToFlashCardsScreen:()-> Unit, viewModel: FlashCardViewModel = hiltViewModel()){

    Log.i("id", "$flashCardId")

    viewModel.getFlashCardById(flashCardId)
    val flashCardState by viewModel.flashCardState.collectAsStateWithLifecycle()


    when (flashCardState) {
        is FlashCardUiState.Loading -> {
            CircularProgressIndicator()
        }
        is FlashCardUiState.Success -> {
            val flashCard = (flashCardState as FlashCardUiState.Success).flashCard

            var question by remember { mutableStateOf(flashCard.question) }
            var answer by remember { mutableStateOf(flashCard.answer) }
            var selectedColor by remember { mutableStateOf(flashCard.color) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                AddFlashCardForm(
                    question = question,
                    onQuestionChange = { question = it },
                    answer = answer,
                    onAnswerChange = { answer = it },
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it }
                )


                Button(
                    onClick = {
                        val updatedFlashCard = flashCard.copy(
                            question = question,
                            answer = answer,
                            color = selectedColor
                        )
                        viewModel.editFlashcard(updatedFlashCard)
                        navigateToFlashCardsScreen()
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("SAVE")
                }
            }
        }

        is FlashCardUiState.Error -> {
            Text("Error al cargar la tarjeta")
        }
    }


}