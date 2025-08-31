package com.example.flashmind.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavHost(
    isAuthenticated: Boolean,
    navController: NavHostController = rememberNavController()
) {

    val startDestination = if (isAuthenticated) Graph.MAIN else Graph.AUTH

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        authGraph(navController)

        mainGraph(navController)
    }
}

object Graph {
    const val AUTH = "auth_graph"
    const val MAIN = "main_graph"
}




