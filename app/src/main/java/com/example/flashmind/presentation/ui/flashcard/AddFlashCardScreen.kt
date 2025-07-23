package com.example.flashmind.presentation.ui.flashcard

import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.R
import com.example.flashmind.presentation.viewmodel.FlashCardViewModel

@Composable
fun AddFlashCardScreenAi(
    lessonId: Int,
    navigateToFlashCards: (Int) -> Unit,
    navigateToEditFlashCard: (Int) -> Unit,
    viewModel: FlashCardViewModel = hiltViewModel()
) {
    val state by viewModel.flashCardAiState.collectAsStateWithLifecycle()
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.mastanrobot),
            contentDescription = "Robot generador de flashcards",
            modifier = Modifier
                .size(120.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            "Generate Flashcards with AI",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color =  MaterialTheme.colorScheme.inverseSurface,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Input text") },
            placeholder = { Text("Paste the content you want to generate flashcards from here") },
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
                Text("GENERATE")
            }

            Button(
                onClick = {
                    viewModel.saveGeneratedFlashcards()
                    Toast.makeText(context, "Flashcards saved successfully", Toast.LENGTH_SHORT)
                        .show()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("SAVE ALL")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        when (state) {
            is FlashCardAiState.Error -> {
                val msg = (state as FlashCardAiState.Error).error
                Text("Error: $msg", color = Color.Red)
            }

            FlashCardAiState.Loading -> {
                Text("Thinking of something great...\uD83E\uDD16", color = MaterialTheme.colorScheme.inverseSurface)
            }

            is FlashCardAiState.Success -> {
                val flashcards = viewModel.generatedFlashcards

                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(flashcards, key = { it.question }) { flashcard ->
                        FlashCardItem(
                            id = flashcard.id,
                            question = flashcard.question,
                            answer = flashcard.answer,
                            deleteFlashCard = { viewModel.removeFromGenerated(flashcard) },
                            editFlashCard = { navigateToEditFlashCard(flashcard.id) }
                        )
                    }
                }

            }

            FlashCardAiState.Saved -> {
                Text("¡Saved Flashcards!", color = Color.Green)
                navigateToFlashCards(lessonId)
            }

            FlashCardAiState.Init -> null
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
    onColorSelected: (String) -> Unit,
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
            .padding(16.dp),
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

        Text("Pick a Color", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.inverseSurface)

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

    }

    Spacer(modifier = Modifier.height(16.dp))
}


@Composable
fun AddFlashCardFab(
    onManualClick: () -> Unit,
    onAiClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.padding(16.dp)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Create manually") },
                    onClick = {
                        expanded = false
                        onManualClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text("Create with AI") },
                    onClick = {
                        expanded = false
                        onAiClick()
                    }
                )
            }

            FloatingActionButton(
                onClick = { expanded = !expanded },

                ) {
                Icon(Icons.Default.Add, contentDescription = "ADD")
            }
        }
    }
}
