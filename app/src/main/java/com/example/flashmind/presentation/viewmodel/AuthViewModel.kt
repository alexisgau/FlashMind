package com.example.flashmind.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.AuthResponse
import com.example.flashmind.domain.usecase.GetCurrentUserUseCase
import com.example.flashmind.domain.usecase.GetDarkModeUseCase
import com.example.flashmind.domain.usecase.Register
import com.example.flashmind.domain.usecase.SaveDarkModeUseCase
import com.example.flashmind.domain.usecase.SignIn
import com.example.flashmind.domain.usecase.SignInWithGoogleUseCase
import com.example.flashmind.domain.usecase.SignOutGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signIn: SignIn,
    private val register: Register,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutGoogleUseCase: SignOutGoogleUseCase,
    private val saveDarkModeUseCase: SaveDarkModeUseCase,
    private val getDarkModeUseCase: GetDarkModeUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResponse>(AuthResponse.Init)
    val authState: StateFlow<AuthResponse> = _authState.asStateFlow()

    private val _signOutState = MutableStateFlow<AuthResponse>(AuthResponse.Init)
    val signOutState: StateFlow<AuthResponse> = _signOutState.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean>(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    init {
        checkCurrentUser()
        collectDarkMode()
    }

    private fun checkCurrentUser() {

        val currentUser = getCurrentUserUseCase()
        _authState.value = if (currentUser != null) {
            AuthResponse.Success
        } else {
            AuthResponse.Init
        }
    }

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            val result = signIn(email, password)
            _authState.value = result.fold(
                onSuccess = { AuthResponse.Success },
                onFailure = { AuthResponse.Error(it.message ?: "Error al iniciar sesión") }
            )
        }
    }


    fun registerWithEmail(email: String, password: String) {
        viewModelScope.launch {
            val result = register(email, password)
            _authState.value = result.fold(
                onSuccess = { AuthResponse.Success },
                onFailure = { AuthResponse.Error(it.message ?: "Error al registrarse") }
            )
        }
    }



    fun signInWithGoogle() {
        viewModelScope.launch {
            try {
                signInWithGoogleUseCase()
                    .collect { response ->
                        _authState.value = response
                    }
            } catch (e: Exception) {
                _authState.value = AuthResponse.Error(e.message ?: "Error al iniciar sesión.")
            }
        }
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

