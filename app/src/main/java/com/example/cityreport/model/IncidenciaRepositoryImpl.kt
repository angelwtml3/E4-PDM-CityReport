package com.example.cityreport.model

class IncidenciaRepositoryImpl : IncidenciaRepository {

    // Lista mutable en memoria para simular una base de datos
    private val listaIncidencias = mutableListOf<Incidencia>()
    private var siguienteId = 1

    override fun obtenerTodas(): List<Incidencia> {
        // Retornamos una copia de la lista para evitar modificaciones externas no deseadas
        return listaIncidencias.toList()
    }

    override fun guardar(inc: Incidencia) {
        if (inc.id == 0) {
            // Es un registro nuevo, le asignamos un ID y lo guardamos
            val nuevaIncidencia = inc.copy(id = siguienteId++)
            listaIncidencias.add(nuevaIncidencia)
        } else {
            // Es una actualización, buscamos el registro existente
            val index = listaIncidencias.indexOfFirst { it.id == inc.id }
            if (index != -1) {
                listaIncidencias[index] = inc
            }
        }
    }

    override fun eliminar(id: Int) {
        listaIncidencias.removeAll { it.id == id }
    }
}