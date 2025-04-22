package com.example.flashmind.presentation.ui.addflashcard

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.presentation.viewmodel.FlashCardAiState
import com.example.flashmind.presentation.viewmodel.FlashCardViewModel

@Composable
fun AddFlashCardScreen(
    lessonId: Int,
    navigateToFlashCards:() -> Unit,
    viewModel: FlashCardViewModel = hiltViewModel()
) {
    val state by viewModel.flashCardAiState.collectAsStateWithLifecycle()
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            "Generar Flashcards con IA",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Texto de entrada") },
            placeholder = { Text("Pegá aquí el contenido del que quieras generar flashcards") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp),
            maxLines = 10,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.generateFlashCards(text, lessonId) },
                modifier = Modifier.weight(1f)
            ) {
                Text("GENERAR")
            }

            Button(
                onClick = {
                    viewModel.saveGeneratedFlashcards()
                    Toast.makeText(context, "Flashcards guardadas con éxito", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("GUARDAR")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (state) {
            is FlashCardAiState.Error -> {
                val msg = (state as FlashCardAiState.Error).error
                Text("Error: $msg", color = Color.Red)
            }

            FlashCardAiState.Loading -> {
                CircularProgressIndicator()
            }

            is FlashCardAiState.Success -> {
                val list = (state as FlashCardAiState.Success).list
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(list) { flashcard ->
                        FlashCardItem(question = flashcard.question, answer = flashcard.answer)
                    }
                }
            }

            FlashCardAiState.Saved -> {
                Text("¡Flashcards guardadas!", color = Color.Green)
                navigateToFlashCards()
            }
        }
    }
}



@Composable
fun FlashCardItem(question: String, answer: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Pregunta:", fontWeight = FontWeight.Bold)
            Text(question, modifier = Modifier.padding(bottom = 8.dp))

            Divider()

            Text("Respuesta:", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            Text(answer)
        }
    }
}


@Composable
fun AddFlashCardForm(
    question: String,
    onQuestionChange: (String) -> Unit,
    answer: String,
    onAnswerChange: (String) -> Unit,
    selectedColor: String,
    onColorSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val colors = listOf(
        "#FF5733", // Naranja
        "#33FF57", // Verde
        "#3357FF", // Azul
        "#FF33A1", // Rosa
        "#F3F3F3"  // Gris claro
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = question,
            onValueChange = onQuestionChange,
            label = { Text("Question") },
            placeholder = { Text("Enter your question") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            label = { Text("Answer") },
            placeholder = { Text("Enter your answer") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Text("Pick a Color", style = MaterialTheme.typography.bodyMedium)

        Box(modifier = Modifier.padding(vertical = 8.dp)) {
            OutlinedButton(
                onClick = { expanded = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(selectedColor.toColorInt())
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(selectedColor.toColorInt()))
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                colors.forEach { color ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(Color(color.toColorInt()))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(color)
                            }
                        },
                        onClick = {
                            onColorSelected(color)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

//    Log.i("FlashCardScreen", "id:$lessonId")
//    var question by remember { mutableStateOf("") }
//    var answer by remember { mutableStateOf("") }
//    var selectedColor by remember { mutableStateOf("#FF5733") }


//    AddFlashCardForm(
//        question = question,
//        onQuestionChange = { question = it },
//        answer = answer,
//        onAnswerChange = { answer = it },
//        selectedColor = selectedColor,
//        onColorSelected = { selectedColor = it }
//    )