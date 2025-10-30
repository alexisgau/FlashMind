package com.example.flashmind.presentation.ui.summary.detail

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.R
import com.example.flashmind.domain.model.SummaryModel
import com.example.flashmind.presentation.ui.test.run.ErrorQuizGenerator
import com.example.flashmind.presentation.ui.test.run.QuizLoadingScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryViewScreen(
    modifier: Modifier = Modifier,
    summaryId: Int? = null,
    contentFile: String? = null,
    summaryTittle: String? = null,
    lessonId: Int? = null,
    onClickBack: () -> Unit,
    viewModel: SummaryViewModel = hiltViewModel()
) {


    val generationState by viewModel.generationState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = contentFile, key2 = lessonId) {
        when {
            contentFile != null && lessonId != null -> {
                viewModel.generateAndSaveSummary(
                    contentFile,
                    lessonId,
                    summaryTittle ?: "Summary generated"
                )
            }

            else -> {
                Log.e("TestScreen", "Invalid navigation arguments")
                viewModel.loadSummaryById(summaryId ?: 3)
            }
        }
    }

// Efecto para observar eventos de descarga
    LaunchedEffect(Unit) {
        viewModel.downloadEvents.collectLatest { event ->
            when (event) {
                is DownloadEvent.Success -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(event.message)
                    }
                }

                is DownloadEvent.Error -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            "Error: ${event.error}",
                            duration = SnackbarDuration.Long
                        )
                    }
                }
            }
        }
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    val title =
                        (generationState as? SummaryGenerationState.Success)?.newSummary?.title
                            ?: summaryTittle ?: "Resumen"
                    Text(
                        title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    val isLoading = generationState is SummaryGenerationState.Loading
                    IconButton(
                        onClick = onClickBack,
                        enabled = !isLoading
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    if (generationState is SummaryGenerationState.Success) {
                        val currentSummary =
                            (generationState as SummaryGenerationState.Success).newSummary
                        IconButton(onClick = {
                            viewModel.saveSummaryAsPdf(context, currentSummary)
                        }) {
                            Icon(
                                painter = painterResource(R.drawable.download_icon),
                                modifier = Modifier.size(25.dp),
                                contentDescription = "Descargar PDF"
                            )
                        }
                    }
                }
            )
        },

        ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val uiState = generationState) {

                is SummaryGenerationState.Error -> {
                    ErrorQuizGenerator(
                        errorMessage = uiState.error,
                        onRetry = { },
                        onBack = onClickBack
                    )

                }

                SummaryGenerationState.Loading -> {

                    QuizLoadingScreen(generateName = "summary", generatingName = "summary")
                }

                is SummaryGenerationState.Success -> {

                    SummaryScreen(uiState.newSummary)
                }

                SummaryGenerationState.Idle -> {}
            }
        }
    }
}

@Composable
fun SummaryScreen(summaryModel: SummaryModel) {
    val verticalScroll = rememberScrollState()
    val lines = remember(summaryModel.generatedSummary) {
        summaryModel.generatedSummary.lines()
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(verticalScroll)
    ) {
        lines.forEach { line ->
            val trimmedLine = line.trim()

            when {
                // Encabezado (##)
                trimmedLine.startsWith("## ") -> {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = trimmedLine.removePrefix("## ").trim(),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(8.dp))
                }
                // Encabezado (###)
                trimmedLine.startsWith("### ") -> {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = trimmedLine.removePrefix("### ").trim(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(4.dp))
                }
                // Ítem de Lista (* o -)
                trimmedLine.startsWith("* ") || trimmedLine.startsWith("- ") -> {
                    val textWithoutBullet = trimmedLine.substring(2).trim()

                    val indentationLevel = line.takeWhile { it.isWhitespace() }.count() / 2
                    val startPadding = (indentationLevel * 8).dp

                    // Parsea y muestra texto con/sin negritas
                    val parts = remember(textWithoutBullet) { parseBoldText(textWithoutBullet) }
                    AnnotatedStringText(
                        parts = parts,
                        modifier = Modifier.padding(start = startPadding + 8.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                }

                trimmedLine.isNotEmpty() -> {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = trimmedLine,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                // Líneas vacías se ignoran, usamos Spacers para control
                else -> { /* No hacer nada */
                }
            }
        }
    }
}


// Parsea texto buscando **negritas** y devuelve una lista de pares (texto, esNegrita)
fun parseBoldText(text: String): List<Pair<String, Boolean>> {
    val parts = mutableListOf<Pair<String, Boolean>>()
    val regex = Regex("""\*\*(.*?)\*\*""")
    var lastIndex = 0

    regex.findAll(text).forEach { matchResult ->
        val boldText = matchResult.groupValues[1]
        val startIndex = matchResult.range.first
        val endIndex = matchResult.range.last

        // Añade texto normal antes de la negrita
        if (startIndex > lastIndex) {
            parts.add(text.substring(lastIndex, startIndex) to false)
        }
        // Añade la parte en negrita
        parts.add(boldText to true)
        lastIndex = endIndex + 1
    }

    // Añade el texto normal restante
    if (lastIndex < text.length) {
        parts.add(text.substring(lastIndex) to false)
    }

    // Si no se encontraron negritas,normal
    if (parts.isEmpty() && text.isNotEmpty()) {
        parts.add(text to false)
    }

    return parts
}

// Composable para renderizar el texto parseado con negritas
@Composable
fun AnnotatedStringText(parts: List<Pair<String, Boolean>>, modifier: Modifier = Modifier) {
    Text(
        buildAnnotatedString {
            parts.forEach { (text, isBold) ->
                if (isBold) {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text)
                    }
                } else {
                    append(text)
                }
            }
        },
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}