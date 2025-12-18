package com.alexisgau.synapai.domain.usecase.auth

import com.alexisgau.synapai.domain.reposotory.AuthRepository
import javax.inject.Inject

class SignIn @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(
        email: String,
        password: String,
    ): Result<String> {

        return authRepository.login(email, password)
    }
}