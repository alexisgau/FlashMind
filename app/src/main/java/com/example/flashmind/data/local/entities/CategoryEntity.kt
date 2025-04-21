package com.example.flashmind.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.flashmind.domain.model.Category

@Entity(tableName = "category")
data class CategoryEntity(


    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val description: String,
    val color: String,

)

fun Category.toEntity(): CategoryEntity = CategoryEntity(id,name,description,color)
fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    description = description,
    color = color
)
