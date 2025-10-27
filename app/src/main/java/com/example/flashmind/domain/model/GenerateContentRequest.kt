package com.example.flashmind.domain.model
import kotlinx.serialization.Serializable

@Serializable
data class GenerateContentRequest(
    val contents: List<Content>
) {
    @Serializable
    data class Content(
        val parts: List<Part>
    )

    @Serializable
    data class Part(
        val text: String
    )
}

@Serializable
data class GenerateContentResponse(
    val candidates: List<Candidate>?
) {
    @Serializable
    data class Candidate(
        val content: Content
    )

    @Serializable
    data class Content(
        val parts: List<Part>
    )

    @Serializable
    data class Part(
        val text: String
    )

    fun getSafeText(): String {
        return candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
    }
}

