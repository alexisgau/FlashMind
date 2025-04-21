package com.example.flashmind.presentation.ui.home

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    navigateToFlashCard:(lessonId: Int) -> Unit
) {
    val state = viewModel.categoriesState.collectAsStateWithLifecycle()
    val lessons by viewModel.lessons.collectAsStateWithLifecycle()
    val selectedCategoryId = remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = { TopBar() },
        floatingActionButton = {
            FloatingActionButton(
                //TODO AGREGAR UN MENSAJE DE ERROR SI ES NULL
                onClick = {
                    selectedCategoryId.value?.let { id ->
                        navigateToAddLesson(id)
                    }
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar lección")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            GopayCard()

            when (state.value) {
                is CategoryState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is CategoryState.Success -> {
                    val categories = (state.value as CategoryState.Success).categories

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "CATEGORIES",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.alignByBaseline()
                        )
                        TextButton(
                            onClick = { navigateToAddCategory() },
                            modifier = Modifier.alignByBaseline()
                        ) {
                            Text("Añadir")
                        }
                    }

                    CategoryLazyRow(
                        categories = categories,
                        selectedCategoryId = selectedCategoryId,
                        onCategorySelected = { categoryId ->
                            selectedCategoryId.value = categoryId
                            viewModel.getAllLessonByCategory(categoryId)
                        }
                    )
                }
                is CategoryState.Error -> {
                    Text(
                        "Error loading categories",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            when (lessons) {
                is LessonsState.Error -> Text("ERROR")
                LessonsState.IsEmpty -> Text("No tienes ninguna lección, agrega una.")
                LessonsState.Loading -> Text("Presiona sobre una categoría para empezar")
                is LessonsState.Success -> {
                    val lessonList = (lessons as LessonsState.Success).lessons
                    LessonItem(lessonList,navigateToFlashCard)
                }
            }
        }
    }
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
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
fun CategoryItem(category: Category){

    Card(colors = CardDefaults.cardColors(Color.Gray), modifier = Modifier.height(200.dp).width(180.dp)) {

        Column(Modifier.fillMaxSize()) {

            Text(category.name, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
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
                onClick = {
                    onCategorySelected(category.id)
                },
                label = { Text(category.name) },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun LessonItem(lessons:List<Lesson>,navigateToFlashCard:(lessonId: Int) -> Unit){

    LazyColumn {

        items(lessons) {

            Text(it.tittle, Modifier.clickable{navigateToFlashCard(it.id)})

        }
    }



}
