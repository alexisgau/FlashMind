package com.example.flashmind.presentation.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.flashmind.R
import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.domain.model.UserData


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToLessonContent: (lessonId: Int, lessonTitle: String) -> Unit,
    onNavigateToAddCategory: () -> Unit,
    onNavigateToAccountSettings: (String) -> Unit,
    onNavigateToAddLesson: (categoryId: Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val categoryToDelete by viewModel.categoryToDelete.collectAsStateWithLifecycle()
    val lessonToDelete by viewModel.lessonToDelete.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            val userData = (uiState as? HomeUiState.Success)?.userData ?: UserData.Init
            TopBar(
                modifier = Modifier.padding(top = 20.dp, start = 16.dp),
                userData = userData,
                navigateToAccountSettings = onNavigateToAccountSettings
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddCategory,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir nueva categoría")
            }
        }
    ) { innerPadding ->

        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding), contentAlignment = Alignment.Center
                ) {
                    Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                }
            }

            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    item {
                        HomeCard()
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = "MIS CATEGORÍAS",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    state.categories.forEach { category ->
                        val isExpanded = state.expandedCategoryIds.contains(category.id)
                        val lessons = state.lessonsMap[category.id]
                        val lessonCount = state.lessonCounts[category.id] ?: 0

                        item(key = "cat_${category.id}") {
                            ExpandableCategoryItem(
                                category = category,
                                isExpanded = isExpanded,
                                lessonCount = lessonCount,
                                onToggleExpand = { viewModel.toggleCategoryExpansion(category.id) },
                                onDeleteRequest = { viewModel.requestCategoryDeletion(category) },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                content = {
                                    Column(Modifier.fillMaxWidth()) {
                                        if (lessons == null) {
                                            Box(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 20.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(
                                                        24.dp
                                                    )
                                                )
                                            }
                                        } else if (lessons.isEmpty()) {
                                            Text(
                                                "No hay lecciones en esta categoría.",
                                                modifier = Modifier.padding(
                                                    start = 32.dp,
                                                    top = 8.dp,
                                                    bottom = 16.dp
                                                ),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        } else {
                                            lessons.forEach { lesson ->
                                                LessonItem(
                                                    lesson = lesson,
                                                    onClick = { onNavigateToLessonContent(lesson.id, lesson.tittle) },
                                                    onDeleteClick = {
                                                        viewModel.requestLessonDeletion(
                                                            lesson
                                                        )
                                                    },
                                                    onEditClick = {},
                                                    modifier = Modifier.padding(
                                                        start = 16.dp,
                                                        end = 16.dp,
                                                        top = 4.dp,
                                                        bottom = 4.dp
                                                    )
                                                )
                                                Divider(modifier = Modifier.padding(horizontal = 16.dp))
                                            }
                                        }

                                        TextButton(
                                            onClick = { onNavigateToAddLesson(category.id) },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Add,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text("Añadir Lección")
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    categoryToDelete?.let { category ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelDeletion() },
            title = { Text("Borrar Categoría") },
            text = { Text("¿Seguro que quieres borrar \"${category.name}\"? Todas sus lecciones también se borrarán.") },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDeleteCategory() }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelDeletion() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    lessonToDelete?.let { lesson ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelDeletion() },
            title = { Text("Borrar Lección") },
            text = { Text("¿Seguro que quieres borrar la lección \"${lesson.tittle}\"?") },
            confirmButton = {
                TextButton(onClick = { viewModel.confirmDeleteLesson() }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelDeletion() }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ExpandableCategoryItem(
    category: Category,
    isExpanded: Boolean,
    lessonCount: Int,
    onToggleExpand: () -> Unit,
    onDeleteRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpand),
            shape = if (isExpanded) {
                RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            } else {
                RoundedCornerShape(16.dp)
            },
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.folder_icon),
                    contentDescription = "Categoría",
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
                        text = "$lessonCount Lecciones",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LocalContentColor.current.copy(alpha = 0.7f)
                    )
                }
                IconButton(onClick = onDeleteRequest) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar Categoría",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
                Icon(
                    painter = if (isExpanded) painterResource(R.drawable.arrow_circle_up_icon) else painterResource(
                        R.drawable.arrow_circle_down_icon
                    ),
                    contentDescription = if (isExpanded) "Colapsar" else "Expandir",
                    modifier = Modifier.size(36.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        AnimatedVisibility(visible = isExpanded) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun LessonItem(
    lesson: Lesson,
    onClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.play_lesson),
            contentDescription = "Lección",
            modifier = Modifier.size(32.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = lesson.tittle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Toca para ver el contenido", // Puedes cambiar este subtítulo
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = onEditClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Edit,
                contentDescription = "Editar Lección",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar Lección",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AddLessonButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Añadir Lección")
    }
}

@Composable
fun CategoryItem(
    category: Category,
    onClick: () -> Unit,
    countLesson: Int,
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
                painter = painterResource(R.drawable.category_icon),
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
                    text = "$countLesson Lessons",
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
fun TopBar(
    modifier: Modifier = Modifier,
    userData: UserData,
    navigateToAccountSettings: (String) -> Unit
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
                    IconButton(onClick = { navigateToAccountSettings(userData.imageUrl) }) {
                        AsyncImage(
                            model = userData.imageUrl,
                            contentDescription = "Profile picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(40.dp)
                                .border(2.dp, Color.Gray, CircleShape)
                        )
                    }

                } else {
                    IconButton(onClick = { navigateToAccountSettings(userData.imageUrl) }) {
                        Icon(
                            painter = painterResource(R.drawable.default_profile_ic),
                            contentDescription = "Default Profile",
                            tint = Color.Unspecified,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(40.dp)
                                .border(2.dp, Color.Gray, CircleShape)
                        )
                    }

                }
            }

            is UserData.Error, UserData.Init -> {
                Text(
                    text = "Hello user!",
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




