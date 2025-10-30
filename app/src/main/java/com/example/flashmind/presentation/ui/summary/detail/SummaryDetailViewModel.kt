package com.example.flashmind.presentation.ui.summary.detail

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import androidx.core.graphics.withSave
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.SummaryModel
import com.example.flashmind.domain.usecase.summary.CreateSummaryUseCase
import com.example.flashmind.domain.usecase.summary.GenerateSummaryUseCase
import com.example.flashmind.domain.usecase.summary.GetSummaryByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val createSummaryUseCase: CreateSummaryUseCase,
    private val generateSummaryUseCase: GenerateSummaryUseCase,
    private val getSummaryByIdUseCase: GetSummaryByIdUseCase
) : ViewModel() {


    private val _generationState =
        MutableStateFlow<SummaryGenerationState>(SummaryGenerationState.Idle)
    val generationState: StateFlow<SummaryGenerationState> = _generationState.asStateFlow()

    private val _downloadChannel = Channel<DownloadEvent>()
    val downloadEvents = _downloadChannel.receiveAsFlow()


    private var showLoadingJob: Job? = null


    fun generateAndSaveSummary(
        originalText: String,
        lessonId: Int,
        summaryTitle: String
    ) {
        // Evita iniciar una nueva generación si ya está en proceso
        if (_generationState.value == SummaryGenerationState.Loading) return

        _generationState.value = SummaryGenerationState.Loading
        viewModelScope.launch {
            try {
                // Generar resumen con IA
                val generatedSummaryText = generateSummaryUseCase(originalText)
                if (generatedSummaryText.isBlank()) {
                    throw Exception("La IA no generó un resumen.")
                }
                Log.i("SummaryViewModel", "Resumen generado por IA.")


                val summaryToSave = SummaryModel(
                    summaryId = 0,
                    lessonId = lessonId,
                    generatedSummary = generatedSummaryText,
                    title = summaryTitle,
                    creationDate = System.currentTimeMillis()

                )


                val newSummaryId = createSummaryUseCase(originalText, summaryToSave)
                if (newSummaryId <= 0) {
                    throw Exception("Error al guardar el resumen en la base de datos.")
                }
                Log.i("SummaryViewModel", "Resumen guardado con ID: $newSummaryId")

                // 4. Actualizar estado a Success con el resumen completo (incluyendo el ID)
                val savedSummary = summaryToSave.copy(summaryId = newSummaryId.toInt())
                _generationState.value = SummaryGenerationState.Success(savedSummary)

            } catch (e: Exception) {
                Log.e("SummaryViewModel", "Error en generateAndSaveSummary", e)
                _generationState.value = SummaryGenerationState.Error(
                    e.message ?: "Error desconocido al generar resumen"
                )
            }
        }
    }

    fun loadSummaryById(summaryId: Int) {

        showLoadingJob?.cancel()

        viewModelScope.launch {
            showLoadingJob = launch {
                delay(150L)
                if (_generationState.value == SummaryGenerationState.Idle || _generationState.value !is SummaryGenerationState.Success) {
                    _generationState.value = SummaryGenerationState.Loading
                }
            }

            try {
                val summary = getSummaryByIdUseCase(summaryId)

                showLoadingJob?.cancel()

                if (summary != null) {
                    _generationState.value = SummaryGenerationState.Success(summary)
                } else {

                    _generationState.value = SummaryGenerationState.Error("Resumen no encontrado")
                }
            } catch (e: Exception) {
                showLoadingJob?.cancel()
                Log.e("SummaryViewModel", "Error al cargar resumen por ID", e)
                _generationState.value =
                    SummaryGenerationState.Error(e.message ?: "Error desconocido al cargar resumen")
            }
        }
    }

    fun resetState() {
        showLoadingJob?.cancel()
        _generationState.value = SummaryGenerationState.Idle
    }

    fun saveSummaryAsPdf(context: Context, summary: SummaryModel) {
        viewModelScope.launch {
            try {
                val successMessage = withContext(Dispatchers.IO) {
                    generateAndSavePdf(context, summary)
                }
                // Envía evento de éxito a la UI
                _downloadChannel.send(DownloadEvent.Success(successMessage))
                Log.i("SummaryViewModel", successMessage)

            } catch (e: Exception) {
                Log.e("SummaryViewModel", "Error saving PDF", e)
                // Envía evento de error a la UI
                _downloadChannel.send(DownloadEvent.Error(e.message ?: "Error al guardar el PDF"))
            }
        }
    }

    private fun generateAndSavePdf(context: Context, summary: SummaryModel): String {
        val pdfDocument = PdfDocument()
        val pageHeight = 1120 // A4 Height approx.
        val pageWidth = 792  // A4 Width approx.
        val margin = 72f
        val contentWidth = pageWidth - 2 * margin.toInt()
        val contentHeight = pageHeight - 2 * margin.toInt() // Usable height per page

        val titlePaint = TextPaint().apply {
            color = Color.BLACK
            textSize = 24f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        val textPaint = TextPaint().apply {
            color = Color.BLACK
            textSize = 12f
        }

        var pageNumber = 1
        var currentPage: PdfDocument.Page? = null
        var currentCanvas: Canvas? = null
        var currentY = margin // Start Y position below top margin

        fun startNewPage() {
            currentPage?.let { pdfDocument.finishPage(it) } // Finish previous page if exists
            val pageInfo =
                PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
            currentPage = pdfDocument.startPage(pageInfo)
            currentCanvas = currentPage!!.canvas
            currentY = margin // Reset Y for new page
        }

        // --- Start Drawing ---
        startNewPage() // Start the first page

        // Draw Title on the first page
        currentCanvas!!.drawText(summary.title, (pageWidth / 2).toFloat(), currentY, titlePaint)
        currentY += 40f // Space after title

        // Split summary into lines or paragraphs to draw sequentially
        val lines = summary.generatedSummary.lines()

        lines.forEach { line ->
            // Create a StaticLayout for the current line/paragraph
            val lineLayout = StaticLayout.Builder.obtain(
                line.trim(), 0, line.trim().length, textPaint, contentWidth
            )
                // Use START alignment for regular text
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .build()

            val lineHeight = lineLayout.height

            // Check if this line fits on the current page
            if (currentY + lineHeight > pageHeight - margin) {
                // Doesn't fit, start a new page
                startNewPage()
            }

            // Draw the line on the current page
            currentCanvas!!.withSave {
                translate(margin, currentY)
                lineLayout.draw(this)
            }
            currentY += lineHeight // Move Y position down
        }

        // Finish the last page
        currentPage?.let { pdfDocument.finishPage(it) }

        // --- Saving Logic (MediaStore) ---
        // (The MediaStore saving code remains the same as before)
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            val safeTitle = summary.title.replace(Regex("[^a-zA-Z0-9.-]"), "_").take(50)
            put(MediaStore.MediaColumns.DISPLAY_NAME, "${safeTitle}_Summary.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: throw Exception("Failed to create MediaStore entry")

        try {
            resolver.openOutputStream(uri).use { outputStream ->
                if (outputStream == null) throw Exception("Failed to open output stream")
                pdfDocument.writeTo(outputStream as FileOutputStream)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
            pdfDocument.close()
            return "Resumen guardado en Descargas"

        } catch (e: Exception) {
            try {
                resolver.delete(uri, null, null)
            } catch (deleteEx: Exception) { /* Ignore */
            }
            pdfDocument.close()
            throw e
        }
    }


    override fun onCleared() {
        super.onCleared()
        showLoadingJob?.cancel()
    }
}

sealed interface SummaryGenerationState {
    data class Success(val newSummary: SummaryModel) : SummaryGenerationState
    data class Error(val error: String) : SummaryGenerationState
    object Loading : SummaryGenerationState
    object Idle : SummaryGenerationState
}

sealed interface DownloadEvent {
    data class Success(val message: String) : DownloadEvent
    data class Error(val error: String) : DownloadEvent
}