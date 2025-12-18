package com.alexisgau.synapai.presentation.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.alexisgau.synapai.data.worker.RestoreUserDataWorker
import com.alexisgau.synapai.domain.model.AuthResponse
import com.alexisgau.synapai.domain.usecase.auth.SignIn
import com.alexisgau.synapai.domain.usecase.auth.SignInAnonymouslyUseCase
import com.alexisgau.synapai.domain.usecase.auth.SignInWithGoogleUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alexisgau.synapai.R

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signIn: SignIn,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signInAnonymouslyUseCase: SignInAnonymouslyUseCase,
    private val workManager: WorkManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    var emailError by mutableStateOf<Int?>(null)
        private set
    var passwordError by mutableStateOf<Int?>(null)
        private set

    fun onEmailChange(newValue: String) {
        email = newValue
        if (emailError != null) emailError = null
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
        if (passwordError != null) passwordError = null
    }

    fun loginWithEmail() {
        if (!validateInputs()) return

        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            val result = signIn(email.trim(), password.trim())

            _uiState.value = result.fold(
                onSuccess = {
                    startDataRestoration()
                    LoginUiState.Success
                },
                onFailure = {
                    LoginUiState.Error(it.message ?: "Error al iniciar sesión")
                }
            )
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true

        if (email.isBlank()) {
            emailError = R.string.auth_error_field_required // "Campo requerido"
            isValid = false
        }

        if (password.isBlank()) {
            passwordError = R.string.auth_error_field_required
            isValid = false
        }

        return isValid
    }


    fun signInAnonymously() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            val result = signInAnonymouslyUseCase()
            _uiState.value = result.fold(
                onSuccess = { LoginUiState.Success },
                onFailure = { LoginUiState.Error(it.message ?: "Error de invitado") }
            )
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                signInWithGoogleUseCase().collect { response ->
                    when (response) {
                        is AuthResponse.Success -> {
                            startDataRestoration()
                            _uiState.value = LoginUiState.Success
                        }
                        is AuthResponse.Error -> _uiState.value = LoginUiState.Error(response.message)
                        else -> Unit
                    }
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.message ?: "Error con Google.")
            }
        }
    }

    private fun startDataRestoration() {
        val request = OneTimeWorkRequestBuilder<RestoreUserDataWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .addTag("restore_data_work")
            .build()
        workManager.enqueue(request)
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