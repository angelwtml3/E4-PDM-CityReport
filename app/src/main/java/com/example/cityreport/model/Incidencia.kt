package com.example.cityreport.model

// Enum para categorías
enum class Categoria { VIALIDAD, AGUA, LUZ, BASURA }

// Data class: el modelo de datos de CityReport
data class Incidencia(
    val id: Int,
    val titulo: String,
    val descripcion: String,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val resuelta: Boolean = false
)