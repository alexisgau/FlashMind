package com.example.flashmind.presentation.ui.test

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.R
import com.example.flashmind.domain.model.TestModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestScreen(
    viewModel: TestViewModel = hiltViewModel(),
    lessonId: Int = 1,
    onNavigateBack:()-> Unit,
    onClickTest: (Int, String) -> Unit,
    navigateToNewTest: () -> Unit
) {

    val uiState by viewModel.testsState.collectAsStateWithLifecycle()

    LaunchedEffect(lessonId) {
        viewModel.loadTestsForLesson(lessonId)

    }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("My tests") },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = navigateToNewTest) {
                    Icon(Icons.Filled.Add, contentDescription = "Nuevo Test") // Añade ícono
                }
            }
        ) { innerPadding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {

                when (val state = uiState) {
                    is TestsListUiState.Loading -> {
                        CircularProgressIndicator()
                    }

                    is TestsListUiState.Error -> {
                        Text(
                            "Error: ${state.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    is TestsListUiState.Empty -> {
                        EmptyTestList()
                    }

                    is TestsListUiState.Success -> {
                        // Muestra la lista solo en caso de éxito
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items = state.tests, key = { it.testId }) { test ->

                                TestItem(
                                    testId = test.testId,
                                    title = test.title,
                                    createdAt = "10/10/2023",
                                    onStartClick = { onClickTest(it, test.title) },
                                    onDeleteClick = { viewModel.deleteTest(test.testId) }
                                )
                            }
                        }
                    }
                }
            }
        }

}

@Composable
fun TestItem(
    modifier: Modifier = Modifier,
    testId: Int,
    title: String,
    createdAt: String,
    onStartClick: (Int) -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = createdAt,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                StartButton(testId = testId, onStartClick = onStartClick)
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartButton(
    testId: Int,
    onStartClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorFondoGris = Color(0xFFF0F0F0)
    val colorCirculoAzul = Color(0xFF03A9F4)
    val colorIconoBlanco = Color.White
    val colorTextoGris = Color(0xFF333333)

    Surface(
        onClick = { onStartClick(testId) },
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(50),
        color = colorFondoGris
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(start = 6.dp, end = 16.dp, top = 6.dp, bottom = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(color = colorCirculoAzul, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {

                Icon(

                 painter = painterResource(R.drawable.play_icon),
                    contentDescription = "Empezar",
                    modifier = Modifier.size(18.dp),
                    tint = colorIconoBlanco
                )
            }

            Spacer(modifier = Modifier.width(10.dp))


            Text(
                text = "Empezar",
                style = MaterialTheme.typography.bodyLarge,
                color = colorTextoGris,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun EmptyTestList(
    modifier: Modifier = Modifier,
    tittle: String = "¡Aún no tienes tests!",
    subtitle: String = "Crea un nuevo test para empezar a practicar"
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.folder_icon),
            contentDescription = "No hay tests",
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = tittle,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}