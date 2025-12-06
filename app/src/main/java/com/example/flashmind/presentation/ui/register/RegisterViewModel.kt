package com.example.flashmind.presentation.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val register: Register,
    private val authRepository: AuthRepository,
) : ViewModel() {


    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun registerWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            val isAnonymous = authRepository.isUserAnonymous()

            val result = if (isAnonymous) {
                // si es invitado, VINCULAMOS (Mismo UID -> Datos preservados)
                authRepository.upgradeAnonymousAccount(email, password)
            } else {
                // Si es nuevo total, CREAMOS (Nuevo UID)
                register(email, password)
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

}

sealed interface RegisterUiState {
    data object Idle : RegisterUiState
    data object Loading : RegisterUiState
    data object Success : RegisterUiState
    data class Error(val message: String) : RegisterUiState
}