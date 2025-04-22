package com.example.flashmind.data.network


import com.example.flashmind.data.local.entities.FlashCardEntity
import com.google.ai.client.generativeai.BuildConfig
import com.google.ai.client.generativeai.GenerativeModel
import javax.inject.Inject

class GeminiDataSource @Inject constructor() {

    private val model = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AIzaSyBtlIkRofL3TfCe5dJwso3t-ljoLHkQIqc"
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

        val regex = Regex("""Pregunta:\s*(.*?)\s*Respuesta:\s*(.*?)(?=(\nFlashcard|\Z))""", RegexOption.DOT_MATCHES_ALL)

        val matches = regex.findAll(input)

        for (match in matches) {
            val question = match.groupValues[1].trim()
            val answer = match.groupValues[2].trim()

            flashcards.add(
                FlashCardEntity(
                    id = 0,
                    question = question,
                    answer = answer,
                    color = "#F3F3F3",
                    lessonId = lessonId
                )
            )
        }

        return flashcards
    }


}