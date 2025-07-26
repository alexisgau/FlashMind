package com.example.flashmind.presentation.ui.flashcard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.presentation.viewmodel.FlashCardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFlashCardScreen(
    flashCardId: Int,
    onNavigateBack: () -> Unit,
    viewModel: FlashCardViewModel = hiltViewModel()
) {
    val flashCardState by viewModel.flashCardState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = flashCardId) {
        viewModel.loadFlashCardById(flashCardId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Edit Flashcard",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Spacer(modifier = Modifier.width(48.dp))
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = flashCardState) {
                is FlashCardUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is FlashCardUiState.Error -> {
                    Text("Error al cargar la tarjeta", color = MaterialTheme.colorScheme.error)
                }
                is FlashCardUiState.Success -> {
                   state.flashCard.lessonId
                    EditFlashCardForm(
                        flashCard = state.flashCard,
                        onSaveChanges = { updatedFlashCard ->
                            viewModel.editFlashCard(updatedFlashCard)
                            onNavigateBack()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EditFlashCardForm(
    flashCard: FlashCard,
    onSaveChanges: (FlashCard) -> Unit
) {
    var question by remember { mutableStateOf(flashCard.question) }
    var answer by remember { mutableStateOf(flashCard.answer) }
    var selectedColor by remember { mutableStateOf(flashCard.color) }

    val colors = listOf("#FF5733", "#33FF57", "#3357FF", "#FF33A1", "#F3F3F3", "#F9A825")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = question,
            onValueChange = { question = it },
            label = { Text("Question") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it },
            label = { Text("Answer") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Pick a Color", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        ColorPickerRow(
            colors = colors,
            selectedColor = selectedColor,
            onColorSelected = { selectedColor = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val updatedFlashCard = flashCard.copy(
                    question = question,
                    answer = answer,
                    color = selectedColor
                )
                onSaveChanges(updatedFlashCard)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("SAVE CHANGES", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ColorPickerRow(
    colors: List<String>,
    selectedColor: String,
    onColorSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        colors.forEach { colorString ->
            val color = Color(colorString.toColorInt())
            val isSelected = selectedColor == colorString

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color)
                    .clickable { onColorSelected(colorString) }
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    androidx.compose.material3.Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Color seleccionado",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}