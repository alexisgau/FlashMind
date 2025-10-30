package com.example.flashmind.domain.usecase.auth

import com.example.flashmind.domain.reposotory.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(): Result<Unit> {

        return authRepository.signOut()
    }
}