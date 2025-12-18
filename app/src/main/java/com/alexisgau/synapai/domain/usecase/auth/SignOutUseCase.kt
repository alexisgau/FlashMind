package com.alexisgau.synapai.domain.usecase.auth

import com.alexisgau.synapai.domain.reposotory.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(): Result<Unit> {

        return authRepository.signOut()
    }
}