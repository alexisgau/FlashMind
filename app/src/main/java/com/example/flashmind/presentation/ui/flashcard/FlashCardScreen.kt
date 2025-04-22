package com.example.flashmind.presentation.ui.flashcard

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.presentation.ui.addflashcard.FlashCardItem
import com.example.flashmind.presentation.viewmodel.FlashCardViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FlashCardScreen(
    lessonId: Int,
    navigateToHome: () -> Unit,
    navigateToAddFlashCard: (Int) -> Unit,
    navigateToStartGame: (Int) -> Unit,
    viewModel: FlashCardViewModel = hiltViewModel()
) {
    Log.i("FlashCardScreen", "id:$lessonId")
    val flashCards = viewModel.flashCards.collectAsStateWithLifecycle()

    viewModel.getFlashCards(lessonId)

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigateToAddFlashCard(lessonId)
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar flashcard")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { navigateToHome() }) {
                    Text("Back")
                }

                Button(onClick = {
                    navigateToStartGame(lessonId)
                }) {
                    Text("Start")
                }
            }

            // Content extraído
            LazyColumn(modifier = Modifier.padding(26.dp)) {
                items(flashCards.value) { flashcard ->
                    FlashCardItem(
                        question = flashcard.question,
                        answer = flashcard.answer
                    )
                }
            }
        }
    }
}







