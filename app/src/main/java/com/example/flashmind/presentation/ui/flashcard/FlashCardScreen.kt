package com.example.flashmind.presentation.ui.flashcard

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.presentation.ui.addflashcard.AddFlashCardFab
import com.example.flashmind.presentation.ui.addflashcard.FlashCardItem
import com.example.flashmind.presentation.viewmodel.FlashCardViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FlashCardScreen(
    lessonId: Int,
    navigateToHome: () -> Unit,
    navigateToAddFlashCardAi: (Int) -> Unit,
    navigateToAddFlashCardManual: (Int) -> Unit,
    navigateToStartGame: (Int) -> Unit,
    navigateToEditFlashCard: (Int) -> Unit,
    viewModel: FlashCardViewModel = hiltViewModel()
) {
    Log.i("FlashCardScreen", "id:$lessonId")
    val flashCards = viewModel.flashCards.collectAsStateWithLifecycle()

    viewModel.getFlashCards(lessonId)

    Scaffold(
        floatingActionButton = {
            AddFlashCardFab(
                onManualClick = { navigateToAddFlashCardManual(lessonId) },
                onAiClick = { navigateToAddFlashCardAi(lessonId) }
            )
        }
    ) {
    innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),

        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { navigateToHome() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                   Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Back")
                }

                Button(
                    onClick = { navigateToStartGame(lessonId) },
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = "Start")
                }
            }


            LazyColumn(modifier = Modifier.padding(26.dp), verticalArrangement = Arrangement.spacedBy(15.dp)) {
                items(flashCards.value) { flashcard ->
                    FlashCardItem(
                        id = flashcard.id,
                        question = flashcard.question,
                        answer = flashcard.answer,
                        deleteFlashCard = {viewModel.deleleteFlashCard(flashcard)},
                        editFlashCard = {navigateToEditFlashCard(flashcard.id)}
                    )
                }
            }
        }
    }
}







