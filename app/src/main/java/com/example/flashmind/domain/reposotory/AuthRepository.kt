package com.example.flashmind.domain.reposotory


import android.net.Uri
import com.example.flashmind.domain.model.AuthResponse
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signInWithGoogle(): Flow<AuthResponse>
    suspend fun signInAnonymously(): Result<String>
    suspend fun signOut(): Result<Unit>
    fun getCurrentUser(): FirebaseUser?
    suspend fun register(email: String, password: String): Result<String>
    suspend fun login(email: String, password: String): Result<String>

    suspend fun updateUserName(name: String): Result<Unit>

    suspend fun updateProfilePicture(imageUri: Uri): Result<String>
    suspend fun upgradeAnonymousAccount(email: String, password: String): Result<String>

    fun isUserAnonymous(): Boolean

    suspend fun deleteAccount(): Result<Unit>
}