package com.example.flashmind

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.flashmind.presentation.ui.addcategory.AddCategoryScreen
import com.example.flashmind.presentation.ui.addflashcard.AddFlashCardScreenAi
import com.example.flashmind.presentation.ui.addflashcard.AddFlashCardsManualScreen
import com.example.flashmind.presentation.ui.addlesson.AddLessonScreen
import com.example.flashmind.presentation.ui.editflashcard.EditFlashCardScreen
import com.example.flashmind.presentation.ui.flashcard.FlashCardScreen
import com.example.flashmind.presentation.ui.home.HomeScreen
import com.example.flashmind.presentation.ui.startGame.StartLessonScreen

@Composable
fun NavigationWrapper() {

    val navController = rememberNavController()

    NavHost(navController, startDestination = Home) {

        composable<Home> {
            HomeScreen(
                navigateToAddCategory = { navController.navigate(AddCategory) },
                navigateToAddLesson = {
                    navController.navigate(
                        AddLesson(categoryId = it)
                    )
                },
                navigateToFlashCard = { navController.navigate(FlashCards(lessonId = it)) })
        }

        composable<AddCategory> {
            AddCategoryScreen(navigateToHome = { navController.navigate(Home) })
        }

        composable<AddLesson> {

            val addLesson: AddLesson = it.toRoute()
            AddLessonScreen(addLesson.categoryId, navigateToHome = { navController.navigate(Home) })
        }

        composable<FlashCards> {

            val flashCard: FlashCards = it.toRoute()
            FlashCardScreen(
                flashCard.lessonId,
                navigateToHome = { navController.navigate(Home) },
                navigateToAddFlashCardAi = { navController.navigate(AddFlashCardsAi(lessonId = it))}, navigateToStartGame = {navController.navigate(
                    StartLesson(lessonId = it))}, navigateToEditFlashCard = {navController.navigate(
                    EditFlashCard(flashcardId = it))},
                navigateToAddFlashCardManual = {navController.navigate(AddFlashCardsManual(lessonId = it))})
        }

        composable<AddFlashCardsAi> {

            val addFlashCard: AddFlashCardsAi = it.toRoute()
            AddFlashCardScreenAi(addFlashCard.lessonId, navigateToFlashCards = {
                navController.navigate(
                    FlashCards(lessonId = it)
                )
            },
                navigateToEditFlashCard = {navController.navigate(EditFlashCard(flashcardId = it))})
        }

        composable<AddFlashCardsManual> {
            val addFlashCard: AddFlashCardsManual = it.toRoute()
            AddFlashCardsManualScreen(addFlashCard.lessonId, navigateToFlashCards = {navController.navigate(
                FlashCards(lessonId = it))})

        }
        composable<StartLesson> {

            val startGame: StartLesson = it.toRoute()
            StartLessonScreen(startGame.lessonId, navigateToFlashCardScreen = {navController.navigate(
                FlashCards(lessonId = it))})

        }
        composable<EditFlashCard> {

            val edit: EditFlashCard = it.toRoute()
            EditFlashCardScreen(edit.flashcardId, navigateToFlashCardsScreen = {navController.navigate(
                Home)})
        }
    }


}