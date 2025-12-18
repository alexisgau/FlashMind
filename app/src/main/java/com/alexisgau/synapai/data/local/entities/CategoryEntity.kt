package com.alexisgau.synapai.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alexisgau.synapai.domain.model.Category

@Entity(tableName = "category")
data class CategoryEntity(


    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String,
    val color: String,
    val userId: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,

    )

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id,
    name,
    description,
    color,
    userId = userId,
    isSynced = isSynced,
    isDeleted = isDeleted
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    description = description,
    color = color,
    userId = userId,
    isSynced = isSynced,
    isDeleted = isDeleted
)
