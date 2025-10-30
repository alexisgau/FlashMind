package com.example.flashmind.data.repository

import android.util.Log
import com.example.flashmind.data.local.entities.toDomain
import com.example.flashmind.data.network.GeminiDataSource
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
           Eres un asistente experto en crear material de estudio. A partir del siguiente texto, genera **tantas preguntas de opción múltiple (multiple choice) como consideres relevantes** basándote en el contenido. La cantidad de preguntas debe ajustarse al tamaño y la densidad de la información del texto.

            **Instrucciones Importantes:**
            1.  Detecta el idioma principal del "Texto de Entrada" (ejemplo: inglés, español, etc.).
            2.  **Genera todas las preguntas y todas las opciones en ese mismo idioma detectado.**
            3.  Devuelve tu respuesta **únicamente** en formato JSON, sin texto introductorio ni comentarios.
            4.  **Usa las claves JSON en español** ("pregunta", "opciones", "respuesta_correcta") tal como se muestra en el ejemplo, sin importar el idioma del contenido.


            Ejemplo de formato de salida (si el texto fuera en español):
            [
              {
                "pregunta": "¿En qué año terminó la Segunda Guerra Mundial?",
                "opciones": [
                  "1942",
                  "1945",
                  "1939",
                  "1950"
                ],
                "respuesta_correcta": 1
              }
            ]

            ---
            TEXTO DE ENTRADA:
            $text
            ---
        """.trimIndent()
        var response = geminiDataSource.getTestFromText(prompt)

        if (response.isBlank()) {
            Log.e("AiRepository", "La respuesta de Gemini fue nula o vacía.")
            return emptyList() // Devuelve una lista vacía en lugar de crashear
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



