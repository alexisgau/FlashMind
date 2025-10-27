package com.example.flashmind.presentation.ui.test

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flashmind.R
import com.example.flashmind.presentation.ui.summary.UploadFileButton
import com.tom_roush.pdfbox.android.PDFBoxResourceLoader
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.text.PDFTextStripper
import org.apache.poi.xwpf.extractor.XWPFWordExtractor
import org.apache.poi.xwpf.usermodel.XWPFDocument
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateTestScreen(
    modifier: Modifier = Modifier,
    navigateToTestScreen: (String, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf<String>("") }
    var contentFile by remember { mutableStateOf<String>("") }
    val context = LocalContext.current

    var testTitle by remember { mutableStateOf("") }


    // Initialize PDFBox
    LaunchedEffect(Unit) {
        if (!PDFBoxResourceLoader.isReady()) {
            PDFBoxResourceLoader.init(context)
        }
    }

    LaunchedEffect(selectedFileUri) {

        if (selectedFileUri != null) {

            fileName = getFileNameFromUri(context, selectedFileUri!!).orEmpty()
        } else {

            fileName = ""
            contentFile = ""
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri: Uri? ->
            if (uri != null) {
                selectedFileUri = uri
                try {
                    val textFile = extractTextFromUri(selectedFileUri!!, context)
                    contentFile = textFile
                    Log.i("TestScreen", "Texto extraído: $textFile")
                } catch (e: Exception) {
                    Log.e("TestScreen", "Error al extraer texto", e)
                }
            } else {
                Log.i("TestScreen", "El usuario canceló la selección de archivo")
            }
        }
    )

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    "Generar Test",
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = { onNavigateBack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "back")
                }
            }

        )
    }) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                // Botón circular "Cargar Archivo"
                UploadFileButton(
                    modifier = Modifier.size(130.dp),
                    onClick = {
                        filePickerLauncher.launch(
                            arrayOf(
                                "application/pdf",
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                "text/plain"
                            )
                        )
                    }
                )

                Spacer(modifier = Modifier.width(24.dp))

                // Columna para texto "PDF" "DOCX"
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Caja para PDF
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "PDF",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    // Caja para DOCX
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = "DOCX",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Campo de texto para el Título
            OutlinedTextField(
                value = testTitle,
                onValueChange = { testTitle = it },
                label = { Text("Título del test") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón "Generar Test"
            Button(
                // Habilitado solo si hay un archivo cargado
                enabled = fileName.isNotBlank() && testTitle.isNotBlank(),
                onClick = { navigateToTestScreen(contentFile, testTitle) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Generate test", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))


            if (fileName.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.small,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.draft),
                            contentDescription = "File Icon",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = fileName,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        IconButton(
                            onClick = {
                                selectedFileUri = null
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Eliminar archivo",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}


fun extractTextFromUri(uri: Uri, context: Context): String {
    val mimeType = context.contentResolver.getType(uri)


    context.contentResolver.openInputStream(uri).use { inputStream ->
        if (inputStream == null) throw Exception("No se pudo abrir el archivo")

        return when (mimeType) {
            "application/pdf" -> {
                var pdDocument: PDDocument? = null
                try {
                    // Carga el documento desde el stream
                    pdDocument = PDDocument.load(inputStream)
                    val stripper = PDFTextStripper()
                    stripper.getText(pdDocument) // Devuelve el texto
                } finally {
                    // Asegúrate de cerrar el documento SIEMPRE
                    pdDocument?.close()
                }
            }

            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
                extractTextFromDocx(inputStream)

            "text/plain" ->
                extractTextFromTxt(inputStream)

            else ->
                throw Exception("Formato no soportado: $mimeType")
        }
    }
}


fun extractTextFromTxt(inputStream: InputStream): String {
    return inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
}

fun extractTextFromDocx(inputStream: InputStream?): String {

    XWPFDocument(inputStream).use { doc ->
        val extractor = XWPFWordExtractor(doc)
        return extractor.text
    }
}


fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var name: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = it.getString(nameIndex)
                }
            }
        }
    }

    if (name == null) {
        // Si no tiene DISPLAY_NAME, obtenelo desde la ruta
        name = uri.path
        val cut = name?.lastIndexOf('/')
        if (cut != -1 && cut != null) {
            name = name?.substring(cut + 1)
        }
    }
    return name
}
