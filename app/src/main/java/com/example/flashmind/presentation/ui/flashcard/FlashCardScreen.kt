package com.example.flashmind.presentation.ui.flashcard

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FlashCardScreen(
    lessonId: Int,
    navigateToHome: () -> Unit,
    navigateToAddFlashCard: (Int) -> Unit
) {
    Log.i("FlashCardScreen", "id:$lessonId")

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { navigateToHome() }) {
                    Text("Back")
                }

                Button(onClick = {
                    // Acción para empezar el juego
                }) {
                    Text("Start")
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navigateToAddFlashCard(lessonId)
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar flashcard")
            }
        }
    ) {

    }
}
