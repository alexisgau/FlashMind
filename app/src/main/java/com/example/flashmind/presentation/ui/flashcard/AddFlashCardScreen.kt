package com.example.flashmind.presentation.ui.flashcard

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.R
import com.example.flashmind.presentation.viewmodel.FlashCardViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlashCardScreenAi(
    lessonId: Int,
    navigateToFlashCards: (Int) -> Unit,
    viewModel: FlashCardViewModel = hiltViewModel()
) {
    val state by viewModel.flashCardAiState.collectAsStateWithLifecycle()
    var text by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {  },
                navigationIcon = {
                    IconButton(onClick = { navigateToFlashCards(lessonId) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {



            Image(
                painter = painterResource(id = R.drawable.mastanrobot),
                contentDescription = "Robot generador de flashcards",
                modifier = Modifier.size(120.dp)
            )

            Text(
                "Generate Flashcards with AI",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            ScrollableOutlinedTextField(
                text = text,
                onTextChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                label = "Input text",
                placeholder = "Paste the content you want to generate flashcards from here"
            )


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                when (val currentState = state) {
                    is FlashCardAiState.Error -> {
                        Text("Error: ${currentState.error}", color = MaterialTheme.colorScheme.error)
                    }
                    FlashCardAiState.Loading -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Thinking of something great...🧠",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    is FlashCardAiState.Success -> {
                        val flashcards = viewModel.generatedFlashcards

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(flashcards, key = { it.question }) { flashcard ->
                                FlashCardItemDeletable(
                                    id = flashcard.id,
                                    question = flashcard.question,
                                    answer = flashcard.answer,
                                    deleteFlashCard = { viewModel.removeFromGenerated(flashcard) },
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                    }
                    FlashCardAiState.Saved -> {
                        Text("¡Flashcards guardadas!", color = Color.Green.copy(alpha = 0.8f))
                        LaunchedEffect(Unit) {
                            navigateToFlashCards(lessonId)
                        }
                    }
                    FlashCardAiState.Init -> {
                    }
                }
            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.generateFlashCards(text, lessonId) },
                    modifier = Modifier.weight(1f),
                    enabled = state !is FlashCardAiState.Loading
                ) {
                    Text("GENERATE")
                }
                Button(
                    onClick = { viewModel.saveGeneratedFlashcards() },
                    modifier = Modifier.weight(1f),
                    enabled = state is FlashCardAiState.Success
                ) {
                    Text("SAVE ALL")
                }
            }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScrollableOutlinedTextField(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "",
    placeholder: String = "",
) {
    val scrollState = rememberScrollState()

    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier
            .verticalScroll(scrollState), // Aplicar verticalScroll aquí
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        textStyle = LocalTextStyle.current.copy(lineHeight = 20.sp),
        shape = RoundedCornerShape(12.dp),
        maxLines = Int.MAX_VALUE,
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        singleLine = false,
        visualTransformation = VisualTransformation.None

    )
}

