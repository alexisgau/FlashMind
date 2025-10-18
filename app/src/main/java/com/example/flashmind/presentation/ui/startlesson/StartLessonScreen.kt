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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.presentation.ui.flashcard.FlashCardListViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartLessonScreen(
    lessonId: Int,
    navigateToFlashCardScreen: (Int) -> Unit,
    viewModel: FlashCardListViewModel = hiltViewModel()
) {


    val flashCards by viewModel.flashCards.collectAsStateWithLifecycle()

    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    var flipped by rememberSaveable { mutableStateOf(false) }


    viewModel.loadFlashCardsByLesson(lessonId)

    Scaffold(topBar = {
        MediumTopAppBar(title = {}, navigationIcon = {
            IconButton(onClick = {navigateToFlashCardScreen(lessonId)}) {
                Icon(
                    Icons.Default.Close, contentDescription = "Close"
                )
            }
        })
    }) { paddingValues ->
        if (flashCards.isNotEmpty()) {

            val currentCard = flashCards.getOrNull(currentIndex)

            if (currentIndex >= flashCards.size) {
                // Fin de la lección
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "You've finished the lesson!",
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.inverseSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = {
                            currentIndex = 0
                            flipped = false
                        }) {
                            Text("Restart Lesson")
                        }
                        Button(onClick = { navigateToFlashCardScreen(lessonId) }) { Text("Finish Lesson") }
                    }
                }
                return@Scaffold
            }


            val animatedRotationY by animateFloatAsState(
                targetValue = if (flipped) 180f else 0f,
                animationSpec = tween(durationMillis = 500),
                label = "flip"
            )


            val scope = rememberCoroutineScope()


            val offsetX = remember { Animatable(0f) }

            //  Obtener el ancho de la pantalla para calcular el umbral de swipe
            val configuration = LocalConfiguration.current
            val density = LocalDensity.current
            val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
            val swipeThreshold = screenWidthPx * 0.5f // Umbral: 50% de la pantalla

            //Función centralizada para pasar a la siguiente tarjeta
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
                                // delta es la cantidad que el dedo se movio.
                                scope.launch {
                                    offsetX.snapTo(offsetX.value + delta)
                                }
                            },
                            onDragStopped = {
                                // Cuando el usuario suelta el dedo
                                scope.launch {
                                    val currentOffset = offsetX.value

                                    if (currentOffset < -swipeThreshold) {
                                        // Swipe a la izquierda
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
                            Color(currentCard!!.color.toColorInt()),
                            shape = RoundedCornerShape(16.dp)
                        )

                        .clickable { flipped = !flipped }
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
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
                                rotationY = 180f // para que se vea derecha al girar
                            }
                        )
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
                        Text("Next")
                    }
                }
            }
        }
    }
}


