package com.example.flashmind.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.flashmind.domain.model.QuizQuestionModel

@Entity(
    tableName = "questions",
    foreignKeys = [
        ForeignKey(
            entity = TestEntity::class,
            parentColumns = ["testId"],
            childColumns = ["testId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class MultipleChoiceQuestionEntity(
    @PrimaryKey(autoGenerate = true)
    val questionId: Int = 0,
    val testId: Int,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val userId: String = "",
    val isSynced: Boolean = false,
    val isDeleted: Boolean = false,
)

fun MultipleChoiceQuestionEntity.toDomain(): QuizQuestionModel {

    return QuizQuestionModel(
        question = questionText,
        options = options,
        correctResponseIndex = correctAnswerIndex
    )
}