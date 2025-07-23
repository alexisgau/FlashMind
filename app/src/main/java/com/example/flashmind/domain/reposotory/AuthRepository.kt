package com.example.flashmind.domain.reposotory


import com.example.flashmind.domain.model.AuthResponse
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signInWithGoogle(): Flow<AuthResponse>
    fun signOutAccountWithGoogle():Flow<AuthResponse>
    fun getCurrentUser(): FirebaseUser?
    suspend fun register(email: String, password: String): Result<String>
    suspend fun login(email: String, password: String): Result<String>
}