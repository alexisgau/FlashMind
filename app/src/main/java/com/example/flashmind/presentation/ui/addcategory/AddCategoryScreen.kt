package com.example.flashmind.presentation.ui.addcategory

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.domain.model.Category
import com.example.flashmind.presentation.viewmodel.AddCategoryState
import com.example.flashmind.presentation.viewmodel.HomeViewModel

@Composable
fun AddCategoryScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToHome: () -> Unit
) {
    val editState by viewModel.uiState.collectAsStateWithLifecycle()
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Text(
                text = "🗂️",
                fontSize = 64.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )


            Text(
                text = "Crear nueva categoría",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))


            Text(
                text = "Organiza tus lecciones agrupándolas por categoría",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            AddCategoryForm(
                name = name,
                onNameChange = { name = it },
                description = description,
                onDescriptionChange = { description = it },
                selectedColor = selectedColor,
                onColorSelected = { selectedColor = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = navigateToHome,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Volver")
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        viewModel.insertCategory(
                            Category(0, name, description, selectedColor)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotBlank()
                ) {
                    Text("Agregar")
                }
            }
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
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Nombre de la categoría") },
            placeholder = { Text("Introduce el nombre...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Descripción") },
            placeholder = { Text("Describe esta categoría...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            maxLines = 3
        )

        Text(
            text = "Elige un color",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
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
                    Text(text = "Color seleccionado", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                colors.forEach { color ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .background(Color(color.toColorInt()))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
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
        onColorSelected = { selectedColor = it })
}

@Composable
fun SelectedColor(colorHex: String) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .background(Color(colorHex.toColorInt()))
    )
}

