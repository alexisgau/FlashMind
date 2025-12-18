package com.alexisgau.synapai.presentation.ui.account

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexisgau.synapai.domain.model.AuthResponse
import com.alexisgau.synapai.domain.reposotory.AuthRepository
import com.alexisgau.synapai.domain.usecase.auth.SignOutUseCase
import com.alexisgau.synapai.domain.usecase.auth.UpdateProfilePictureUseCase
import com.alexisgau.synapai.domain.usecase.preference.GetDarkModeUseCase
import com.alexisgau.synapai.domain.usecase.preference.SaveDarkModeUseCase
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
    private val getDarkModeUseCase: GetDarkModeUseCase,
    private val updateProfilePictureUseCase: UpdateProfilePictureUseCase,
    private val authClient: AuthRepository,
) : ViewModel() {

    val isUserAnonymous: Boolean
        get() = authClient.getCurrentUser()?.isAnonymous == true
    private val _signOutState = MutableStateFlow<AuthResponse>(AuthResponse.Init)
    val signOutState: StateFlow<AuthResponse> = _signOutState.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _currentImageUrl = MutableStateFlow("")
    val currentImageUrl: StateFlow<String> = _currentImageUrl.asStateFlow()

    private val _isUploadingPhoto = MutableStateFlow(false)
    val isUploadingPhoto: StateFlow<Boolean> = _isUploadingPhoto.asStateFlow()

    private val _deleteAccountState = MutableStateFlow<Result<Unit>?>(null)
    val deleteAccountState = _deleteAccountState.asStateFlow()

    init {
        val user = authClient.getCurrentUser()
        _currentImageUrl.value = user?.photoUrl?.toString().orEmpty()
        collectDarkMode()
    }

    fun updateProfilePicture(uri: Uri) {
        viewModelScope.launch {
            _isUploadingPhoto.value = true
            updateProfilePictureUseCase(uri)
                .onSuccess { downloadUrl ->
                    _currentImageUrl.value = downloadUrl
                    _isUploadingPhoto.value = false
                }
                .onFailure {
                    _isUploadingPhoto.value = false
                }
        }
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

    fun deleteAccount() {
        viewModelScope.launch {
            _deleteAccountState.value = null
            val result = authClient.deleteAccount()
            _deleteAccountState.value = result
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
