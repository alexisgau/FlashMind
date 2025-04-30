package com.example.flashmind.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.AuthResponse
import com.example.flashmind.domain.usecase.GetCurrentUserUseCase
import com.example.flashmind.domain.usecase.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthResponse>(AuthResponse.Init)
    val authState: StateFlow<AuthResponse> = _authState.asStateFlow()

    init {
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        val currentUser = getCurrentUserUseCase()
        _authState.value = if (currentUser != null) {
            AuthResponse.Success
        } else {
            AuthResponse.Init
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
}

