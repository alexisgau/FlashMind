package com.alexisgau.synapai.presentation.ui.lessonoptions

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexisgau.synapai.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonOptionsScreen(
    lessonTitle: String,
    onNavigateBack: () -> Unit,
    onStudyFlashcards: () -> Unit,
    onViewSummary: () -> Unit,
    onTakeTest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.lesson_options_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },

        ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF455f91),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Text(
                        text = stringResource(id = R.string.lesson_options_subtitle),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = lessonTitle,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OptionCard(
                painter = painterResource(R.drawable.play_lesson),
                title = stringResource(id = R.string.lesson_options_flashcards_title),
                description = stringResource(id = R.string.lesson_options_flashcards_desc),
                buttonText = stringResource(id = R.string.lesson_options_flashcards_button),
                onButtonClick = onStudyFlashcards
            )

            Spacer(modifier = Modifier.height(16.dp))

            OptionCard(
                painter = painterResource(R.drawable.summary_icon),
                title = stringResource(id = R.string.lesson_options_summary_title),
                description = stringResource(id = R.string.lesson_options_summary_desc),
                buttonText = stringResource(id = R.string.lesson_options_summary_button),
                onButtonClick = onViewSummary
            )

            Spacer(modifier = Modifier.height(16.dp))

            OptionCard(
                painter = painterResource(R.drawable.test_icon),
                title = stringResource(id = R.string.lesson_options_quiz_title),
                description = stringResource(id = R.string.lesson_options_quiz_desc),
                buttonText = stringResource(id = R.string.lesson_options_quiz_button),
                onButtonClick = onTakeTest
            )
        }
    }
}

@Composable
private fun OptionCard(
    painter: Painter,
    title: String,
    description: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {


    val iconBackgroundBlue = Color(0xFF2b4778)
    val iconTintLightBlue = Color(0xFFD0E7FF)

    val isDark = MaterialTheme.colorScheme.surface.luminance() < 0.5f
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = if (isDark) {
            BorderStroke(1.dp, MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            null
        }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = iconBackgroundBlue
            ) {
                Icon(
                    painter = painter,
                    contentDescription = title,
                    tint = iconTintLightBlue,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = onButtonClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF455f91),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = ButtonDefaults.ContentPadding,
                modifier = Modifier.height(36.dp)
            ) {

                if (title == stringResource(id = R.string.lesson_options_flashcards_title)) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = buttonText,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LessonOptionsScreenPreview() {
    MaterialTheme {
        LessonOptionsScreen(
            lessonTitle = "Primera Guerra Mundial",
            onNavigateBack = {},
            onStudyFlashcards = {},
            onViewSummary = {},
            onTakeTest = {},
        )
    }
}