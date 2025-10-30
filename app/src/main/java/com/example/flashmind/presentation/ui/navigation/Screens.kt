package com.example.flashmind.presentation.ui.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
@SerialName("onboarding")
object Onboarding


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
data class LessonOptions(val lessonId: Int, val lessonName: String)

@Serializable
data class AddLesson(val categoryId: Int)

@Serializable
data class FlashCards(val lessonId: Int, val lessonName: String)

@Serializable
data class AddFlashCardsAi(val lessonId: Int)

@Serializable
data class AddFlashCardsManual(val lessonId: Int)

@Serializable
data class StartLesson(val lessonId: Int)

@Serializable
data class GenerateTest(val lessonId: Int)

@Serializable
data class Quiz(val lessonId: Int,val contentFile: String?, val testId: Int, val testTittle: String?)

@Serializable
data class Test(val lessonId: Int)

@Serializable
data class SummariesRoute(val lessonId: Int, val lessonTittle: String)


@Serializable
data class GenerateSummaryRoute(
    val lessonId: Int
)

@Serializable
data class SummaryDetailRoute(
    val lessonId: Int,
    val summaryId: Int? = null,
    val contentFile:String? = null,
    val summaryTittle:String? = null
)
@Serializable
data class EditFlashCard(val flashcardId: Int)

@Serializable
data class AccountSettings(val userData: String)