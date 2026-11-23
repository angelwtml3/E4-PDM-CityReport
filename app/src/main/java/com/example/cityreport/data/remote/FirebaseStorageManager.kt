package com.example.cityreport.data.remote

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

object FirebaseStorageManager {

    private val storage: FirebaseStorage?
        get() = try {
            FirebaseStorage.getInstance()
        } catch (e: Exception) {
            null
        }

    suspend fun subirFotoIncidencia(fileUri: Uri, userId: String, incidenciaId: Int): String? {
        val st = storage ?: return null
        return try {
            val fileName = "foto_${System.currentTimeMillis()}.jpg"
            val ref = st.reference
                .child("users")
                .child(userId)
                .child("incidencias")
                .child(incidenciaId.toString())
                .child("imagenes")
                .child(fileName)

            ref.putFile(fileUri).await()
            val downloadUrl = ref.downloadUrl.await()
            downloadUrl.toString()
        } catch (e: Exception) {
            null
        }
    }
}
