package com.example.flashmind.domain.usecase.auth

import com.example.flashmind.domain.reposotory.AuthRepository
import javax.inject.Inject

class Register @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<String> {

        return authRepository.register(email, password)
    }
}
