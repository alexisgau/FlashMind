package com.example.flashmind.domain.reposotory


import com.example.flashmind.domain.model.AuthResponse
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signInWithGoogle(): Flow<AuthResponse>
    fun getCurrentUser(): FirebaseUser?
    fun signInWithEmailAndPassword(email: String, password: String)
}