package com.example.flashmind.presentation.ui.register

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flashmind.R
import com.example.flashmind.domain.reposotory.AuthRepository
import com.example.flashmind.domain.usecase.auth.Register
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: Register,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var confirmPassword by mutableStateOf("")
        private set

    var emailError by mutableStateOf<Int?>(null)
        private set
    var passwordError by mutableStateOf<Int?>(null)
        private set
    var confirmPasswordError by mutableStateOf<Int?>(null)
        private set

    fun onEmailChange(newValue: String) {
        email = newValue
        if (emailError != null) emailError = null
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
        if (passwordError != null) passwordError = null
    }

    fun onConfirmPasswordChange(newValue: String) {
        confirmPassword = newValue
        if (confirmPasswordError != null) confirmPasswordError = null
    }

    fun register() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            // lógica de Invitado vs Nuevo Usuario
            val isAnonymous = authRepository.isUserAnonymous()

            val result = if (isAnonymous) {
                // Si es invitado, VINCULAMOS para no perder datos
                authRepository.upgradeAnonymousAccount(email.trim(), password.trim())
            } else {
                // Si es nuevo total, CREAMOS desde cero
                registerUseCase(email.trim(), password.trim())
            }

            _uiState.value = result.fold(
                onSuccess = { RegisterUiState.Success },
                onFailure = {
                    val errorMsg = it.message ?: "Error al registrarse"
                    RegisterUiState.Error(errorMsg)
                }
            )
        }
    }


    private fun validateInputs(): Boolean {
        var isValid = true

        // Validar Email
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
            emailError = R.string.auth_error_invalid_email
            isValid = false
        }

        // Validar Contraseña (Mínimo 6 chars)
        if (password.length < 6) {
            passwordError = R.string.auth_error_password_too_short
            isValid = false
        }

        // Validar Coincidencia
        if (password != confirmPassword) {
            confirmPasswordError = R.string.auth_error_password_mismatch
            isValid = false
        }

        return isValid
    }
}


sealed interface RegisterUiState {
    data object Idle : RegisterUiState
    data object Loading : RegisterUiState
    data object Success : RegisterUiState
    data class Error(val message: String) : RegisterUiState
}