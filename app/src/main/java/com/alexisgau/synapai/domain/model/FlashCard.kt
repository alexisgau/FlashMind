package com.alexisgau.synapai.domain.model

data class FlashCard(
    val id: Int,
    val question: String,
    val color: String,
    val answer: String,
    val lessonId: Int,
    val userId: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,


    )
