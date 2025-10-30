package com.example.flashmind.data.repository

import com.example.flashmind.data.network.AuthClient
import com.example.flashmind.domain.model.AuthResponse
import com.example.flashmind.domain.reposotory.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authClient: AuthClient) : AuthRepository {

    override fun signInWithGoogle(): Flow<AuthResponse> {
        return authClient.signInWithGoogle()
    }

    override suspend fun signOut(): Result<Unit> {
        return authClient.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return authClient.getCurrentUser()
    }

    override suspend fun register(
        email: String,
        password: String
    ): Result<String> {
        return authClient.register(email, password)
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<String> {
        return authClient.login(email, password)
    }
}