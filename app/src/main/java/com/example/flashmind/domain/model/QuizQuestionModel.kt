package com.example.flashmind.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuizQuestionModel(
    @SerialName("pregunta")
    val question: String,
    @SerialName("opciones")
    val options: List<String>,
    @SerialName("respuesta_correcta")
    val correctResponseIndex: Int
)