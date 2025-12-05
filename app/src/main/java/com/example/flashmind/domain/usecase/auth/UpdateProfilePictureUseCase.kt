package com.example.flashmind.domain.usecase.auth

import android.net.Uri
import com.example.flashmind.domain.reposotory.AuthRepository
import javax.inject.Inject

class UpdateProfilePictureUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(uri: Uri): Result<String> {
        return authRepository.updateProfilePicture(uri)
    }
}