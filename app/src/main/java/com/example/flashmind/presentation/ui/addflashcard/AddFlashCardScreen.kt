package com.example.flashmind.presentation.ui.addflashcard

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flashmind.presentation.ui.addcategory.AddCategoryForm
import com.example.flashmind.presentation.viewmodel.FlashCardViewModel

@Composable
fun AddFlashCardScreen(lessonId: Int, viewModel: FlashCardViewModel = hiltViewModel()) {







}

@Composable
fun AddFlashCardForm(
    question: String,
    onQuestionChange: (String) -> Unit,
    answer: String,
    onAnswerChange: (String) -> Unit,
    selectedColor: String,
    onColorSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    val colors = listOf(
        "#FF5733", // Naranja
        "#33FF57", // Verde
        "#3357FF", // Azul
        "#FF33A1", // Rosa
        "#F3F3F3"  // Gris claro
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        OutlinedTextField(
            value = question,
            onValueChange = onQuestionChange,
            label = { Text("Question") },
            placeholder = { Text("Enter your question") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = answer,
            onValueChange = onAnswerChange,
            label = { Text("Answer") },
            placeholder = { Text("Enter your answer") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        Text("Pick a Color", style = MaterialTheme.typography.bodyMedium)

        Box(modifier = Modifier.padding(vertical = 8.dp)) {
            OutlinedButton(
                onClick = { expanded = true },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color(selectedColor.toColorInt())
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(Color(selectedColor.toColorInt()))
                    )
                    Spacer(modifier = Modifier.width(8.dp))

                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                colors.forEach { color ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(Color(color.toColorInt()))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(color)
                            }
                        },
                        onClick = {
                            onColorSelected(color)
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

//    Log.i("FlashCardScreen", "id:$lessonId")
//    var question by remember { mutableStateOf("") }
//    var answer by remember { mutableStateOf("") }
//    var selectedColor by remember { mutableStateOf("#FF5733") }


//    AddFlashCardForm(
//        question = question,
//        onQuestionChange = { question = it },
//        answer = answer,
//        onAnswerChange = { answer = it },
//        selectedColor = selectedColor,
//        onColorSelected = { selectedColor = it }
//    )