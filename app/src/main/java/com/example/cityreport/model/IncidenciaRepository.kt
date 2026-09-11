package com.example.cityreport.model

// Interfaz que define el contrato[cite: 1]
interface IncidenciaRepository {
    fun obtenerTodas(): List<Incidencia>
    fun guardar(inc: Incidencia)
    fun eliminar(id: Int)
}