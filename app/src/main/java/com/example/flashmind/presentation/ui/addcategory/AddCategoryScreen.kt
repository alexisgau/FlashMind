package com.example.flashmind.presentation.ui.addcategory

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.AddCategory
import com.example.flashmind.domain.model.Category
import com.example.flashmind.presentation.viewmodel.AddCategoryState
import com.example.flashmind.presentation.viewmodel.HomeViewModel
import androidx.core.graphics.toColorInt

@Composable
fun AddCategoryScreen(viewmodel: HomeViewModel = hiltViewModel(), navigateToHome: () -> Unit) {

    val editState by viewmodel.uiState.collectAsStateWithLifecycle()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FF5733") }

    LaunchedEffect(editState) {
        when (editState) {
            is AddCategoryState.Error -> Log.e("AddCategoryScreen", "Error")
            AddCategoryState.Loading -> Log.e("AddCategoryScreen", "Loading")
            AddCategoryState.Success -> navigateToHome()
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
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
                    viewmodel.insertCategory(
                        Category(
                            0,
                            name,
                            description,
                            selectedColor
                        )
                    )
                }) {
                    Text("Add")
                }
            }

            AddCategoryForm(
                name = name,
                onNameChange = { name = it },
                description = description,
                onDescriptionChange = { description = it },
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )
        }
    }
}

@Composable
fun AddCategoryForm(
    name: String,
    onNameChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
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
            value = name,
            onValueChange = onNameChange,
            label = { Text("Category Name") },
            placeholder = { Text("Enter category name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Description") },
            placeholder = { Text("Enter category description") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            maxLines = 3
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
                                        .background(Color(android.graphics.Color.parseColor(color)))
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

@Preview(showBackground = true)
@Composable
fun AddCategoryPreview() {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("") }

    AddCategoryForm(
        name = name,
        onNameChange = { name = it },
        description = description,
        onDescriptionChange = { description = it },
        selectedColor = selectedColor,
        onColorSelected = { selectedColor = it }
    )
}

@Composable
fun SelectedColor(colorHex: String) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(Color(android.graphics.Color.parseColor(colorHex)))
    )
}

