package com.example.flashmind.presentation.ui.flashcard

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.R
import com.example.flashmind.presentation.ui.addflashcardai.AddFlashCardFab


@Composable
fun FlashCardScreen(
    lessonId: Int,
    lessonName: String,
    navigateToAddFlashCardAi: (Int) -> Unit,
    navigateToAddFlashCardManual: (Int) -> Unit,
    navigateToStartGame: (Int) -> Unit,
    navigateToEditFlashCard: (Int) -> Unit,
    navigateToLessons: () -> Unit,
    viewModel: FlashCardListViewModel = hiltViewModel()
) {
    val flashCards = viewModel.flashCards.collectAsStateWithLifecycle()


    LaunchedEffect(key1 = lessonId) {
        viewModel.loadFlashCardsByLesson(lessonId)
    }


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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.flashcards_title, lessonName),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.playing_cards),
                        contentDescription = stringResource(id = R.string.flashcards_counter, flashCards.value.size),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.flashcards_counter, flashCards.value.size),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { navigateToLessons() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.back),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.back))
                }

                Button(
                    onClick = { navigateToStartGame(lessonId) },
                    shape = RoundedCornerShape(12.dp),
                    enabled = flashCards.value.isNotEmpty(),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = stringResource(id = R.string.start),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(text = stringResource(id = R.string.start))
                }
            }

            if (flashCards.value.isEmpty()) {
                EmptyFlashcardList()
            }

            LazyColumn(
                modifier = Modifier.padding(26.dp),
                contentPadding = PaddingValues(
                    top = 0.dp,
                    bottom = 80.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                items(flashCards.value) { flashcard ->
                    FlashcardItem(
                        categoryName = "a",
                        question = flashcard.question,
                        imagePainter = painterResource(R.drawable.flashcard),
                        onEditClick = { navigateToEditFlashCard(flashcard.id) },
                        onDeleteClick = { viewModel.deleteFlashCard(flashcard) },
                    )
                }
            }
        }
    }
}

@Composable
fun FlashCardItemDeletable(
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
                    contentDescription = stringResource(id = R.string.flashcards_delete_card_cd),
                    modifier = Modifier.clickable { deleteFlashCard() }
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = stringResource(id = R.string.flashcards_question_label),
                fontWeight = FontWeight.Bold
            )
            Text(question, modifier = Modifier.padding(bottom = 8.dp))

            Spacer(Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.flashcards_answer_label),
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
            Image(
                painter = imagePainter,
                contentDescription = stringResource(
                    id = R.string.flashcards_category_image_cd,
                    categoryName
                ),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(id = R.string.flashcards_edit_card_cd),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.flashcards_delete_card_cd),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FlashcardItemPreview() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            FlashcardItem(
                categoryName = "Biología",
                question = "¿Qué es la fotosíntesis?",
                imagePainter = painterResource(id = R.drawable.ic_launcher_background),
                onEditClick = {},
                onDeleteClick = {}
            )
        }
    }
}

@Composable
fun EmptyFlashcardList(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.empty_flashcards),
            contentDescription = stringResource(id = R.string.flashcards_empty_title),
            modifier = Modifier.size(150.dp),
            contentScale = ContentScale.Fit,
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(id = R.string.flashcards_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.flashcards_empty_description),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}




