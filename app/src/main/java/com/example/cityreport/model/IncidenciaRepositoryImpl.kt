package com.example.cityreport.model

import com.example.cityreport.CityReportApp
import com.example.cityreport.data.local.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class IncidenciaRepositoryImpl private constructor() : IncidenciaRepository {

    companion object {
        @Volatile
        private var instance: IncidenciaRepositoryImpl? = null

        fun getInstance(): IncidenciaRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: IncidenciaRepositoryImpl().also { instance = it }
            }
        }
    }

    private val dao get() = AppDatabase.getInstance(CityReportApp.instance).incidenciaDao()

    override fun obtenerTodas(): List<Incidencia> {
        return runBlocking(Dispatchers.IO) {
            dao.obtenerTodas()
        }
    }

    override fun obtenerPorId(id: Int): Incidencia? {
        return runBlocking(Dispatchers.IO) {
            dao.obtenerPorId(id)
        }
    }

    override fun guardar(inc: Incidencia) {
        runBlocking(Dispatchers.IO) {
            if (inc.id == 0) {
                dao.insertar(inc)
            } else {
                dao.actualizar(inc)
            }
        }
    }

    override fun eliminar(id: Int) {
        runBlocking(Dispatchers.IO) {
            dao.eliminarPorId(id)
        }
    }

    override fun actualizarEstado(id: Int, resuelta: Boolean) {
        runBlocking(Dispatchers.IO) {
            dao.actualizarEstado(id, resuelta)
        }
    }
}
