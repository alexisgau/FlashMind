package com.example.flashmind.data.network


import android.util.Log
import com.example.flashmind.BuildConfig
import com.example.flashmind.data.local.entities.FlashCardEntity
import com.google.ai.client.generativeai.GenerativeModel
import javax.inject.Inject


class GeminiDataSource @Inject constructor() {

    val apiKey = BuildConfig.GEMINI_API_KEY.ifEmpty {
        "AIzaSyBtlIkRofL3TfCe5dJwso3t-ljoLHkQIqc"
    }

    init {
        if (apiKey.contains("Demo") || apiKey.contains("Ejemplo")) {
            Log.w("API_KEY", "Usando clave de demostración con capacidades limitadas")
        }
    }

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = apiKey
    )


    suspend fun getFlashcardsFromText(prompt: String): String {
        return try {
            model.generateContent(prompt).text ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun parseFlashcardsFromText(
        input: String,
        lessonId: Int
    ): List<FlashCardEntity> {
        val flashcards = mutableListOf<FlashCardEntity>()

        val regex = Regex(
            """Pregunta:\s*(.*?)\s*Respuesta:\s*(.*?)(?=(\nFlashcard|\Z))""",
            RegexOption.DOT_MATCHES_ALL
        )

        val matches = regex.findAll(input)
        val colors = listOf("#FF5733", "#33FF57", "#3357FF", "#FF33A1", "#FFC300")

        for (match in matches) {
            val question = match.groupValues[1].trim()
            val answer = match.groupValues[2].trim()

            flashcards.add(
                FlashCardEntity(
                    id = 0,
                    question = question,
                    answer = answer,
                    color = colors.random(),
                    lessonId = lessonId
                )
            )
        }

        return flashcards
    }


}