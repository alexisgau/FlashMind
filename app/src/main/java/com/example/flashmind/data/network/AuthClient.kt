package com.example.flashmind.data.network

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.flashmind.R
import com.example.flashmind.domain.model.AuthResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject


class AuthClient @Inject constructor(@ApplicationContext private val context: Context, private val auth:FirebaseAuth) {

    fun signInWithEmailAndPassword(email: String, password: String){

        auth.signInWithEmailAndPassword(email,password)

    }



    fun signInWithGoogle(): Flow<AuthResponse> = callbackFlow {
        try {
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .setAutoSelectEnabled(false)
                .setNonce(createNonce())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val credentialManager = CredentialManager.create(context)
            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is CustomCredential) {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential =
                        GoogleIdTokenCredential.createFrom(credential.data)


                    if (googleIdTokenCredential.idToken == null) {
                        trySend(AuthResponse.Error(message = "El ID token de Google es nulo"))
                        return@callbackFlow
                    }

                    val firebaseCredential =
                        GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

                    auth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                trySend(AuthResponse.Success)
                            } else {
                                val errorMsg = task.exception?.let {
                                    "Firebase auth error: ${it.message}\n${it.cause?.message}"
                                } ?: "Error desconocido al autenticar en Firebase"
                                trySend(AuthResponse.Error(message = errorMsg))
                            }
                        }
                }
            }
        } catch (e: Exception) {
            trySend(AuthResponse.Error(message = "Error completo: ${e.message}\n${e.stackTraceToString()}"))
        }

        awaitClose()
    }


    fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
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



