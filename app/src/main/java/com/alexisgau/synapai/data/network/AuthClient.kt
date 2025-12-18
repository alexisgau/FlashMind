package com.alexisgau.synapai.data.network

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.alexisgau.synapai.R
import com.alexisgau.synapai.domain.model.AuthResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val firestore: FirebaseFirestore
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
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception(context.getString(R.string.auth_error_null_user)))

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
            Result.failure(
                Exception(
                    e.message ?: context.getString(R.string.auth_error_unknown_register)
                )
            )
        }
    }

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception(context.getString(R.string.auth_error_null_user)))
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
            Result.failure(
                Exception(
                    e.message ?: context.getString(R.string.auth_error_unknown_login)
                )
            )
        }
    }


    suspend fun updateUserName(name: String): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("No hay usuario logueado"))
        return try {
            val updates = userProfileChangeRequest { displayName = name }
            user.updateProfile(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun updateProfilePicture(imageUri: Uri): Result<String> {
        val user = auth.currentUser ?: return Result.failure(Exception("No hay usuario logueado"))

        return try {
            val storageRef = storage.reference.child("profile_images/${user.uid}")

            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(imageUri)
                ?: return Result.failure(Exception("No se pudo abrir el archivo de imagen"))

            val mimeType = contentResolver.getType(imageUri) ?: "image/jpeg"
            val metadata = StorageMetadata.Builder()
                .setContentType(mimeType)
                .build()

            inputStream.use { stream ->
                storageRef.putStream(stream, metadata).await()
            }


            val downloadUrl = storageRef.downloadUrl.await()


            val profileUpdates = userProfileChangeRequest {
                photoUri = downloadUrl
            }
            user.updateProfile(profileUpdates).await()

            Result.success(downloadUrl.toString())

        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun signInAnonymously(): Result<String> {
        return try {
            val authResult = auth.signInAnonymously().await()
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception(context.getString(R.string.auth_error_null_user)))
            Result.success(uid)
        } catch (e: FirebaseNetworkException) {
            Result.failure(Exception(context.getString(R.string.auth_error_no_internet)))
        } catch (e: Exception) {
            Result.failure(
                Exception(
                    e.message ?: context.getString(R.string.auth_error_unknown_login)
                )
            )
        }
    }

    suspend fun upgradeAnonymousAccount(email: String, password: String): Result<String> {
        val user = auth.currentUser
        if (user == null || !user.isAnonymous) {
            return Result.failure(Exception("No hay un usuario invitado activo para vincular."))
        }

        val credential = EmailAuthProvider.getCredential(email, password)

        return try {
            val authResult = user.linkWithCredential(credential).await()
            val uid = authResult.user?.uid
                ?: return Result.failure(Exception("Usuario nulo tras vincular"))
            Result.success(uid)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(Exception("Este email ya tiene una cuenta. Por favor inicia sesión."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun isUserAnonymous(): Boolean {
        return auth.currentUser?.isAnonymous == true
    }

    fun signInWithGoogle(): Flow<AuthResponse> = flow {
        try {
            val googleIdToken = getGoogleIdToken()

            val firebaseCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
            val currentUser = auth.currentUser

            if (currentUser != null && currentUser.isAnonymous) {
                try {
                    currentUser.linkWithCredential(firebaseCredential).await()
                } catch (e: FirebaseAuthUserCollisionException) {
                    auth.signInWithCredential(firebaseCredential).await()
                }
            } else {
                auth.signInWithCredential(firebaseCredential).await()
            }

            emit(AuthResponse.Success)

        } catch (e: GetCredentialCancellationException) {

            Log.d("AuthClient", "Google Sign-In cancelado por el usuario")
            emit(AuthResponse.Error(context.getString(R.string.auth_error_google_cancelled)))

        } catch (e: NoCredentialException) {
            Log.e("AuthClient", "No hay cuentas de Google disponibles: ${e.message}")
            emit(AuthResponse.Error(context.getString(R.string.auth_error_no_google_account)))

        } catch (e: GetCredentialException) {
            Log.e("AuthClient", "Error de Credential Manager: ${e.message}")
            emit(AuthResponse.Error(context.getString(R.string.auth_error_google)))

        } catch (e: Exception) {
            Log.e("AuthClient", "Error desconocido en signInWithGoogle: ${e.message}")
            emit(AuthResponse.Error(context.getString(R.string.auth_error_unknown)))
        }
    }
    private suspend fun getGoogleIdToken(): String {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(context.getString(R.string.web_client_id))
            .setAutoSelectEnabled(true)
            .setNonce(createNonce())
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()


        val result = CredentialManager.create(context).getCredential(context, request)
        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            return googleIdTokenCredential.idToken
        } else {
            throw Exception("Tipo de credencial no reconocido")
        }
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


    suspend fun deleteAccount(): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("No user logged in"))
        val userId = user.uid

        return try {
            try {
                storage.reference.child("profile_images/$userId").delete().await()
            } catch (e: Exception) {
                Log.e("DeleteAccount", "Error borrando imagen: ${e.message}")
            }

            // 2. Borrar datos de usuario en Firestore
            // (Esto borra el documento raíz, las subcolecciones quedan huérfanas pero inaccesibles, suficiente para MVP)
            firestore.collection("users").document(userId).delete().await()

            // 3. Borrar el usuario de Authentication (Lo más importante)
            user.delete().await()

            Result.success(Unit)
        } catch (e: Exception) {
            // Si falla (ej. requiere re-autenticación), devolvemos el error
            Result.failure(e)
        }
    }
}


