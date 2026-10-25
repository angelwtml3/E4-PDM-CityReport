package com.example.cityreport.model

interface IncidenciaRepository {
    fun obtenerTodas(): List<Incidencia>
    fun obtenerPorId(id: Int): Incidencia?
    fun guardar(inc: Incidencia)
    fun eliminar(id: Int)
    fun actualizarEstado(id: Int, resuelta: Boolean)
}
