package com.example.flashmind.presentation.ui.lesson

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.presentation.viewmodel.FlashCardViewModel

@Composable
fun StartLessonScreen(
    lessonId: Int,
    navigateToFlashCardScreen: (Int) -> Unit,
    viewModel: FlashCardViewModel = hiltViewModel()
) {


    val flashCards by viewModel.flashCards.collectAsStateWithLifecycle()

    var currentIndex by remember { mutableIntStateOf(0) }
    var flipped by remember { mutableStateOf(false) }

    viewModel.loadFlashCardsByLesson(lessonId)

    if (flashCards.isNotEmpty()) {

        val currentCard = flashCards.getOrNull(currentIndex)

        if (currentIndex >= flashCards.size) {
            // Fin de la lección
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("¡Has terminado la lección!", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = {
                        currentIndex = 0
                        flipped = false
                    }) {
                        Text("Volver a empezar")
                    }
                    Button(onClick = { navigateToFlashCardScreen(lessonId) }) { Text("Finalizar") }
                }
            }
            return
        }


        val animatedRotationY by animateFloatAsState(
            targetValue = if (flipped) 180f else 0f,
            animationSpec = tween(durationMillis = 500),
            label = "flip"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .graphicsLayer {
                        rotationY = animatedRotationY
                        cameraDistance = 12 * density
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
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                } else {
                    Text(
                        text = currentCard.answer,
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
                    if (currentIndex <= flashCards.lastIndex) {
                        currentIndex++
                        flipped = false
                    }
                }) {
                    Text("Siguiente")
                }

            }
        }

    }

}



