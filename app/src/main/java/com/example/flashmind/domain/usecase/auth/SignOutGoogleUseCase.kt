package com.example.flashmind.domain.usecase.auth

import com.example.flashmind.domain.model.AuthResponse
import com.example.flashmind.domain.reposotory.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignOutGoogleUseCase @Inject constructor(private val authRepository: AuthRepository) {

    operator fun invoke(): Flow<AuthResponse> {

        return authRepository.signOutAccountWithGoogle()
    }
}