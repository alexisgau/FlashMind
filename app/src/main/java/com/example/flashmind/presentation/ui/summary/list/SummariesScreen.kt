package com.example.flashmind.presentation.ui.summary.list

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.R
import com.example.flashmind.domain.model.SummaryModel
import com.example.flashmind.presentation.utils.cleanMarkdownForPreview
import com.example.flashmind.presentation.utils.toFormattedDateString

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
                title = {
                    Text(
                        lessonTitle ?: stringResource(id = R.string.summaries_list_title),
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSummaryClick) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = stringResource(id = R.string.summaries_list_add_button)
                )
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
                    Text(
                        text = "${stringResource(id = R.string.error_prefix)} ${state.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                is SummariesListUiState.Empty -> {
                    EmptySummariesList(
                        tittle = stringResource(id = R.string.summaries_list_empty_title),
                        subtitle = stringResource(id = R.string.summaries_list_empty_description)
                    )
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
                subtitle = summary.generatedSummary,
                createdAt = summary.creationDate.toFormattedDateString(),
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
    createdAt: String,
    onViewClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val cleanSubtitle = remember(subtitle) {
        cleanMarkdownForPreview(subtitle)
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onViewClick() },
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
                    text = cleanSubtitle,
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
                        contentDescription = stringResource(id = R.string.summary_view_button),
                        modifier = modifier.size(25.dp)
                    )
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(id = R.string.summary_delete_button),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun EmptySummariesList(
    modifier: Modifier = Modifier,
    tittle: String = stringResource(id = R.string.summaries_list_empty_title),
    subtitle: String = stringResource(id = R.string.summaries_list_empty_description)
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.empty_summary),
            contentDescription = stringResource(id = R.string.summaries_list_empty_title),
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