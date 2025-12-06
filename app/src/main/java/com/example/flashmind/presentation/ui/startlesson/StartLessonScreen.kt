package com.example.flashmind.presentation.ui.startlesson

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.flashmind.R
import com.example.flashmind.presentation.ui.flashcard.FlashCardListViewModel
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

    Scaffold(topBar = {
        MediumTopAppBar(title = {}, navigationIcon = {
            IconButton(onClick = { navigateToFlashCardScreen(lessonId) }) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(id = R.string.close)
                )
            }
        })
    }) { paddingValues ->
        if (flashCards.isNotEmpty()) {

            val currentCard = flashCards.getOrNull(currentIndex)

            if (currentIndex >= flashCards.size) {
                LessonCompleteScreen(
                    onRestart = {
                        currentIndex = 0
                        flipped = false
                    },
                    onFinish = { navigateToFlashCardScreen(lessonId) },
                )

                return@Scaffold
            }

            if (currentCard == null) return@Scaffold

            val animatedRotationY by animateFloatAsState(
                targetValue = if (flipped) 180f else 0f,
                animationSpec = tween(durationMillis = 500),
                label = "flip"
            )

            val scope = rememberCoroutineScope()
            val offsetX = remember { Animatable(0f) }
            val configuration = LocalConfiguration.current
            val density = LocalDensity.current
            val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
            val swipeThreshold = screenWidthPx * 0.5f

            val goToNextCard = {
                if (currentIndex <= flashCards.lastIndex) {
                    currentIndex++
                    flipped = false
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(paddingValues)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                        .fillMaxWidth()
                        .height(250.dp)
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = rememberDraggableState { delta ->
                                scope.launch {
                                    offsetX.snapTo(offsetX.value + delta)
                                }
                            },
                            onDragStopped = {
                                scope.launch {
                                    val currentOffset = offsetX.value
                                    if (currentOffset < -swipeThreshold) {
                                        offsetX.animateTo(-screenWidthPx * 1.2f, tween(300))
                                        goToNextCard()
                                        offsetX.snapTo(0f)
                                    } else {
                                        offsetX.animateTo(0f, tween(300))
                                    }
                                }
                            }
                        )
                        .graphicsLayer {
                            rotationY = animatedRotationY
                            cameraDistance = 12 * density.density
                        }
                        .background(
                            color = parseColorSafe(currentCard.color),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { flipped = !flipped }
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {

                    val scrollState = rememberScrollState()

                    LaunchedEffect(flipped) {
                        scrollState.scrollTo(0)
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (animatedRotationY <= 90f) {
                            Text(
                                text = currentCard.question,
                                color = Color.Black,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center
                            )
                        } else {
                            val textToShow = if (flipped) {
                                currentCard.answer
                            } else {
                                currentCard.question
                            }
                            Text(
                                text = textToShow,
                                color = Color.Black,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.graphicsLayer {
                                    rotationY = 180f
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = {
                        goToNextCard()
                    }) {
                        Text(stringResource(id = R.string.next))
                    }
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), contentAlignment = Alignment.Center
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