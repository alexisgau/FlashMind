package com.example.flashmind.data.repository

import android.util.Log
import com.example.flashmind.data.local.entities.toDomain
import com.example.flashmind.data.network.GeminiDataSource
import com.example.flashmind.data.network.dto.SafetySetting
import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.domain.model.QuizQuestionModel
import com.example.flashmind.domain.reposotory.AiRepository
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val geminiDataSource: GeminiDataSource
) : AiRepository {

    override suspend fun generateFlashcards(text: String, lessonId: Int): List<FlashCard> {
        val prompt = """
            Tu tarea es extraer flashcards (preguntas y respuestas) del "Texto".

            **Instrucciones Cruciales:**
            1.  **Detecta el idioma principal del "Texto" (ej. inglés, español).**
            2.  Genera preguntas y respuestas **ESTRICTAMENTE en ese mismo idioma detectado.**
            3.  **Formato Obligatorio:** Para la estructura, DEBES usar las palabras clave en español "Pregunta:" y "Respuesta:", sin importar el idioma del contenido.

            ---
            **Ejemplo de formato (si el texto fuera en INGLÉS):**

            Flashcard 1:
            Pregunta: What is the main characteristic of OFDM subcarriers?
            Respuesta: They are orthogonal to each other.

            Flashcard 2:
            Pregunta: What does OFDM stand for?
            Respuesta: Orthogonal Frequency Division Modulation.

            ---
            **Ejemplo de formato (si el texto fuera en ESPAÑOL):**

            Flashcard 1:
            Pregunta: ¿Cuál es la característica principal de las subportadoras OFDM?
            Respuesta: Son ortogonales entre sí.

            Flashcard 2:
            Pregunta: ¿Qué significa OFDM?
            Respuesta: Orthogonal Frequency Division Modulation.
            ---

            Texto:
            $text
        """.trimIndent()

        val response = geminiDataSource.getFlashcardsFromText(prompt)
        return geminiDataSource.parseFlashcardsFromText(response, lessonId).map { it.toDomain() }
    }

    override suspend fun generateTest(
        text: String,
        testId: Int
    ): List<QuizQuestionModel> {

        val prompt = """
           Actúa como un tutor experto. Tu objetivo es ayudar a un estudiante a autoevaluarse.

           **Tarea:**
           1.  Lee el "TEXTO DE ENTRADA". Este texto puede estar "sucio", extraído de un PDF (puede incluir encabezados, pies de página, o fórmulas).
           2.  Ignora la "basura" (encabezados, pies de página repetitivos, texto de diagramas) y extrae los conceptos principales.
           3.  Basado **solo en los conceptos principales**, genera preguntas de opción múltiple para ayudar al estudiante a verificar su comprensión.
           4.  **Detecta el idioma** del texto principal (inglés o español) y genera las preguntas y opciones en ESE MISMO IDIOMA.
           5. ** En el caso que detectes un que el texto tiene sus preguntas con sus opciones, usa esa información para generar las preguntas sin ignorar ninguna.

           **Formato de Salida Obligatorio:**
           * Devuelve **únicamente** un JSON, sin texto introductorio.
           * Usa **exactamente** las claves en español: "pregunta", "opciones", "respuesta_correcta".

           Ejemplo de formato (si el texto fuera en español):
           [
             {
               "pregunta": "¿Qué es la criptografía simétrica?",
               "opciones": ["Usa dos claves", "Usa una clave pública", "Usa la misma clave para cifrar y descifrar", "No usa claves"],
               "respuesta_correcta": 2
             }
           ]
           ---
           TEXTO DE ENTRADA:
           $text
           ---
        """.trimIndent()
        val safetySettings = listOf(
            SafetySetting("HARM_CATEGORY_HARASSMENT", "BLOCK_NONE"),
            SafetySetting("HARM_CATEGORY_HATE_SPEECH", "BLOCK_NONE"),
            SafetySetting("HARM_CATEGORY_SEXUALLY_EXPLICIT", "BLOCK_NONE"),
            SafetySetting("HARM_CATEGORY_DANGEROUS_CONTENT", "BLOCK_NONE")
        )

        // 2. Pasa el prompt Y los safetySettings a la función del DataSource
        var response = geminiDataSource.getTestFromText(prompt, safetySettings)

        if (response.isBlank()) {
            Log.e("AiRepository", "La respuesta de Gemini fue nula o vacía.")
            return emptyList()
        }
        response = response.trim().removePrefix("```json").removeSuffix("```").trim()
        return geminiDataSource.parseTestFromJson(response, testId).map { it.toDomain() }
    }


    override suspend fun generateSummary(text: String): String {
        val prompt = """
            Tu tarea es crear un resumen de estudio detallado y bien estructurado a partir del "Texto de Entrada".

            **Instrucciones Cruciales:**
            1.  **Detecta el idioma principal del "Texto de Entrada" (ej. inglés, español).**
            2.  **Escribe todo el resumen EN ESE MISMO IDIOMA.**
            3.  **NO TRADUZCAS** el resumen a otro idioma.
            4.  Estructura el resumen usando formato Markdown (##, ###, *, **).
            5.  Devuelve **únicamente** el resumen formateado, sin ningún texto introductorio o saludos.

            ---
            **Ejemplo (Si el Texto de Entrada está en INGLÉS):**
            ## Key Concepts of OFDM
            * **Definition:** A digital modulation technique that combines multiplexing and modulation for high spectral efficiency.
            * **Principle:** Divides a channel into multiple narrow orthogonal subcarriers.

            ---
            **Ejemplo (Si el Texto de Entrada está en ESPAÑOL):**
            ## Conceptos Clave de OFDM
            * **Definición:** Es una tecnología de modulación digital que combina multiplexación y modulación para lograr una alta eficiencia espectral.
            * **Principio:** Divide un canal en múltiples subportadoras ortogonales estrechas.
            ---

            TEXTO DE ENTRADA:
            $text
            ---
        """.trimIndent()

        var response = geminiDataSource.getSummaryFromText(prompt)

         response = response.trim().removePrefix("```markdown").removeSuffix("```").trim()

        if (response.isBlank()) {
            Log.e("AiRepository", "La respuesta de Gemini para el resumen fue nula o vacía.")
            return ""
        }

        return response
    }
}



