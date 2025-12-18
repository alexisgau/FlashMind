package com.alexisgau.synapai.presentation.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.alexisgau.synapai.presentation.ui.login.LoginScreen
import com.alexisgau.synapai.presentation.ui.register.RegisterScreen

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
                    navController.navigate(Register)
                }
            )
        }

        //Register
        composable<Register> {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Graph.MAIN) {
                        popUpTo(Graph.AUTH) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                navigateBackToLogin = {
                    navController.popBackStack()
                })
        }
    }
}