package com.example.flashmind.presentation.ui.main
import androidx.compose.runtime.getValue
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import com.example.flashmind.presentation.ui.navigation.AppNavHost
import com.example.flashmind.ui.theme.FlashMindTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()

            FlashMindTheme(darkTheme = isDarkMode) {

                if (!isLoading) {
                    val isAuthenticated by viewModel.isAuthenticated.collectAsState()
                    AppNavHost(isAuthenticated = isAuthenticated)
                }
            }
        }
    }
}



