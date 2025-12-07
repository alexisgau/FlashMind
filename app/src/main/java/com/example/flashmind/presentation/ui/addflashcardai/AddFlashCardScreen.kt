package com.example.flashmind.presentation.ui.addflashcardai

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.R
import com.example.flashmind.presentation.ui.flashcard.FlashCardAiState
import com.example.flashmind.presentation.ui.flashcard.FlashCardItemDeletable

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlashCardScreenAi(
    lessonId: Int,
    navigateToFlashCards: (Int) -> Unit,
    viewModel: AddFlashCardAiViewModel = hiltViewModel(),
) {
    val state by viewModel.flashCardAiState.collectAsStateWithLifecycle()
    var text by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navigateToFlashCards(lessonId) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
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
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Image(
                painter = painterResource(id = R.drawable.mastanrobot),
                contentDescription = stringResource(id = R.string.flashcards_ai_title),
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = stringResource(id = R.string.flashcards_ai_subtitle),
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
                label = stringResource(id = R.string.flashcards_ai_input_label),
                placeholder = stringResource(id = R.string.flashcards_ai_input_placeholder)
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
                        Text(
                            text = "${stringResource(id = R.string.error_prefix)} ${currentState.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    FlashCardAiState.Loading -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stringResource(id = R.string.flashcards_ai_thinking),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    is FlashCardAiState.Success -> {
                        val flashcardsList = currentState.list

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            items(flashcardsList, key = { it.question }) { flashcard ->
                                FlashCardItemDeletable(
                                    question = flashcard.question,
                                    answer = flashcard.answer,
                                    deleteFlashCard = { viewModel.removeFromGenerated(flashcard) },
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }

                    }

                    FlashCardAiState.Saved -> {
                        Text(
                            text = stringResource(id = R.string.flashcards_ai_saved_success),
                            color = Color.Green.copy(alpha = 0.8f)
                        )
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
                    Text(stringResource(id = R.string.generate))
                }
                Button(
                    onClick = { viewModel.saveGeneratedFlashcards() },
                    modifier = Modifier.weight(1f),
                    enabled = state is FlashCardAiState.Success
                ) {
                    Text(stringResource(id = R.string.flashcards_ai_save_all_button))
                }
            }
        }
    }
}


@Composable
fun AddFlashCardFab(
    onManualClick: () -> Unit,
    onAiClick: () -> Unit,
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
                    text = { Text(stringResource(id = R.string.flashcards_create_manual_tab)) },
                    onClick = {
                        expanded = false
                        onManualClick()
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(id = R.string.flashcards_create_ai_tab)) },
                    onClick = {
                        expanded = false
                        onAiClick()
                    }
                )
            }

            FloatingActionButton(
                onClick = { expanded = !expanded },

                ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add)
                )
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
            .verticalScroll(scrollState),
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

