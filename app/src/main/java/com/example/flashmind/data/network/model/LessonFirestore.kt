package com.example.flashmind.data.network.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class LessonFirestore(
    val id: String = "",
    val categoryId: String = "",
    val title: String = "",
    @ServerTimestamp
    val timestamp: Date? = null
)
