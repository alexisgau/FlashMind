package com.example.flashmind.domain.model

data class FlashCard(
    val id: Int,
    val question: String,
    val color: String,
    val answer: String,
    val lessonId: Int,

)
