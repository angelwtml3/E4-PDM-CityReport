package com.example.cityreport.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "incidencias")
data class Incidencia(
    @field:PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @field:ColumnInfo(name = "titulo")
    val titulo: String,
    @field:ColumnInfo(name = "descripcion")
    val descripcion: String,
    @field:ColumnInfo(name = "categoria")
    val categoria: Categoria = Categoria.VIALIDAD,
    @field:ColumnInfo(name = "latitud")
    val latitud: Double = 0.0,
    @field:ColumnInfo(name = "longitud")
    val longitud: Double = 0.0,
    @field:ColumnInfo(name = "fotoUri")
    val fotoUri: String? = null,
    @field:ColumnInfo(name = "fecha")
    val fecha: String = "",
    @field:ColumnInfo(name = "resuelta")
    val resuelta: Boolean = false
)
