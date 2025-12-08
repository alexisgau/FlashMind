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
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    private val createSummaryUseCase: CreateSummaryUseCase,
    private val generateSummaryUseCase: GenerateSummaryUseCase,
    private val getSummaryByIdUseCase: GetSummaryByIdUseCase,
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
        summaryTitle: String,
    ) {
        if (_generationState.value == SummaryGenerationState.Loading) return
        _generationState.value = SummaryGenerationState.Loading
        viewModelScope.launch {
            try {
                val generatedSummaryText = generateSummaryUseCase(originalText)

                val MIN_SUMMARY_LENGTH = 50
                if (generatedSummaryText.isBlank() || generatedSummaryText.length < MIN_SUMMARY_LENGTH) {
                    throw Exception("La IA no pudo generar un resumen válido a partir de este texto.")
                }


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

    fun loadSummaryById(summaryId: Int?) {
        showLoadingJob?.cancel()


        if (summaryId == null || summaryId <= 0) {
            _generationState.value = SummaryGenerationState.Error("ID de resumen inválido.")
            return
        }

        viewModelScope.launch {
            showLoadingJob = launch {
                delay(150L)
                if (_generationState.value !is SummaryGenerationState.Success) {
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

    fun saveSummaryAsPdf(context: Context, summary: SummaryModel) {
        viewModelScope.launch {
            try {
                val successMessage = withContext(Dispatchers.IO) {
                    generateAndSavePdf(context, summary)
                }
                // Envía evento de éxito a la UI
                _downloadChannel.send(DownloadEvent.Success(successMessage))

            } catch (e: Exception) {
                Log.e("SummaryViewModel", "Error saving PDF", e)
                // Envía evento de error a la UI
                _downloadChannel.send(DownloadEvent.Error(e.message ?: "Error al guardar el PDF"))
            }
        }
    }

    private fun generateAndSavePdf(context: Context, summary: SummaryModel): String {
        val pdfDocument = PdfDocument()
        val pageHeight = 1120
        val pageWidth = 792
        val margin = 72f
        val contentWidth = pageWidth - 2 * margin.toInt()

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
        var currentY = margin

        fun startNewPage() {
            currentPage?.let { pdfDocument.finishPage(it) }
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber++).create()
            currentPage = pdfDocument.startPage(pageInfo)
            currentCanvas = currentPage!!.canvas
            currentY = margin
        }

        // --- 1. DIBUJAR EL PDF (Igual que antes) ---
        startNewPage()
        currentCanvas!!.drawText(summary.title, (pageWidth / 2).toFloat(), currentY, titlePaint)
        currentY += 40f

        val lines = summary.generatedSummary.lines()

        lines.forEach { line ->
            // Nota: StaticLayout.Builder requiere API 23+, como tu minSdk es 24, esto es seguro.
            val lineLayout = StaticLayout.Builder.obtain(
                line.trim(), 0, line.trim().length, textPaint, contentWidth
            )
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .build()

            val lineHeight = lineLayout.height

            if (currentY + lineHeight > pageHeight - margin) {
                startNewPage()
            }

            currentCanvas?.withSave {
                translate(margin, currentY)
                lineLayout.draw(this)
            }
            currentY += lineHeight.toFloat()
        }

        currentPage?.let { pdfDocument.finishPage(it) }


        // --- 2. GUARDAR EL ARCHIVO (Lógica Híbrida) ---

        // Limpiamos el título para que sea un nombre de archivo válido
        val safeTitle = summary.title.replace(Regex("[^a-zA-Z0-9.-]"), "_").take(50)
        val fileName = "${safeTitle}_Summary.pdf"

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // === OPCIÓN A: Android 10 (API 29) en adelante (Moderno) ===
                val resolver = context.contentResolver
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.MediaColumns.IS_PENDING, 1) // Marcar como pendiente
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    ?: throw Exception("No se pudo crear entrada en MediaStore")

                resolver.openOutputStream(uri)?.use { outputStream ->
                    // ERROR CORREGIDO: No casteamos a FileOutputStream, solo usamos writeTo(outputStream)
                    pdfDocument.writeTo(outputStream)
                }

                // Finalizar: Marcar como ya no pendiente
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)

            } else {
                // === OPCIÓN B: Android 7, 8, 9 (Legado) ===
                // Requiere permiso WRITE_EXTERNAL_STORAGE en el Manifest
                val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                // Asegurarnos que la carpeta existe
                if (!downloadsDir.exists()) downloadsDir.mkdirs()

                val file = File(downloadsDir, fileName)

                FileOutputStream(file).use { outputStream ->
                    pdfDocument.writeTo(outputStream)
                }
            }

            pdfDocument.close()
            return "PDF guardado exitosamente"

        } catch (e: Exception) {
            pdfDocument.close()
            e.printStackTrace()
            throw Exception("Error al guardar PDF: ${e.message}")
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