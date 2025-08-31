package com.example.flashmind.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.domain.model.AuthResponse
import com.example.flashmind.domain.usecase.auth.SignIn
import com.example.flashmind.domain.usecase.auth.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signIn: SignIn,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {


    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()


    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = signIn(email, password)
            _uiState.value = result.fold(
                onSuccess = { LoginUiState.Success },
                onFailure = { LoginUiState.Error(it.message ?: "Error al iniciar sesión") }
            )
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {

                signInWithGoogleUseCase().collect { response ->
                    when(response) {
                        is AuthResponse.Success -> _uiState.value = LoginUiState.Success
                        is AuthResponse.Error -> _uiState.value = LoginUiState.Error(response.message)
                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Error con Google.")
            }
        }
    }


    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}


sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data object Success : LoginUiState
    data class Error(val message: String) : LoginUiState
}