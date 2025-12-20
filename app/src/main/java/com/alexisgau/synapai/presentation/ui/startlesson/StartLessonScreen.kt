package com.alexisgau.synapai.presentation.ui.startlesson

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alexisgau.synapai.R
import com.alexisgau.synapai.presentation.ui.flashcard.FlashCardListViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.core.graphics.toColorInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartLessonScreen(
    lessonId: Int,
    navigateToFlashCardScreen: (Int) -> Unit,
    viewModel: FlashCardListViewModel = hiltViewModel(),
) {
    val flashCards by viewModel.flashCards.collectAsStateWithLifecycle()

    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var flipped by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(lessonId) {
        viewModel.loadFlashCardsByLesson(lessonId)
    }

    Scaffold(
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navigateToFlashCardScreen(lessonId) }) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.close)
                            )
                        }
                    }
                )
                if (flashCards.isNotEmpty()) {
                    val progress by animateFloatAsState(
                        targetValue = (currentIndex + 1).toFloat() / flashCards.size,
                        label = "progress"
                    )
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }
    ) { paddingValues ->
        if (flashCards.isNotEmpty()) {

            val currentCard = flashCards.getOrNull(currentIndex)

            if (currentIndex >= flashCards.size) {
                LessonCompleteScreen(
                    onRestart = {
                        currentIndex = 0
                        flipped = false
                    },
                    onFinish = { navigateToFlashCardScreen(lessonId) },
                    modifier = Modifier.padding(paddingValues)
                )
                return@Scaffold
            }

            if (currentCard == null) return@Scaffold

            val rotation = remember { Animatable(0f) }

            LaunchedEffect(flipped) {
                rotation.animateTo(
                    targetValue = if (flipped) 180f else 0f,
                    animationSpec = tween(durationMillis = 500)
                )
            }

            val scope = rememberCoroutineScope()
            val offsetX = remember { Animatable(0f) }
            val configuration = LocalConfiguration.current
            val density = LocalDensity.current
            val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
            val swipeThreshold = screenWidthPx * 0.3f

            val goToNextCard = {
                scope.launch {
                    offsetX.animateTo(-screenWidthPx * 1.2f, tween(300))
                    if (currentIndex < flashCards.size) {
                        currentIndex++
                        flipped = false
                        rotation.snapTo(0f)
                        offsetX.snapTo(0f)
                    }
                }
            }

            val cardColor = parseColorSafe(currentCard.color)
            val contentColor = if (cardColor.luminance() > 0.5f) Color.Black else Color.White

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                        .fillMaxWidth()
                        .aspectRatio(1f)
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                scope.launch { offsetX.snapTo(offsetX.value + delta) }
                            },
                            onDragStopped = {
                                if (offsetX.value < -swipeThreshold) {
                                    goToNextCard()
                                } else {
                                    scope.launch { offsetX.animateTo(0f, tween(300)) }
                                }
                            }
                        )
                        .graphicsLayer {
                            rotationY = rotation.value
                            cameraDistance = 12 * density.density
                        }
                        .background(
                            color = cardColor,
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable { flipped = !flipped }
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (rotation.value <= 90f) {
                        Text(
                            text = currentCard.question,
                            color = contentColor,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = currentCard.answer,
                            color = contentColor,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.graphicsLayer { rotationY = 180f }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { goToNextCard() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(stringResource(id = R.string.next), fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun LessonCompleteScreen(
    modifier: Modifier = Modifier,
    onRestart: () -> Unit,
    onFinish: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(id = R.string.study_session_completed_title),
                tint = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(id = R.string.study_session_completed_message_1),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.study_session_completed_message_2),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onRestart,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(id = R.string.study_session_restart_button),
                    fontSize = 16.sp
                )

            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onFinish,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = stringResource(id = R.string.study_session_finish_button),
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}



fun parseColorSafe(colorString: String): Color {
    return try {
        if (colorString.isBlank()) {
            Color(0xFF6750A4)
        } else {

            Color(colorString.toColorInt())
        }
    } catch (e: Exception) {
        Color(0xFF6750A4)
    }
}