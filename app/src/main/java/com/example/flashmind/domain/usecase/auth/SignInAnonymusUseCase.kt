package com.example.flashmind.domain.usecase.auth

import com.example.flashmind.domain.reposotory.AuthRepository
import javax.inject.Inject

class SignInAnonymouslyUseCase @Inject constructor(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): Result<String> {
        return repository.signInAnonymously()
    }
}