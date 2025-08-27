package com.example.flashmind.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.flashmind.domain.model.FlashCard

@Entity(
    tableName = "flashcards",
    foreignKeys = [
        ForeignKey(
            entity = LessonEntity::class,
            parentColumns = ["id"],
            childColumns = ["lessonId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("lessonId")]
)
data class FlashCardEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val question: String,
    val answer: String,
    val color: String,
    val lessonId: Int,
    val userId: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false
)

fun FlashCard.toEntity(): FlashCardEntity = FlashCardEntity(id = id, question = question, answer = answer, color = color, lessonId = lessonId, userId = userId, isSynced = isSynced,isDeleted = isDeleted)
fun FlashCardEntity.toDomain(): FlashCard = FlashCard(id = id, question = question, answer = answer, color = color, lessonId =  lessonId, userId = userId, isSynced = isSynced,isDeleted = isDeleted)

