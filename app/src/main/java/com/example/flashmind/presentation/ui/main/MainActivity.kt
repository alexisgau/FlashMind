package com.example.flashmind.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.flashmind.presentation.ui.navigation.AppNavHost
import com.example.flashmind.ui.theme.FlashMindTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val isOnboardingCompleted by viewModel.isOnboardingCompleted.collectAsStateWithLifecycle()

            FlashMindTheme(darkTheme = isDarkMode) {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!isLoading) {
                        val isAuthenticated by viewModel.isAuthenticated.collectAsState()
                        AppNavHost(
                            isAuthenticated = isAuthenticated,
                            isOnboardingCompleted = isOnboardingCompleted,
                            setOnboardingCompleted = viewModel::setOnboardingCompleted
                        )
                    } else {

                    }
                }
            }
        }
    }
}



