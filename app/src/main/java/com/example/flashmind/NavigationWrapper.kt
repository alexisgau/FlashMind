package com.example.flashmind

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.flashmind.presentation.ui.login.LoginScreen
import com.example.flashmind.presentation.ui.category.AddCategoryScreen
import com.example.flashmind.presentation.ui.flashcard.AddFlashCardScreenAi
import com.example.flashmind.presentation.ui.flashcard.AddFlashCardsManualScreen
import com.example.flashmind.presentation.ui.lesson.AddLessonScreen
import com.example.flashmind.presentation.ui.flashcard.EditFlashCardScreen
import com.example.flashmind.presentation.ui.flashcard.FlashCardScreen
import com.example.flashmind.presentation.ui.home.HomeScreen
import com.example.flashmind.presentation.ui.lesson.StartLessonScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = Login) {

        composable<Login> {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Home) }
            )
        }

        composable<Home> {
            HomeScreen(
                navigateToAddCategory = { navController.navigate(AddCategory) },
                navigateToAddLesson = { navController.navigate(AddLesson(it)) },
                navigateToFlashCard = { navController.navigate(FlashCards(it)) }
            )
        }

        composable<AddCategory> {
            AddCategoryScreen(
                navigateToHome = { navController.navigate(Home) }
            )
        }

        composable<AddLesson> {
            val args = it.toRoute<AddLesson>()
            AddLessonScreen(
                categoryId = args.categoryId,
                navigateToHome = { navController.navigate(Home) }
            )
        }

        composable<FlashCards> {
            val args = it.toRoute<FlashCards>()
            FlashCardScreen(
                lessonId = args.lessonId,
                navigateToHome = { navController.navigate(Home) },
                navigateToAddFlashCardAi = { navController.navigate(AddFlashCardsAi(it)) },
                navigateToStartGame = { navController.navigate(StartLesson(it)) },
                navigateToEditFlashCard = { navController.navigate(EditFlashCard(it)) },
                navigateToAddFlashCardManual = { navController.navigate(AddFlashCardsManual(it)) }
            )
        }

        composable<AddFlashCardsAi> {
            val args = it.toRoute<AddFlashCardsAi>()
            AddFlashCardScreenAi(
                lessonId = args.lessonId,
                navigateToFlashCards = { navController.navigate(FlashCards(it)) },
                navigateToEditFlashCard = { navController.navigate(EditFlashCard(it)) }
            )
        }

        composable<AddFlashCardsManual> {
            val args = it.toRoute<AddFlashCardsManual>()
            AddFlashCardsManualScreen(
                lessonId = args.lessonId,
                navigateToFlashCards = { navController.navigate(FlashCards(it)) }
            )
        }

        composable<StartLesson> {
            val args = it.toRoute<StartLesson>()
            StartLessonScreen(
                lessonId = args.lessonId,
                navigateToFlashCardScreen = { navController.navigate(FlashCards(it)) }
            )
        }

        composable<EditFlashCard> {
            val args = it.toRoute<EditFlashCard>()
            EditFlashCardScreen(
                flashCardId = args.flashcardId,
                navigateToFlashCardsScreen = { navController.navigate(Home) }
            )
        }
    }
}
