package com.example.flashmind.presentation.ui.flashcard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.presentation.viewmodel.FlashCardViewModel


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
    val flashCards = viewModel.flashCards.collectAsStateWithLifecycle()

    viewModel.loadFlashCardsByLesson(lessonId)

    Scaffold(
        floatingActionButton = {
            AddFlashCardFab(
                onManualClick = { navigateToAddFlashCardManual(lessonId) },
                onAiClick = { navigateToAddFlashCardAi(lessonId) }
            )
        }
    ) { innerPadding ->
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


            LazyColumn(
                modifier = Modifier.padding(26.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(flashCards.value) { flashcard ->
                    FlashCardItem(
                        id = flashcard.id,
                        question = flashcard.question,
                        answer = flashcard.answer,
                        deleteFlashCard = { viewModel.deleteFlashCard(flashcard) },
                        editFlashCard = { navigateToEditFlashCard(flashcard.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun FlashCardItem(
    id: Int,
    question: String,
    answer: String,
    deleteFlashCard: () -> Unit,
    editFlashCard: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {

                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    Modifier.clickable { deleteFlashCard() })
                Icon(
                    Icons.Default.Edit,
                    contentDescription = null,
                    Modifier.clickable { editFlashCard(id) })
            }
            Text("Pregunta:", fontWeight = FontWeight.Bold)
            Text(question, modifier = Modifier.padding(bottom = 8.dp))


            Spacer(Modifier.height(8.dp))

            Text(
                "Respuesta:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(answer)
        }
    }
}







