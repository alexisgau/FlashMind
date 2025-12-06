package com.example.flashmind.data.network.dto

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class CategoryFirestore(
    val id: Int = 0,
    val name: String = "",
    val description: String = "",
    val color: String = "",
    @ServerTimestamp
    val timestamp: Date? = null,
)
