package com.example.flashmind.presentation.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.flashmind.presentation.ui.login.LoginScreen
import com.example.flashmind.presentation.ui.register.RegisterScreen

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = "login",
        route = Graph.AUTH
    ) {
        // Login
        composable<Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Graph.MAIN) {
                        popUpTo(Graph.AUTH) {
                            inclusive = true
                        }

                        launchSingleTop = true
                    }
                },
                navigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        //Register
        composable<Register> {
            RegisterScreen(
                onRegisterSuccess = { navController.popBackStack() },
                navigateBackToLogin = {
                    navController.popBackStack()
                })
        }
    }
}