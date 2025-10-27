package com.example.flashmind.presentation.ui.summary
import androidx.compose.foundation.clickable
import com.example.flashmind.R

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.domain.model.SummaryModel
import com.example.flashmind.presentation.ui.test.EmptyTestList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummariesScreen(
    lessonId: Int,
    lessonTitle: String?,
    viewModel: SummariesListViewModel = hiltViewModel(),
    onSummaryClick: (summaryId: Int, summaryTitle: String) -> Unit,
    onAddSummaryClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(lessonId) {
        viewModel.loadSummaries(lessonId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(lessonTitle ?: "My saved summaries", fontWeight = FontWeight.Bold, fontSize = 25.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSummaryClick) {
                Icon(Icons.Filled.Add, contentDescription = "Add Summary")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is SummariesListUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is SummariesListUiState.Error -> {
                    Text("Error: ${state.error}", color = MaterialTheme.colorScheme.error)
                }
                is SummariesListUiState.Empty -> {
                    EmptyTestList(tittle = "No summaries available", subtitle = "Add a new summary to get started.")
                }
                is SummariesListUiState.Success -> {
                    SummariesList(
                        summaries = state.summaries,
                        onSummaryClick = { summary ->
                            onSummaryClick(summary.summaryId, summary.title)
                        },
                        onDeleteClick = { viewModel.deleteSummary(it.summaryId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SummariesList(
    summaries: List<SummaryModel>,
    onSummaryClick: (SummaryModel) -> Unit,
    onDeleteClick: (SummaryModel) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = summaries, key = { it.summaryId }) { summary ->

            SummaryItem(
                title = summary.title,
                subtitle = summary.generatedSummary ,
                createdAt = "creado el 26/10/2023",
                onViewClick = { onSummaryClick(summary) },
                onDeleteClick = { onDeleteClick(summary) }
            )
        }
    }
}

@Composable
fun SummaryItem(
    title: String,
    subtitle: String,
    createdAt:String,
    onViewClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable{onViewClick()},
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
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = createdAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


            Spacer(modifier = Modifier.width(16.dp))

            Row {
                IconButton(onClick = onViewClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.visibility_icon),
                        contentDescription = "Ver Resumen",
                        modifier = modifier.size(25.dp)
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar Resumen",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}