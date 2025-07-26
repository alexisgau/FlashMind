package com.example.flashmind.presentation.ui.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.flashmind.R
import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.model.UserData

import com.example.flashmind.presentation.viewmodel.HomeViewModel


@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    // Los parámetros de navegación cambian para reflejar el nuevo flujo
    onNavigateToLessons: (categoryId: Int, categoryName: String) -> Unit,
    onNavigateToAddCategory: () -> Unit,
    onNavigateToAccountSettings: (String) -> Unit
) {
    val categoryState by viewModel.categoriesState.collectAsStateWithLifecycle()
    val userData by viewModel.userData.collectAsStateWithLifecycle()
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    Scaffold(
        topBar = {
            TopBar(
                modifier = Modifier.padding(top = 20.dp, start = 16.dp),
                userData = userData,
                navigateToAccountSettings = onNavigateToAccountSettings
            )
        },
        floatingActionButton = {
            // El FAB ahora tiene una única y clara función: añadir categoría.
            FloatingActionButton(
                onClick = { onNavigateToAddCategory() },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir nueva categoría")
            }
        }
    ) { innerPadding ->
        // La LazyColumn reemplaza a la Column para un mejor rendimiento con listas largas
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp) // Espacio para que no choque con el FAB
        ) {
            // 1. Elementos fijos de la parte superior
            item {
                HomeCard()
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "MY CATEGORIES",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // 2. Lógica para mostrar la lista de categorías
            when (val state = categoryState) {
                is CategoryState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                is CategoryState.Success -> {
                    items(state.categories) { category ->
                        CategoryItem(
                            category = category,
                            onClick = {
                                onNavigateToLessons(category.id, category.name)
                            },
                            onDeleteRequest = { categoryToDelete = it },
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                    }
                }
                is CategoryState.Error -> {
                    item {
                        Text(
                            text = "Error al cargar categorías",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    // El diálogo de confirmación para eliminar
    categoryToDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Eliminar categoría") },
            text = { Text("¿Estás seguro de que deseas eliminar \"${category.name}\"? Se borrarán todas sus lecciones.") },
            confirmButton = {
                TextButton(onClick = {
                    {  } // Llama al ViewModel para borrar
                    categoryToDelete = null
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// -- Composable Auxiliar para el Item de Categoría (usado arriba) --
@Composable
fun CategoryItem(
    category: Category,
    onClick: () -> Unit,
    onDeleteRequest: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Face, // O un ícono dinámico
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "${"1" ?: 0} Lecciones", // Asumiendo que tienes un contador
                    style = MaterialTheme.typography.bodyMedium,
                    color = LocalContentColor.current.copy(alpha = 0.7f)
                )
            }
            IconButton(onClick = { onDeleteRequest(category) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar Categoría",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = onActionClick) {
            Text(actionText)
        }
    }
}


@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    userData: UserData,
    navigateToAccountSettings:(String)->Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (userData) {
            is UserData.Success -> {
                Text(
                    text = "Hello, ${userData.name}!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (userData.imageUrl.isNotEmpty()) {
                    IconButton(onClick = {navigateToAccountSettings(userData.imageUrl)}) {
                        AsyncImage(
                        model = userData.imageUrl,
                        contentDescription = "Profile picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(40.dp)
                            .border(2.dp, Color.Gray, CircleShape)
                    )}

                } else {
                    Icon(
                        painter = painterResource(R.drawable.ic_launcher_background),
                        contentDescription = "Default Profile",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(40.dp)
                            .border(2.dp, Color.Gray, CircleShape)
                            .clickable { navigateToAccountSettings(userData.imageUrl) }
                    )
                }
            }

            is UserData.Error, UserData.Init -> {
                Text(
                    text = "Hello!",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = "Default Profile",
                    modifier = Modifier
                        .clip(CircleShape)
                        .size(40.dp)
                        .border(2.dp, Color.Gray, CircleShape)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CategoryLazyRowPreview() {
    val categories = listOf(
        Category(1, "Comida", "", "#FF5733"),
        Category(2, "Bebidas", "", "#33FF57"),
        Category(3, "Snacks", "", "#3357FF"),
        Category(4, "Postres", "", "#FF33A1"),
        Category(5, "Otros", "", "#F3F3F3"),
    )

    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategoryId == category.id,
                onClick = { selectedCategoryId = category.id },
                label = { Text(category.name) },
                modifier = Modifier
                    .padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun CategoryLazyRow(
    categories: List<Category>,
    selectedCategoryId: MutableState<Int?>,
    onCategorySelected: (Int) -> Unit,
    onDeleteCategory: (Category) -> Unit
) {
    var categoryToDelete by remember { mutableStateOf<Category?>(null) }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(categories) { category ->
            FilterChip(
                selected = selectedCategoryId.value == category.id,
                onClick = {
                    onCategorySelected(category.id)
                },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.name,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar categoría",
                            modifier = Modifier
                                .size(18.dp)
                                .clickable {
                                    categoryToDelete = category
                                },
                            tint = Color.Black
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = Color(category.color.toColorInt())
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }

    // Dialogo de confirmación
    categoryToDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("Eliminar categoría") },
            text = { Text("¿Desea eliminar la categoría \"${category.name}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteCategory(category)
                    categoryToDelete = null
                }) {
                    Text("Sí")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    categoryToDelete = null
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}


@Composable
fun LessonItem(
    lessons: List<Lesson>,
    navigateToFlashCard: (lessonId: Int) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(lessons) { lesson ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navigateToFlashCard(lesson.id) },
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = lesson.tittle,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Ir a la lección",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }


            }
        }
    }
}

@Composable
fun HomeCard() {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)) // fondo oscuro
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Texto
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "FlashMind",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Studying has never\nbeen easier with\nFlashMind",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }


            Image(
                painter = painterResource(id = R.drawable.mastan),
                contentDescription = "Mastan illustration",
                modifier = Modifier
                    .width(150.dp)
                    .height(500.dp)
            )
        }
    }
}



