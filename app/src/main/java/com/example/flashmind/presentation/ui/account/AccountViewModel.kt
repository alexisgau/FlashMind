package com.example.flashmind.presentation.ui.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.AuthResponse
import com.example.flashmind.domain.usecase.auth.SignOutUseCase
import com.example.flashmind.domain.usecase.preference.GetDarkModeUseCase
import com.example.flashmind.domain.usecase.preference.SaveDarkModeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AccountViewModel @Inject constructor(
    private val saveDarkModeUseCase: SaveDarkModeUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getDarkModeUseCase: GetDarkModeUseCase
) : ViewModel() {


    private val _signOutState = MutableStateFlow<AuthResponse>(AuthResponse.Init)
    val signOutState: StateFlow<AuthResponse> = _signOutState.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean>(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        collectDarkMode()
    }


    fun signOut() {
        viewModelScope.launch {

            val result = signOutUseCase()

            result.fold(
                onSuccess = {
                    _signOutState.value = AuthResponse.Success
                },
                onFailure = { exception ->
                    _signOutState.value =
                        AuthResponse.Error(exception.message ?: "Error al cerrar sesión.")
                }
            )
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
