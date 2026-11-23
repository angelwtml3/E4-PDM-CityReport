package com.example.cityreport.data.remote

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val uid: String,
    val nombre: String,
    val email: String,
    val fotoUrl: String?
)

object FirebaseAuthManager {

    private val auth: FirebaseAuth?
        get() = try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            null
        }

    val usuarioActual: FirebaseUser?
        get() = try {
            auth?.currentUser
        } catch (e: Exception) {
            null
        }

    fun obtenerPerfilUsuario(): UserProfile? {
        val user = usuarioActual ?: return null
        return UserProfile(
            uid = user.uid,
            nombre = user.displayName.orEmpty().ifBlank { "Usuario de CityReport" },
            email = user.email.orEmpty().ifBlank { "Sin correo" },
            fotoUrl = user.photoUrl?.toString()
        )
    }

    suspend fun iniciarSesionConGoogle(context: Context, webClientId: String): Result<FirebaseUser> {
        val firebaseAuth = auth ?: return Result.failure(IllegalStateException("Firebase no está configurado. Agrega google-services.json"))
        return try {
            val credentialManager = CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context = context, request = request)
            val credential = result.credential

            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                val user = authResult.user ?: throw Exception("No se pudo obtener el usuario de Firebase")
                Result.success(user)
            } else if (credential is GoogleIdTokenCredential) {
                val firebaseCredential = GoogleAuthProvider.getCredential(credential.idToken, null)
                val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
                val user = authResult.user ?: throw Exception("No se pudo obtener el usuario de Firebase")
                Result.success(user)
            } else {
                Result.failure(Exception("Tipo de credencial no soportado"))
            }
        } catch (e: GetCredentialException) {
            Result.failure(e)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cerrarSesion() {
        try {
            auth?.signOut()
        } catch (_: Exception) {
        }
    }
}
