package com.example.cityreport.model

data class Incidencia(
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val categoria: Categoria = Categoria.VIALIDAD,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val fotoUri: String? = null,
    val fecha: String = "",
    val resuelta: Boolean = false
)
