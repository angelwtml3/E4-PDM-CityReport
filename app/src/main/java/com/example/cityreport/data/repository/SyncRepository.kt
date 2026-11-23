package com.example.cityreport.data.repository

import android.net.Uri
import com.example.cityreport.data.remote.FirebaseAuthManager
import com.example.cityreport.data.remote.FirebaseStorageManager
import com.example.cityreport.data.remote.FirestoreRepository
import com.example.cityreport.model.Incidencia
import com.example.cityreport.model.IncidenciaRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SyncRepository {

    private val localRepository = IncidenciaRepositoryImpl.getInstance()

    suspend fun guardarYSincronizarIncidencia(incidencia: Incidencia, localFileUri: Uri?): Incidencia = withContext(Dispatchers.IO) {
        // 1. Guardar localmente en Room primero
        localRepository.guardar(incidencia)
        val incidenciasLocales = localRepository.obtenerTodas()
        val incidenciaGuardada = incidenciasLocales.firstOrNull {
            it.titulo == incidencia.titulo && it.descripcion == incidencia.descripcion
        } ?: incidencia

        val user = FirebaseAuthManager.usuarioActual
        if (user != null) {
            var remoteFotoUrl = incidenciaGuardada.fotoUri

            // 2. Subir imagen a Firebase Storage si aplica
            if (localFileUri != null) {
                val uploadedUrl = FirebaseStorageManager.subirFotoIncidencia(
                    fileUri = localFileUri,
                    userId = user.uid,
                    incidenciaId = incidenciaGuardada.id
                )
                if (!uploadedUrl.isNullOrBlank()) {
                    remoteFotoUrl = uploadedUrl
                    val incidenciaConFotoRemota = incidenciaGuardada.copy(fotoUri = remoteFotoUrl)
                    localRepository.guardar(incidenciaConFotoRemota)
                }
            }

            // 3. Sincronizar con Cloud Firestore
            val incidenciaFinal = incidenciaGuardada.copy(fotoUri = remoteFotoUrl)
            FirestoreRepository.guardarIncidencia(incidenciaFinal, user.uid)
            return@withContext incidenciaFinal
        }

        return@withContext incidenciaGuardada
    }

    suspend fun sincronizarConNube() = withContext(Dispatchers.IO) {
        val user = FirebaseAuthManager.usuarioActual ?: return@withContext
        val remotes = FirestoreRepository.obtenerIncidencias(user.uid)
        for (remoteInc in remotes) {
            localRepository.guardar(remoteInc)
        }
    }
}
