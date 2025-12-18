package com.alexisgau.synapai.domain.model

data class Category(
    val id: Int,
    val name: String,
    val description: String,
    val color: String,
    val userId: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
)
