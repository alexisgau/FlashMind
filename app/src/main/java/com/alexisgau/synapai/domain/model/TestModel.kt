package com.alexisgau.synapai.domain.model

import com.alexisgau.synapai.data.local.entities.TestEntity

data class TestModel(
    val testId: Int,
    val lessonId: Int,
    val title: String,
    val creationDate: Long,
)

fun TestEntity.toDomain(): TestModel {
    return TestModel(
        testId = this.testId,
        lessonId = this.lessonId,
        title = this.title,
        creationDate = this.creationDate
    )
}
