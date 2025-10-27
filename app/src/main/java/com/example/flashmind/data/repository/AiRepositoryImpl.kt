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
            Extraé del siguiente texto una lista de preguntas y respuestas en forma de flashcards. Cada flashcard debe tener una pregunta clara y su respectiva respuesta corta pero completa y en ingles. Formato:

            Flashcard 1:
            Pregunta: ¿...?
            Respuesta: ...

            Flashcard 2:
            Pregunta: ¿...?
            Respuesta: ...

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

Devuelve tu respuesta **únicamente** en formato JSON, sin texto introductorio ni comentarios.

El formato JSON debe ser una lista de objetos. Cada objeto debe tener esta estructura exacta:
1.  "pregunta": El texto de la pregunta.
2.  "opciones": Una lista de 4 strings con las posibles respuestas.
3.  "respuesta_correcta": El índice (un número del 0 al 3) de la respuesta correcta en la lista de opciones.

Ejemplo de formato de salida:
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
           Eres un asistente experto en sintetizar información para facilitar el estudio. A partir del siguiente texto, crea un resumen **detallado pero conciso**, bien estructurado, ideal para repasar los conceptos clave a fondo.

**Instrucciones:**

1.  Identifica las **ideas principales, secundarias y detalles de soporte importantes**.
2.  **Estructura el resumen usando formato Markdown:**
    * Usa encabezados de nivel 2 (`##`) para los temas principales.
    * Usa encabezados de nivel 3 (`###`) para subtemas importantes (si aplica).
    * Usa viñetas (`*` o `-`) para los detalles clave y sub-puntos. Usa sangría para anidar viñetas si es necesario.
    * Usa negritas (`**`) para resaltar términos o conceptos **muy importantes**.
3.  Usa un lenguaje claro y directo.
4.  Prioriza la información esencial, conservando detalles explicativos.
5.  Asegúrate de que el resumen sea preciso.
6.  **Devuelve únicamente el resumen formateado en Markdown**, sin ningún texto introductorio, saludo o comentario antes o después.

            ---
            **TEXTO DE ENTRADA:**
            $text
            ---
        """.trimIndent()

        // Llama a la nueva función del DataSource
        var response = geminiDataSource.getSummaryFromText(prompt)

        // Podrías añadir alguna limpieza extra si Gemini a veces añade Markdown
         response = response.trim().removePrefix("```markdown").removeSuffix("```").trim()

        if (response.isBlank()) {
            Log.e("AiRepository", "La respuesta de Gemini para el resumen fue nula o vacía.")
            return ""
        }

        return response
    }
}



