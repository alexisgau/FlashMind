package com.example.flashmind.presentation.ui.test.run

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.R
import com.example.flashmind.domain.model.QuizQuestionModel
import com.example.flashmind.presentation.utils.formatTime
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    modifier: Modifier = Modifier,
    contentFile: String? = null,
    testTittle: String? = null,
    testId: Int? = null,
    lessonId: Int? = null,
    onClickBack: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {

    val testUiState by viewModel.quizState.collectAsStateWithLifecycle()
    val defaultTitle = stringResource(id = R.string.quiz_generate_title)
    val finalTestTitle = testTittle ?: defaultTitle
    Log.i(
        "QuizScreen",
        "QuizScreen - contentFile: $contentFile, lessonId: $lessonId, testId: $testId"
    )

    BackHandler(
        enabled = testUiState !is QuizUiState.Loading,
        onBack = onClickBack
    )

    LaunchedEffect(key1 = contentFile, key2 = lessonId, key3 = testId) {
        Log.i("QuizScreen", "LaunchedEffect triggered")
        when {
            contentFile != null && lessonId != null -> {
                Log.i("QuizScreen", "Starting test generation with content file")
                viewModel.generateAndSaveTest(
                    contentFile,
                    lessonId,
                    testTitle = finalTestTitle
                )
            }

            testId != null -> {
                Log.i("QuizScreen", "Loading existing test with ID: $testId")
                viewModel.loadTest(testId)
            }

            else -> {
                Log.e("QuizScreen", "Invalid navigation arguments")
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        finalTestTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (testUiState is QuizUiState.Loading) {
                        IconButton(
                            onClick = { },
                            enabled = false
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.quiz_close_button_cd),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = onClickBack
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.quiz_close_button_cd)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val uiState = testUiState) {
                is QuizUiState.Error -> {
                    ErrorGenerator(
                        errorTitle = stringResource(id = R.string.quiz_error_generating_title),
                        errorMessage = uiState.error,
                        onRetry = {
                            if (contentFile != null && lessonId != null) {
                                viewModel.generateAndSaveTest(
                                    contentFile,
                                    lessonId,
                                    testTitle = finalTestTitle
                                )
                            }
                        },
                        onBack = onClickBack
                    )
                }

                is QuizUiState.Finished -> {
                    val percentage =
                        (uiState.correctAnswers.toDouble() / uiState.totalQuestions) * 100

                    QuizFinished(
                        correctAnswers = uiState.correctAnswers,
                        totalQuestions = uiState.totalQuestions,
                        percentage = percentage.roundToInt(),
                        timeTaken = uiState.elapsedTimeSeconds.formatTime(),
                        onRetry = { viewModel.restartQuiz() },
                    )
                }

                QuizUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        QuizLoadingScreen()
                    }
                }

                is QuizUiState.Success -> {
                    QuizQuestionUi(
                        question = uiState.test,
                        questionNumber = uiState.currentIndex + 1,
                        totalQuestions = uiState.totalQuestions,
                        onAnswerSelected = viewModel::answerSelected,
                        onNextQuestion = viewModel::moveToNextQuestion,
                        isCorrect = uiState.isCorrect,
                        selectedAnswerIndex = uiState.selectedAnswerIndex,
                        correctAnswerIndex = uiState.test.correctResponseIndex
                    )
                }
            }
        }
    }
}

@Composable
fun PulsingRobotLogo(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")

    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Image(
        painter = painterResource(id = R.drawable.mastanrobot),
        contentDescription = stringResource(id = R.string.quiz_generating_step_generating),
        modifier = modifier
            .size(160.dp)
            .scale(scale)
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizLoadingScreen(
    modifier: Modifier = Modifier,
    generateName: String = "Quiz",
    generatingName: String = "preguntas"
) {
    val colorAzul = Color(0xFF2196F3)
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(id = R.string.quiz_generating_title, generateName),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(48.dp))

        PulsingRobotLogo(modifier = modifier)

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = stringResource(id = R.string.quiz_generating_subtitle, generatingName),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(id = R.string.quiz_generating_wait_message),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.weight(1f))

        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp),
            color = colorAzul,
            trackColor = colorAzul.copy(alpha = 0.2f)
        )

        Text(
            text = stringResource(id = R.string.quiz_generating_step_processing),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuizFinished(
    correctAnswers: Int = 9,
    totalQuestions: Int = 20,
    percentage: Int = 45,
    timeTaken: String = "00:15:32",
    onRetry: () -> Unit = {},
) {

    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = Color.White,
        modifier = Modifier.size(100.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = stringResource(id = R.string.quiz_completed_title),
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        )
    }

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = stringResource(id = R.string.quiz_completed_title),
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold
    )

    Text(
        text = stringResource(id = R.string.quiz_completed_score, correctAnswers, totalQuestions),
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = stringResource(id = R.string.quiz_completed_percentage, percentage),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(id = R.string.quiz_completed_time, timeTaken),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Spacer(modifier = Modifier.height(48.dp))

    Button(
        onClick = onRetry,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = stringResource(id = R.string.quiz_completed_retry),
            fontSize = 16.sp
        )
    }
}

@Composable
fun ErrorGenerator(
    errorTitle: String,
    errorMessage: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = stringResource(id = R.string.error_prefix),
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = errorTitle ,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onBack,
            ) {
                Text(stringResource(id = R.string.go_back))
            }
            Button(
                onClick = onRetry
            ) {
                Text(stringResource(id = R.string.error_try_again))
            }
        }
    }
}

@Composable
fun QuizQuestionUi(
    question: QuizQuestionModel,
    questionNumber: Int,
    totalQuestions: Int,
    onAnswerSelected: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    isCorrect: Boolean?,
    selectedAnswerIndex: Int?,
    correctAnswerIndex: Int
) {
    val colorPrimario = MaterialTheme.colorScheme.primary
    val scrollState = rememberScrollState()

    Column(
        Modifier.fillMaxSize()
    ) {

        QuizProgress(questionNumber, totalQuestions, Modifier)
        Box(
            modifier = Modifier
                .height(220.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Card(
                elevation = CardDefaults.cardElevation(2.dp),
                border = BorderStroke(2.dp, colorPrimario),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = question.question,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            question.options.forEachIndexed { index, optionText ->
                ModernOptionSurface(
                    index = index,
                    text = optionText,
                    onClick = { if (selectedAnswerIndex == null) onAnswerSelected(index) },
                    isSelected = selectedAnswerIndex == index,
                    isCorrect = isCorrect,
                    correctAnswerIndex = correctAnswerIndex,
                    isEnabled = selectedAnswerIndex == null
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        Button(
            onClick = onNextQuestion,
            enabled = selectedAnswerIndex != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(stringResource(id = R.string.quiz_next_question))
        }
    }
}

@Composable
fun ModernOptionSurface(
    index: Int,
    text: String,
    onClick: () -> Unit,
    isSelected: Boolean,
    isCorrect: Boolean?,
    correctAnswerIndex: Int,
    isEnabled: Boolean
) {
    val shape = RoundedCornerShape(12.dp)


    val correctColor = Color(0xFF388E3C)
    val incorrectColor = Color(0xFFD32F2F)


    val (backgroundColor, borderColor, textColor) = when {
        isCorrect != null && index == correctAnswerIndex -> {
            Triple(correctColor, correctColor, Color.White)
        }
        isCorrect == false && isSelected -> {
            Triple(incorrectColor, incorrectColor, Color.White)
        }
        isCorrect != null && !isSelected -> {

            Triple(
                MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
        isSelected -> {
            Triple(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.onSurface
            )
        }
        else -> {
            Triple(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.outline,
                MaterialTheme.colorScheme.onSurface
            )
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(enabled = isEnabled, onClick = onClick),
        shape = shape,
        tonalElevation = 1.dp,
        color = backgroundColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
        }
    }
}
@Composable
fun QuizProgress(
    questionNumber: Int,
    totalQuestions: Int,
    modifier: Modifier = Modifier
) {
    val progressColor = Color(0xFF03A9F4)
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progress = (questionNumber.toFloat() / totalQuestions.toFloat()).coerceIn(0f, 1f)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.quiz_question_counter, questionNumber, totalQuestions),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(trackColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(progress)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(progressColor)
            )
        }
    }
}