package com.example.flashmind

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.flashmind.domain.model.AuthResponse
import com.example.flashmind.presentation.ui.account.AccountSettingsScreen
import com.example.flashmind.presentation.ui.category.AddCategoryScreen
import com.example.flashmind.presentation.ui.flashcard.AddFlashCardScreenAi
import com.example.flashmind.presentation.ui.flashcard.AddFlashCardsManualScreen
import com.example.flashmind.presentation.ui.flashcard.EditFlashCardScreen
import com.example.flashmind.presentation.ui.flashcard.FlashCardScreen
import com.example.flashmind.presentation.ui.home.HomeScreen
import com.example.flashmind.presentation.ui.lesson.AddLessonScreen
import com.example.flashmind.presentation.ui.lesson.LessonScreen
import com.example.flashmind.presentation.ui.lesson.StartLessonScreen
import com.example.flashmind.presentation.ui.login.LoginScreen
import com.example.flashmind.presentation.ui.register.RegisterScreen
import com.example.flashmind.presentation.ui.splash.SplashScreen
import com.example.flashmind.presentation.viewmodel.AuthViewModel
import com.example.flashmind.presentation.viewmodel.HomeViewModel

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
                navigateToRegister = { navController.navigate(Register) }
            )
        }

        //Register
        composable<Register> {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Login) },
                navigateBackToLogin = {
                    navController.navigate(
                        Login
                    )
                })
        }

        // Home
        composable<Home> {
            HomeScreen(
                onNavigateToAddCategory = { navController.navigate(AddCategory) },
                onNavigateToLessons = { categoryId, categoryName ->
                    navController.navigate(Lessons(categoryId, categoryName))
                },
                onNavigateToAccountSettings = { navController.navigate(AccountSettings(it)) }
            )
        }

        composable<Lessons> { backStackEntry ->

            val args = backStackEntry.toRoute<Lessons>()

            LessonScreen(
                categoryId = args.categoryId,
                categoryName = args.categoryName,
                onNavigateToFlashcards = { lessonId -> navController.navigate(FlashCards(lessonId)) },
                onAddLesson = { navController.navigate(AddLesson(args.categoryId)) },
                onNavigateBack = { navController.popBackStack() },
                onEditLesson = {  },

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
                navigateToHome = {
                    navController.navigate(Home) {
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
                navigateToHome = {
                    navController.navigate(Home) {
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
                navigateToHome = {
                    navController.navigate(Home) { //deberia manejar a lessons
                        popUpTo(Home) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack()},
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
                navigateToFlashCards = {
                    navController.navigate(FlashCards(it))},
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
                onNavigateBack = { navController.popBackStack()},

            )
        }
    }
}

