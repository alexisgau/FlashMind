package com.example.flashmind.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.usecase.auth.GetCurrentUserUseCase
import com.example.flashmind.domain.usecase.preference.GetDarkModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {


    val isDarkMode = getDarkModeUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()


    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    init {

        viewModelScope.launch {
            _isAuthenticated.value = getCurrentUserUseCase() != null
            _isLoading.value = false
        }
    }
}