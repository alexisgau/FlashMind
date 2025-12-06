package com.example.flashmind.data.network.dto

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class FlashCardFirestore(

    val id: Int = 0,
    val lessonId: Int = 0,
    val question: String = "",
    val answer: String = "",
    val color: String = "",

    @ServerTimestamp
    val timestamp: Date? = null,
)
