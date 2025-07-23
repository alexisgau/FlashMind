package com.example.flashmind

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.flashmind.domain.model.AuthResponse
import com.example.flashmind.presentation.ui.login.LoginScreen
import com.example.flashmind.presentation.ui.category.AddCategoryScreen
import com.example.flashmind.presentation.ui.flashcard.AddFlashCardScreenAi
import com.example.flashmind.presentation.ui.flashcard.AddFlashCardsManualScreen
import com.example.flashmind.presentation.ui.lesson.AddLessonScreen
import com.example.flashmind.presentation.ui.flashcard.EditFlashCardScreen
import com.example.flashmind.presentation.ui.flashcard.FlashCardScreen
import com.example.flashmind.presentation.ui.home.HomeScreen
import com.example.flashmind.presentation.ui.lesson.StartLessonScreen
import com.example.flashmind.presentation.ui.splash.SplashScreen
import com.example.flashmind.presentation.viewmodel.AuthViewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.presentation.ui.account.AccountSettingsScreen
import com.example.flashmind.presentation.ui.register.RegisterScreen

@Composable
fun NavigationWrapper(authViewModel: AuthViewModel = hiltViewModel()) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()
    val navController = rememberNavController()

    // Determina la pantalla inicial solo una vez
    val startDestination = when (authState) {
        is AuthResponse.Success -> Home
        is AuthResponse.Init, is AuthResponse.Error -> Login
        else -> Spash
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash
        composable<Spash> {
            SplashScreen()
        }

        // Login
        composable<Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Home) {
                        popUpTo(Login) { inclusive = true }
                    }
                },
                navigateToRegister = {navController.navigate(Register) }
            )
        }

        //Register
        composable<Register>{
            RegisterScreen(onRegisterSuccess = {navController.navigate(Login)}, navigateBackToLogin = {})
        }

        // Home
        composable<Home> {
            HomeScreen(
                navigateToAddCategory = { navController.navigate(AddCategory) },
                navigateToAddLesson = { navController.navigate(AddLesson(it)) },
                navigateToFlashCard = { navController.navigate(FlashCards(it)) },
                navigateToAccountSettings = { navController.navigate(AccountSettings(it)) }
            )
        }

        // Account Settings
        composable<AccountSettings> {
            val args = it.toRoute<AccountSettings>()
            AccountSettingsScreen(
                navigateToLogin = {
                    navController.navigate(Login) {
                        popUpTo(Home) { inclusive = true }
                    }
                },
                userData = args.userData
            )
        }

        // Add Category
        composable<AddCategory> {
            AddCategoryScreen(
                navigateToHome = { navController.navigate(Home) {
                    popUpTo(Home) { inclusive = true }
                }
                }
            )
        }

        // Add Lesson
        composable<AddLesson> {
            val args = it.toRoute<AddLesson>()
            AddLessonScreen(
                categoryId = args.categoryId,
                navigateToHome = { navController.navigate(Home){
                    popUpTo(Home) { inclusive = true }
                }
                }
            )
        }

        // FlashCard Screen
        composable<FlashCards> {
            val args = it.toRoute<FlashCards>()
            FlashCardScreen(
                lessonId = args.lessonId,
                navigateToHome = { navController.navigate(Home){
                    popUpTo(Home) { inclusive = true }
                } },
                navigateToAddFlashCardAi = { navController.navigate(AddFlashCardsAi(it)) },
                navigateToStartGame = { navController.navigate(StartLesson(it)) },
                navigateToEditFlashCard = { navController.navigate(EditFlashCard(it)) },
                navigateToAddFlashCardManual = { navController.navigate(AddFlashCardsManual(it)) }
            )
        }

        // Add Flashcards AI
        composable<AddFlashCardsAi> {
            val args = it.toRoute<AddFlashCardsAi>()
            AddFlashCardScreenAi(
                lessonId = args.lessonId,
                navigateToFlashCards = { navController.navigate(FlashCards(it)) },
                navigateToEditFlashCard = { navController.navigate(EditFlashCard(it)) }
            )
        }

        // Add Flashcards Manual
        composable<AddFlashCardsManual> {
            val args = it.toRoute<AddFlashCardsManual>()
            AddFlashCardsManualScreen(
                lessonId = args.lessonId,
                navigateToFlashCards = { navController.navigate(FlashCards(it)) }
            )
        }

        // Start Lesson
        composable<StartLesson> {
            val args = it.toRoute<StartLesson>()
            StartLessonScreen(
                lessonId = args.lessonId,
                navigateToFlashCardScreen = { navController.navigate(FlashCards(it)) }
            )
        }

        // Edit Flashcard
        composable<EditFlashCard> {
            val args = it.toRoute<EditFlashCard>()
            EditFlashCardScreen(
                flashCardId = args.flashcardId,
                navigateToFlashCardsScreen = { navController.navigate(FlashCards(it)) }
            )
        }
    }
}

