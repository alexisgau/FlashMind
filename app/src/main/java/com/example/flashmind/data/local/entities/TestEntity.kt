package com.example.flashmind.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "tests",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TestEntity(
    @PrimaryKey(autoGenerate = true)
    val testId: Int = 0,
    val lessonId: Int = 0,
    val title: String,
    val creationDate: Long,
    val userId: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)