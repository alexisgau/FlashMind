package com.example.flashmind.domain.model

import com.example.flashmind.data.local.entities.TestEntity

data class TestModel(
    val testId: Int,
    val lessonId: Int,
    val title: String
)

fun TestEntity.toDomain(): TestModel {
    return TestModel(
        testId = this.testId,
        lessonId = this.lessonId,
        title = this.title
    )
}
