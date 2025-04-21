package com.example.flashmind.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.flashmind.domain.model.Lesson

@Entity(
    tableName = "lessons",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class LessonEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val title: String,
    val categoryId: Int
)

fun Lesson.toEntity(): LessonEntity = LessonEntity(id,tittle,categoryId)
fun LessonEntity.toDomain(): Lesson = Lesson(id, title,categoryId)