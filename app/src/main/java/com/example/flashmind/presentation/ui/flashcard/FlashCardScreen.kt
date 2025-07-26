package com.example.flashmind.presentation.ui.flashcard

import androidx.compose.foundation.Image
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.R
import com.example.flashmind.presentation.viewmodel.FlashCardViewModel


@Composable
fun FlashCardScreen(
    lessonId: Int,
    navigateToHome: () -> Unit,
    onNavigateBack:()-> Unit,
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

            if(flashCards.value.isEmpty()){

                Text("Empty flashCards in lesson,add one!", modifier = Modifier.padding(26.dp))
            }


            LazyColumn(
                modifier = Modifier.padding(26.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(flashCards.value) { flashcard ->
                    FlashcardItem(
                        categoryName = "a",
                        question = flashcard.question,
                        imagePainter = painterResource(R.drawable.icon_google),
                        onEditClick = { navigateToEditFlashCard(flashcard.id) },
                        onDeleteClick = { viewModel.deleteFlashCard(flashcard) },
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
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
            Text("Question:", fontWeight = FontWeight.Bold)
            Text(question, modifier = Modifier.padding(bottom = 8.dp))


            Spacer(Modifier.height(8.dp))

            Text(
                "Answer:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(answer)
        }
    }
}

@Composable
fun FlashCardItemDeletable(
    id: Int,
    question: String,
    answer: String,
    deleteFlashCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Flashcard",
                    modifier = Modifier.clickable { deleteFlashCard() }
                )
            }


            Spacer(Modifier.height(4.dp))

            Text("Question:", fontWeight = FontWeight.Bold)
            Text(question, modifier = Modifier.padding(bottom = 8.dp))

            Spacer(Modifier.height(8.dp))

            Text(
                "Answer:",
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(answer)
        }
    }
}

@Composable
fun FlashcardItem(
    categoryName: String,
    question: String,
    imagePainter: Painter,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de la categoría
            Image(
                painter = imagePainter,
                contentDescription = "Imagen de la categoría $categoryName",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Columna con el nombre de la categoría y la pregunta
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2 // Para que no ocupe mucho espacio
                )
            }

            // Iconos de acción
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar tarjeta",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar tarjeta",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// --- Preview para ver el diseño en Android Studio ---
@Preview(showBackground = true)
@Composable
fun FlashcardItemPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            FlashcardItem(
                categoryName = "Biología",
                question = "¿Qué es la fotosíntesis?",
                imagePainter = painterResource(id = R.drawable.ic_launcher_background), // Usa una imagen de tu proyecto
                onEditClick = {},
                onDeleteClick = {}
            )
        }
    }
}








