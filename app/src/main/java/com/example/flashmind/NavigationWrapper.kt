package com.example.flashmind

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.flashmind.presentation.ui.addcategory.AddCategoryScreen
import com.example.flashmind.presentation.ui.addflashcard.AddFlashCardScreen
import com.example.flashmind.presentation.ui.addlesson.AddLessonScreen
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
                navigateToAddFlashCard = { navController.navigate(AddFlashCards(lessonId = it))}, navigateToStartGame = {navController.navigate(
                    StartLesson(lessonId = it))})
        }

        composable<AddFlashCards> {

            val addFlashCard: AddFlashCards = it.toRoute()
            AddFlashCardScreen(addFlashCard.lessonId, navigateToFlashCards = {
                navController.navigate(
                    FlashCards
                )
            })


        }
        composable<StartLesson> {

            val startGame: StartLesson = it.toRoute()
            StartLessonScreen(startGame.lessonId)

        }
    }


}