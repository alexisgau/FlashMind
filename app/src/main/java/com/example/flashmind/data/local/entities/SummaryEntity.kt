package com.example.flashmind.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "summaries",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("lessonId"), Index("userId")]
)
data class SummaryEntity(
    @PrimaryKey(autoGenerate = true)
    val summaryId: Int = 0,
    val lessonId: Int,
    val originalText: String = "",
    val generatedSummary: String,
    val title: String,
    val creationDate: Long,


    val userId: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)