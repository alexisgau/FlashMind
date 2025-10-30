package com.example.flashmind.presentation.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.data.UserPreferencesRepository
import com.example.flashmind.data.network.AuthClient
import com.example.flashmind.domain.usecase.preference.GetDarkModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

// En MainViewModel.kt

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val authClient: AuthClient,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val isDarkMode = getDarkModeUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val isAuthenticated: StateFlow<Boolean> = authClient.getAuthState()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isOnboardingCompleted = userPreferencesRepository.isOnboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        viewModelScope.launch {
            combine(isAuthenticated, isOnboardingCompleted) { auth, onboarding ->
                Log.d("MainViewModel", "Initial state loaded: Auth=$auth, Onboarding=$onboarding")
            }.first()
            _isLoading.value = false
        }
    }

    fun setOnboardingCompleted() {
        viewModelScope.launch {
            userPreferencesRepository.setOnboardingCompleted(true)
        }
    }
}