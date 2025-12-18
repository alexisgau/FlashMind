package com.alexisgau.synapai.domain.usecase.auth

import com.alexisgau.synapai.domain.model.AuthResponse
import com.alexisgau.synapai.domain.reposotory.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(private val authRepository: AuthRepository) {

    operator fun invoke(): Flow<AuthResponse> {
        return authRepository.signInWithGoogle()
    }


}