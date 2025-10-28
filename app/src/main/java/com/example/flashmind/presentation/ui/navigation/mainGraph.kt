package com.example.flashmind.presentation.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.flashmind.presentation.ui.account.AccountSettingsScreen
import com.example.flashmind.presentation.ui.category.AddCategoryScreen
import com.example.flashmind.presentation.ui.addflashcardai.AddFlashCardScreenAi
import com.example.flashmind.presentation.ui.addflashcardmanual.AddFlashCardsManualScreen
import com.example.flashmind.presentation.ui.editflashcard.EditFlashCardScreen
import com.example.flashmind.presentation.ui.flashcard.FlashCardScreen
import com.example.flashmind.presentation.ui.home.HomeScreen
import com.example.flashmind.presentation.ui.addlesson.AddLessonScreen
import com.example.flashmind.presentation.ui.le.LessonOptionsScreen
import com.example.flashmind.presentation.ui.lessons.LessonScreen
import com.example.flashmind.presentation.ui.startlesson.StartLessonScreen
import com.example.flashmind.presentation.ui.summary.GenerateSummaryScreen
import com.example.flashmind.presentation.ui.summary.SummariesScreen
import com.example.flashmind.presentation.ui.summary.SummaryViewScreen
import com.example.flashmind.presentation.ui.test.GenerateTestScreen
import com.example.flashmind.presentation.ui.test.QuizScreen
import com.example.flashmind.presentation.ui.test.TestScreen

fun NavGraphBuilder.mainGraph(navController: NavHostController) {
    navigation(
        startDestination = "home", //generateTest
        route = Graph.MAIN
    ) {


        composable<Home> {
            HomeScreen(
                onNavigateToAddCategory = { navController.navigate(AddCategory) },

                onNavigateToLessonContent = { clickedLessonId, lessonName ->
                    navController.navigate(LessonOptions(clickedLessonId,lessonName ))
                },
                onNavigateToAccountSettings = { imageUrl ->
                    navController.navigate(AccountSettings(imageUrl))
                },
                onNavigateToAddLesson = {navController.navigate(AddLesson(it))}

            )
        }

        composable<LessonOptions> { backStackEntry ->
            val args = backStackEntry.toRoute<LessonOptions>()

            LessonOptionsScreen(
                lessonId = args.lessonId,
                lessonTitle = args.lessonName,
                onNavigateBack = { navController.popBackStack() },
                onStudyFlashcards = {navController.navigate(FlashCards(args.lessonId))},
                onViewSummary = { navController.navigate(SummariesRoute(args.lessonId, args.lessonName)) },
                onTakeTest = { navController.navigate(Test(args.lessonId)) },

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
                navigateToHome = { navController.navigate(Home) },
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
                navigateToAddFlashCardAi = { navController.navigate(AddFlashCardsAi(it)) },
                navigateToAddFlashCardManual = { navController.navigate(AddFlashCardsManual(it)) },
                navigateToStartGame = { navController.navigate(StartLesson(it)) },
                navigateToEditFlashCard = { navController.navigate(EditFlashCard(it)) },
                navigateToLessons = { navController.popBackStack<LessonOptions>(inclusive = false) }
            )
        }

        // Add Flashcards AI
        composable<AddFlashCardsAi> {
            val args = it.toRoute<AddFlashCardsAi>()
            AddFlashCardScreenAi(
                lessonId = args.lessonId,
                navigateToFlashCards = {
                    navController.navigate(FlashCards(it))
                },
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
                navigateToFlashCardScreen = {
                    navController.navigate(FlashCards(it)) {
                        popUpTo<StartLesson> {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable<GenerateTest> { backStackEntry ->
            val args = backStackEntry.toRoute<GenerateTest>()
            GenerateTestScreen(
                navigateToTestScreen = { contentFile,tittle ->
                    navController.navigate(Quiz(lessonId = args.lessonId ,contentFile = contentFile, testTittle = tittle, testId = 1)) {
                        popUpTo<GenerateTest> { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Quiz> {
            val args = it.toRoute<Quiz>()
            QuizScreen(
                contentFile = args.contentFile,
                testTittle = args.testTittle,
                lessonId = args.lessonId,
                testId = args.testId,
                onClickBack = { navController.popBackStack() })

        }

        composable<Test> {
            val args = it.toRoute<Test>()
            TestScreen(
                lessonId = args.lessonId,
                onNavigateBack = {navController.popBackStack()},
                onClickTest = { testId, testTittle ->
                navController.navigate(
                    Quiz(
                        lessonId = args.lessonId,
                        contentFile = null,
                        testTittle = testTittle,
                        testId = testId
                    )
                )
            }, navigateToNewTest = {
                navController.navigate(
                    GenerateTest(args.lessonId)
                )
            })
        }

        // Edit Flashcard
        composable<EditFlashCard> {
            val args = it.toRoute<EditFlashCard>()
            EditFlashCardScreen(
                flashCardId = args.flashcardId,
                onNavigateBack = { navController.popBackStack() },

                )
        }

        // Ruta para la pantalla de LISTA de Resúmenes
        composable<SummariesRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<SummariesRoute>()
            SummariesScreen(
                lessonId = args.lessonId,
                lessonTitle = args.lessonTittle,
                onSummaryClick = { clickedSummaryId, summaryTitle ->
                    navController.navigate(SummaryDetailRoute(lessonId = args.lessonId,summaryId = clickedSummaryId, summaryTittle = summaryTitle))
                },
                onAddSummaryClick = {
                    navController.navigate(GenerateSummaryRoute(lessonId = args.lessonId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Ruta para la pantalla de GENERACIÓN
        composable<GenerateSummaryRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<GenerateSummaryRoute>()
            GenerateSummaryScreen(
                lessonId = args.lessonId,
                onClickBack = {navController.popBackStack()},
                navigateToSummaryScreen = { contentFile,summaryTitle ->
                    navController.navigate(SummaryDetailRoute(lessonId = args.lessonId,contentFile = contentFile, summaryTittle = summaryTitle ))
                }
            )
        }


        // Ruta para la pantalla de DETALLE del Resumen
        composable<SummaryDetailRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<SummaryDetailRoute>()
            SummaryViewScreen(
                summaryId = args.summaryId,
                contentFile = args.contentFile,
                summaryTittle = args.summaryTittle,
                lessonId = args.lessonId,
                onClickBack = { navController.popBackStack() }
            )
        }
    }
}



