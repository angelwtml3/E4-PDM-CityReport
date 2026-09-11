package com.example.cityreport.model

// Sealed class - estados finitos y conocidos
sealed class EstadoUI {
    object Cargando : EstadoUI()
    data class Exito(val datos: List<Incidencia>) : EstadoUI()
    data class Error(val mensaje: String) : EstadoUI()
}