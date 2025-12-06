package com.example.flashmind.data.network.dto

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class LessonFirestore(
    val id: Int = 0,
    val categoryId: Int = 0,
    val title: String = "",
    @ServerTimestamp
    val timestamp: Date? = null,
)
