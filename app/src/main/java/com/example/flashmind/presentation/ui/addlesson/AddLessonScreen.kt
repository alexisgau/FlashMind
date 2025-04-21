package com.example.flashmind.presentation.ui.addlesson

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.presentation.viewmodel.HomeViewModel

@Composable
fun AddLessonScreen(categoryId: Int, navigateToHome:()->Unit, viewModel: HomeViewModel = hiltViewModel()) {

    var tittle by remember { mutableStateOf("")}

Log.i("AddLessonScreen","id : $categoryId")


        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = navigateToHome) {
                    Text("Back")
                }
                Button(onClick = {

                    viewModel.insertLesson(
                        Lesson(0,tittle,categoryId)
                    )

                }) {
                    Text("Add")
                }
            }

            OutlinedTextField(
                value = tittle,
                onValueChange = {tittle = it},
                label = { Text("Lesson name") },
                placeholder = { Text("Enter lesson name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true
            )


        }
    }
