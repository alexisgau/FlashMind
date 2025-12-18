package com.alexisgau.synapai.domain.usecase.auth

import com.alexisgau.synapai.domain.reposotory.AuthRepository
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(name: String) = authRepository.updateUserName(name)
}