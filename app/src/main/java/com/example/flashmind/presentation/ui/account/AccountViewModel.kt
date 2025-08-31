package com.example.flashmind.presentation.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.AuthResponse
import com.example.flashmind.domain.usecase.auth.SignOutGoogleUseCase
import com.example.flashmind.domain.usecase.preference.GetDarkModeUseCase
import com.example.flashmind.domain.usecase.preference.SaveDarkModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel  @Inject constructor(
    private val saveDarkModeUseCase: SaveDarkModeUseCase,
    private val signOutGoogleUseCase: SignOutGoogleUseCase,
    private val getDarkModeUseCase: GetDarkModeUseCase
) : ViewModel() {


    private val _signOutState = MutableStateFlow<AuthResponse>(AuthResponse.Init)
    val signOutState: StateFlow<AuthResponse> = _signOutState.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean>(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

init {
    collectDarkMode()
}
    fun signOutWithGoogle() {
        viewModelScope.launch {
            try {
                signOutGoogleUseCase().collect { response ->
                    _signOutState.value = response
                }
            } catch (e: Exception) {
                _signOutState.value = AuthResponse.Error(e.message ?: "Error al cerrar sesión.")
            }
        }
    }


    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            saveDarkModeUseCase(enabled)
        }
    }

    fun collectDarkMode() {
        viewModelScope.launch {
            getDarkModeUseCase().collect {
                _isDarkMode.value = it
            }
        }
    }
}