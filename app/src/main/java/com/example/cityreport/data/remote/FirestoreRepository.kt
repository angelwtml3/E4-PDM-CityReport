package com.example.cityreport.data.remote

import com.example.cityreport.model.Categoria
import com.example.cityreport.model.Incidencia
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object FirestoreRepository {

    private val db: FirebaseFirestore?
        get() = try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            null
        }

    suspend fun guardarIncidencia(incidencia: Incidencia, userId: String): Boolean {
        val firestore = db ?: return false
        return try {
            val docRef = firestore.collection("users")
                .document(userId)
                .collection("incidencias")
                .document(incidencia.id.toString())

            val mapData = mapOf(
                "id" to incidencia.id,
                "titulo" to incidencia.titulo,
                "descripcion" to incidencia.descripcion,
                "categoria" to incidencia.categoria.name,
                "latitud" to incidencia.latitud,
                "longitud" to incidencia.longitud,
                "fotoUri" to (incidencia.fotoUri ?: ""),
                "fecha" to incidencia.fecha,
                "resuelta" to incidencia.resuelta,
                "userId" to userId
            )

            docRef.set(mapData).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun obtenerIncidencias(userId: String): List<Incidencia> {
        val firestore = db ?: return emptyList()
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("incidencias")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val id = doc.getLong("id")?.toInt() ?: 0
                val titulo = doc.getString("titulo") ?: return@mapNotNull null
                val descripcion = doc.getString("descripcion") ?: ""
                val catStr = doc.getString("categoria") ?: Categoria.VIALIDAD.name
                val categoria = try { Categoria.valueOf(catStr) } catch (e: Exception) { Categoria.VIALIDAD }
                val lat = doc.getDouble("latitud") ?: 0.0
                val lng = doc.getDouble("longitud") ?: 0.0
                val fotoUri = doc.getString("fotoUri")
                val fecha = doc.getString("fecha") ?: ""
                val resuelta = doc.getBoolean("resuelta") ?: false

                Incidencia(
                    id = id,
                    titulo = titulo,
                    descripcion = descripcion,
                    categoria = categoria,
                    latitud = lat,
                    longitud = lng,
                    fotoUri = fotoUri,
                    fecha = fecha,
                    resuelta = resuelta
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun actualizarEstado(incidenciaId: Int, resuelta: Boolean, userId: String): Boolean {
        val firestore = db ?: return false
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("incidencias")
                .document(incidenciaId.toString())
                .update("resuelta", resuelta)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun eliminarIncidencia(incidenciaId: Int, userId: String): Boolean {
        val firestore = db ?: return false
        return try {
            firestore.collection("users")
                .document(userId)
                .collection("incidencias")
                .document(incidenciaId.toString())
                .delete()
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
