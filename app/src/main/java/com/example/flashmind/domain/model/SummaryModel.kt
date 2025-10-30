package com.example.flashmind.domain.model

import com.example.flashmind.data.local.entities.SummaryEntity


data class SummaryModel(
    val summaryId: Int,
    val lessonId: Int,
    val generatedSummary: String,
    val title: String,
    val creationDate: Long
)

fun SummaryEntity.toDomain(): SummaryModel {
    return SummaryModel(
        summaryId = this.summaryId,
        lessonId = this.lessonId,
        generatedSummary = this.generatedSummary,
        title = this.title,
        creationDate = this.creationDate
    )
}


fun SummaryModel.toEntity(userId: String): SummaryEntity {
    return SummaryEntity(
        summaryId = this.summaryId,
        lessonId = this.lessonId,
        generatedSummary = this.generatedSummary,
        title = this.title,
        creationDate = this.creationDate,
        userId = userId,
        isSynced = false,
        isDeleted = false,
    )
}