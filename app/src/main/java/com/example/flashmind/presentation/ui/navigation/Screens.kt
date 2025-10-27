package com.example.flashmind.presentation.ui.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("login")
object Login

@Serializable
object Register


@Serializable
@SerialName("home")
object Home

@Serializable
object AddCategory


@Serializable
data class Lessons(val categoryId: Int, val categoryName: String)

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
@SerialName("generateTest")
object GenerateTest

@Serializable
data class Quiz(val contentFile: String?, val testId: Int, val testTittle: String?)

@Serializable
@SerialName("test")
object Test

@Serializable
@SerialName("summaries")
 object SummariesRoute


@Serializable
data class GenerateSummaryRoute(
    val lessonId: Int
)

@Serializable
data class SummaryDetailRoute(
    val summaryId: Int? = null,
    val contentFile:String? = null,
    val summaryTittle:String? = null
)
@Serializable
data class EditFlashCard(val flashcardId: Int)

@Serializable
data class AccountSettings(val userData: String)