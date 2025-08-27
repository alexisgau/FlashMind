package com.example.flashmind.domain.model

data class Lesson(
    val id: Int,
    val tittle: String,
    val categoryId: Int,
    val userId: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false

)
