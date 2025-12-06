package com.example.flashmind.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost(
    isAuthenticated: Boolean,
    isOnboardingCompleted: Boolean,
    setOnboardingCompleted: () -> Unit,
    navController: NavHostController = rememberNavController(),
) {
    val startDestination = when {
        !isAuthenticated -> Graph.AUTH
        !isOnboardingCompleted -> Graph.MAIN
        else -> Graph.MAIN
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        authGraph(navController)

        mainGraph(
            navController = navController,
            isOnboardingCompleted = isOnboardingCompleted,
            setOnboardingCompleted = setOnboardingCompleted
        )
    }
}

object Graph {
    const val AUTH = "auth_graph"
    const val MAIN = "main_graph"
}




