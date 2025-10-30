package com.example.flashmind.data.network


import android.util.Log
import com.example.flashmind.BuildConfig
import com.example.flashmind.data.local.entities.FlashCardEntity
import com.example.flashmind.data.local.entities.MultipleChoiceQuestionEntity
import com.example.flashmind.data.network.api.IaCallService
import com.example.flashmind.data.network.dto.GenerateContentRequest
import com.example.flashmind.domain.model.QuizQuestionModel
import kotlinx.serialization.json.Json
import javax.inject.Inject


class GeminiDataSource @Inject constructor(private val apiService: IaCallService) {

    val apiKey = BuildConfig.GEMINI_API_KEY



    suspend fun getFlashcardsFromText(prompt: String): String {
        val models = apiService.listModels(apiKey)
        Log.i("Models", "$models")
        val requestBody = GenerateContentRequest(
            contents = listOf(
                GenerateContentRequest.Content(
                    parts = listOf(GenerateContentRequest.Part(text = prompt))
                )
            )
        )

        return try {
            val response = apiService.generateContent(apiKey, requestBody)
            response.getSafeText()
        } catch (e: Exception) {
            Log.e("GeminiAPI", "Error en Retrofit (Flashcards): ${e.message}", e)
            try {
                val response = apiService.generateContent(apiKey, requestBody)
                response.getSafeText()
            } catch (e2: Exception) {
                Log.e("GeminiAPI", "Error también con v1.5: ${e2.message}")
                ""
            }
        }
    }

    suspend fun getTestFromText(prompt: String): String {
        val requestBody = GenerateContentRequest(
            contents = listOf(GenerateContentRequest.Content(
                parts = listOf(GenerateContentRequest.Part(text = prompt))
            ))
        )
        return try {
            val response = apiService.generateContent(apiKey, requestBody)
            //devuelve el texto limpio
            response.getSafeText()
        } catch (e: Exception) {
            Log.e("GeminiAPI", "Error en Retrofit (GenerateTest): ${e.message}", e)
            ""
        }
    }


    fun parseTestFromJson(jsonInput: String, testId: Int): List<MultipleChoiceQuestionEntity> {
        val json = Json { ignoreUnknownKeys = true }

        return try {
            val dtoList = json.decodeFromString<List<QuizQuestionModel>>(jsonInput)
            dtoList.map { dto ->
                MultipleChoiceQuestionEntity(
                    testId = testId,
                    questionText = dto.question,
                    options = dto.options,
                    correctAnswerIndex = dto.correctResponseIndex
                )
            }
        } catch (e: kotlinx.serialization.SerializationException) {
            Log.e("ParseTest", "Error al parsear el JSON de Gemini. Mensaje: ${e.message}")
            Log.e("ParseTest", "JSON inválido recibido: $jsonInput")
            emptyList()
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

    suspend fun getSummaryFromText(prompt: String): String {
        val requestBody = GenerateContentRequest(
            contents = listOf(
                GenerateContentRequest.Content(
                    parts = listOf(GenerateContentRequest.Part(text = prompt))
                )
            )
        )
        return try {
            val response = apiService.generateContent(apiKey, requestBody)
            response.getSafeText()
        } catch (e: Exception) {
            Log.e("GeminiAPI", "Error en Retrofit (GenerateSummary): ${e.message}", e)
            ""
        }
    }


}