package com.example.flashmind.data.repository

import com.example.flashmind.data.network.AuthClient
import com.example.flashmind.domain.model.AuthResponse
import com.example.flashmind.domain.reposotory.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val authClient: AuthClient): AuthRepository {

    override fun signInWithGoogle(): Flow<AuthResponse> {
        return  authClient.signInWithGoogle()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return authClient.getCurrentUser()
    }

    override fun signInWithEmailAndPassword(email: String, password: String) {
        return authClient.signInWithEmailAndPassword(email,password)
    }
}