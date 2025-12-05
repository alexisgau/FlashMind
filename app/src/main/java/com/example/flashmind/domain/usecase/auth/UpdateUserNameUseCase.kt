package com.example.flashmind.domain.usecase.auth

import com.example.flashmind.domain.reposotory.AuthRepository
import javax.inject.Inject

class UpdateUserNameUseCase @Inject constructor(private val authRepository: AuthRepository) {

    suspend operator fun invoke(name:String) = authRepository.updateUserName(name)
}