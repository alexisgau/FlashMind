package com.example.flashmind.presentation.ui.flashcard

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.presentation.viewmodel.FlashCardViewModel
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalMaterial3Api::class)
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
        when (addCardState) {
            is FlashCardUiState.Error -> showError = true
            FlashCardUiState.Loading -> {}
            is FlashCardUiState.Success -> navigateToFlashCards(lessonId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Create new Flashcard",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navigateToFlashCards(lessonId)}) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text("Question", fontSize = 16.sp)
            QuestionTextField(
                question = question,
                onQuestionChange = { question = it },
                label = "input question",
                placeholder = "Insert question"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Answer", fontSize = 16.sp)
            ScrollableOutlinedTextField(
                text = answer,
                onTextChange = { answer = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = "Input text",
                placeholder = "Paste the content you want to generate flashcards from here"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select a color", fontSize = 16.sp)

            ColorSelector(selectedColor = selectedColor, onColorSelected = {selectedColor = it})

            if (showError) {
                Text(
                    text = "Please complete the question and answer.",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
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
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Save", fontSize = 18.sp)
            }
        }
    }
}
@Composable
fun CreateCardScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // 🔙 Header con ícono de cerrar y título
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Cerrar",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Nueva Tarjeta",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 📝 Campo de Pregunta
        Text("Pregunta", color = Color.DarkGray)
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Escribe aquí tu pregunta", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),

            )

        Spacer(modifier = Modifier.height(24.dp))

        // 📝 Campo de Respuesta
        Text("Respuesta", color = Color.DarkGray)
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Escribe aquí tu respuesta", color = Color.Gray) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp),

            )

        Spacer(modifier = Modifier.weight(1f))

        // ✅ Botones inferiores: Cancelar y Guardar
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE2E8F0),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancelar")
            }

            Button(
                onClick = {},
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF29B6F6),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Guardar")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCreateCardScreen() {
    CreateCardScreenPreview()
}

@Composable
fun QuestionTextField(
    modifier: Modifier = Modifier, // Hacer el modifier el primer parámetro opcional es convención
    question: String,
    onQuestionChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    OutlinedTextField(
        value = question,
        onValueChange = onQuestionChange,
        singleLine = true,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        shape = RoundedCornerShape(12.dp),
    )
}


@Composable
fun ColorSelector(
    selectedColor: String,
    onColorSelected: (String) -> Unit
) {
    val colors = listOf("#FF5733", "#33FF57", "#3357FF", "#FF33A1", "#FFC300")

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        colors.forEach { color ->
            val isSelected = selectedColor == color
            val animatedOffset by animateDpAsState(
                targetValue = if (isSelected) (-8).dp else 0.dp,
                label = "offsetAnimation"
            )
            val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

            Box(
                modifier = Modifier
                    .offset(y = animatedOffset) // sube el color seleccionado
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(color.toColorInt()))
                    .border(3.dp, borderColor, CircleShape)
                    .clickable { onColorSelected(color) }
            )
        }
    }
}




