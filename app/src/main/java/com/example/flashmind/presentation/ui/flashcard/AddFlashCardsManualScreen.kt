package com.example.flashmind.presentation.ui.flashcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.presentation.viewmodel.FlashCardViewModel

@Composable
fun AddFlashCardsManualScreen(
    lessonId: Int,
    navigateToFlashCards: (Int) -> Unit,
    viewModel: FlashCardViewModel = hiltViewModel()
) {
    val addCardState by viewModel.flashCardState.collectAsStateWithLifecycle()
    var question by rememberSaveable { mutableStateOf("") }
    var answer by rememberSaveable { mutableStateOf("") }
    var selectedColor by rememberSaveable { mutableStateOf("#FF5733") }
    var showError by remember { mutableStateOf(false) }


    LaunchedEffect(addCardState) {
        println("Estado actual: $addCardState")
        when (addCardState) {
            is FlashCardUiState.Error -> showError = true
            FlashCardUiState.Loading -> {}
            is FlashCardUiState.Success -> navigateToFlashCards(lessonId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Crear nueva Flashcard",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        AddFlashCardForm(
            question = question,
            onQuestionChange = { question = it },
            answer = answer,
            onAnswerChange = { answer = it },
            selectedColor = selectedColor,
            onColorSelected = { selectedColor = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (showError) {
            Text(
                text = "Por favor completa la pregunta y la respuesta.",
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                if (question.isNotBlank() && answer.isNotBlank()) {
                    viewModel.insertFlashCard(
                        FlashCard(
                            id = 0,
                            question = question,
                            answer = answer,
                            color = selectedColor,
                            lessonId = lessonId
                        )
                    )
                } else {
                    showError = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = addCardState != FlashCardUiState.Loading
        ) {
            Text("Guardar", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(32.dp)) // Para evitar que el botón quede demasiado pegado al borde inferior
    }
}

