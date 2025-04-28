package com.example.flashmind.presentation.ui.home

import android.widget.Toast
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.GopayCard
import com.example.flashmind.R
import com.example.flashmind.domain.model.Category
import com.example.flashmind.domain.model.Lesson
import com.example.flashmind.presentation.viewmodel.CategoryState
import com.example.flashmind.presentation.viewmodel.HomeViewModel
import com.example.flashmind.presentation.viewmodel.LessonsState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToAddCategory: () -> Unit,
    navigateToAddLesson: (id: Int) -> Unit,
    navigateToFlashCard: (lessonId: Int) -> Unit
) {
    val state = viewModel.categoriesState.collectAsStateWithLifecycle()
    val lessons by viewModel.lessons.collectAsStateWithLifecycle()
    val selectedCategoryId = remember { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    Scaffold(
        topBar = { TopBar(modifier = Modifier.padding(top = 20.dp, start = 16.dp)) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text("New lesson") },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                onClick = {
                    selectedCategoryId.value?.let { id ->
                        navigateToAddLesson(id)
                    } ?: Toast.makeText(
                        context,
                        "Debes seleccionar una categoría primero",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()

        ) {

            GopayCard()

            Spacer(modifier = Modifier.height(16.dp))

            SectionHeader(
                title = "CATEGORÍAS",
                actionText = "+ Añadir",
                onActionClick = navigateToAddCategory
            )

            when (val categoryState = state.value) {
                is CategoryState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is CategoryState.Success -> {
                    CategoryLazyRow(
                        categories = categoryState.categories,
                        selectedCategoryId = selectedCategoryId,
                        onCategorySelected = { categoryId ->
                            selectedCategoryId.value = categoryId
                            viewModel.getAllLessonByCategory(categoryId)
                        }
                    )
                }
                is CategoryState.Error -> {
                    Text(
                        text = "Error al cargar categorías",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sección Lecciones
            Text(
                text = "LESSONS",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (val lessonState = lessons) {
                is LessonsState.Error -> {
                    Text(
                        text = "Error cargando lecciones",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                LessonsState.IsEmpty -> {
                    Text(
                        text = "No tienes ninguna lección aún. Agrega una.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                LessonsState.Loading -> {
                    Text(
                        text = "Selecciona una categoría para ver sus lecciones",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                is LessonsState.Success -> {
                    LessonItem(
                        lessons = lessonState.lessons,
                        navigateToFlashCard = navigateToFlashCard
                    )
                }
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
fun TopBar(modifier: Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Hello, Name!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Icon(
            painter = painterResource(R.drawable.ic_launcher_background),
            contentDescription = "Profile",
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp)
                .border(2.dp, Color.Gray, CircleShape)
        )
    }
}


@Composable
fun CategoryItem(category: Category) {

    Card(
        colors = CardDefaults.cardColors(Color.Gray),
        modifier = Modifier
            .height(200.dp)
            .width(180.dp)
    ) {

        Column(Modifier.fillMaxSize()) {

            Text(
                category.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            Text(category.description, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)

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
    onCategorySelected: (Int) -> Unit
) {

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        items(categories) { category ->
            FilterChip(
                selected = selectedCategoryId.value == category.id,
                colors = FilterChipDefaults.filterChipColors(Color(category.color.toColorInt())),
                onClick = {
                    onCategorySelected(category.id)
                },
                label = {
                    Text(
                        text = category.name,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
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
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Ir a la lección",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}




