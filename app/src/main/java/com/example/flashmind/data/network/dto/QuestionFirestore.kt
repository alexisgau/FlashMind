package com.example.flashmind.data.network.dto

data class QuestionFirestore(
    val questionId: Int = 0,
    val testId: Int = 0,
    val questionText: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = -1,
    val userId: String = "",
)