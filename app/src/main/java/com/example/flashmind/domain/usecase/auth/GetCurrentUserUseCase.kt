package com.example.flashmind.domain.usecase.auth

import com.example.flashmind.domain.reposotory.AuthRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(private val authRepository: AuthRepository) {

    operator fun invoke(): FirebaseUser? {
        return authRepository.getCurrentUser()
    }
}