package com.example.flashmind

import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object AddCategory

@Serializable
data class AddLesson(val categoryId: Int)

@Serializable
data class FlashCards(val lessonId: Int)

@Serializable
data class AddFlashCardsAi(val lessonId: Int)

@Serializable
data class AddFlashCardsManual(val lessonId: Int)

@Serializable
data class StartLesson(val lessonId: Int)

@Serializable
data class EditFlashCard(val flashcardId: Int)