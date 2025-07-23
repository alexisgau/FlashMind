package com.example.flashmind.domain.usecase

import com.example.flashmind.domain.reposotory.AuthRepository
import javax.inject.Inject

class SignIn @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<String> {

        return authRepository.login(email, password)
    }
}