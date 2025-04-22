package com.example.flashmind.data.repository

import com.example.flashmind.data.local.entities.toDomain
import com.example.flashmind.data.network.GeminiDataSource
import com.example.flashmind.domain.model.FlashCard
import com.example.flashmind.domain.reposotory.AiRepository
import javax.inject.Inject

class AiRepositoryImpl @Inject constructor(
    private val geminiDataSource: GeminiDataSource
) : AiRepository {

    override suspend fun generateFlashcards(text: String, lessonId: Int): List<FlashCard> {
        val prompt = """
            Extraé del siguiente texto una lista de preguntas y respuestas en forma de flashcards. Cada flashcard debe tener una pregunta clara y su respectiva respuesta corta pero completa. Formato:

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
}
