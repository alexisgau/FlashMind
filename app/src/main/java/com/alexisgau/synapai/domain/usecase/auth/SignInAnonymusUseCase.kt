package com.alexisgau.synapai.domain.usecase.auth

import com.alexisgau.synapai.domain.reposotory.AuthRepository
import javax.inject.Inject

class SignInAnonymouslyUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): Result<String> {
        return repository.signInAnonymously()
    }
}