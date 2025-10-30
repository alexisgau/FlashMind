package com.example.flashmind.data.network

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.flashmind.R
import com.example.flashmind.domain.model.AuthResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class AuthClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth
) {

    fun getAuthState(): Flow<Boolean> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser != null)
        }
        auth.addAuthStateListener(authStateListener)
        awaitClose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    suspend fun register(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception(context.getString(R.string.auth_error_null_user)))
            Result.success(uid)
        } catch (e: FirebaseAuthWeakPasswordException) {
            Result.failure(Exception(context.getString(R.string.auth_error_weak_password)))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception(context.getString(R.string.auth_error_invalid_email)))
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(Exception(context.getString(R.string.auth_error_user_collision)))
        } catch (e: FirebaseNetworkException) {
            Result.failure(Exception(context.getString(R.string.auth_error_no_internet)))
        } catch (e: FirebaseTooManyRequestsException) {
            Result.failure(Exception(context.getString(R.string.auth_error_too_many_requests)))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: context.getString(R.string.auth_error_unknown_register)))
        }
    }

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception(context.getString(R.string.auth_error_null_user)))
            Result.success(uid)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(Exception(context.getString(R.string.auth_error_user_not_found)))
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(Exception(context.getString(R.string.auth_error_invalid_credentials)))
        } catch (e: FirebaseNetworkException) {
            Result.failure(Exception(context.getString(R.string.auth_error_no_internet)))
        } catch (e: FirebaseTooManyRequestsException) {
            Result.failure(Exception(context.getString(R.string.auth_error_too_many_requests)))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: context.getString(R.string.auth_error_unknown_login)))
        }
    }

    suspend fun signInAnonymously(): Result<String> {
        return try {
            val authResult = auth.signInAnonymously().await()
            val uid = authResult.user?.uid ?: return Result.failure(Exception(context.getString(R.string.auth_error_null_user)))
            Result.success(uid)
        } catch (e: FirebaseNetworkException) {
            Result.failure(Exception(context.getString(R.string.auth_error_no_internet)))
        } catch (e: Exception) {
            Result.failure(Exception(e.message ?: context.getString(R.string.auth_error_unknown_login)))
        }
    }

    fun signInWithGoogle(): Flow<AuthResponse> = callbackFlow {
        val job = launch {
            try {
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .setAutoSelectEnabled(true)
                    .setNonce(createNonce())
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(context = context, request = request)
                val credential = result.credential

                if (credential is CustomCredential &&
                    credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                ) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)

                    val firebaseCredential = GoogleAuthProvider.getCredential(
                        googleIdTokenCredential.idToken,
                        null
                    )

                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                trySend(AuthResponse.Success)
                            } else {
                                val errorMsg = task.exception?.message ?: context.getString(R.string.auth_error_google)
                                trySend(AuthResponse.Error(errorMsg))
                            }
                            channel.close()
                        }
                } else {
                    trySend(AuthResponse.Error(context.getString(R.string.auth_error_google)))
                    channel.close()
                }
            } catch (e: GetCredentialException) {
                Log.e("AuthClient", "GetCredentialException: ${e.message}")
                trySend(AuthResponse.Error(context.getString(R.string.auth_error_google)))
                channel.close()
            } catch (e: Exception) {
                Log.e("AuthClient", "Error en signInWithGoogle: ${e.message}")
                trySend(AuthResponse.Error(e.message ?: context.getString(R.string.auth_error_google)))
                channel.close()
            }
        }
        awaitClose { job.cancel() }
    }

    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            val credentialManager = CredentialManager.create(context)
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthClient", "Error signing out", e)
            Result.failure(Exception(e.message ?: context.getString(R.string.auth_logout_error)))
        }
    }

    private fun createNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it ->
            str + "%02x".format(it)
        }
    }
}


