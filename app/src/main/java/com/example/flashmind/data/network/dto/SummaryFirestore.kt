package com.example.flashmind.data.network.dto


data class SummaryFirestore(
    val summaryId: Int = 0,
    val lessonId: Int = 0,
    val generatedSummary: String = "",
    val title: String = "",
    val userId: String = "",
    val creationDate: Long = 0L
)