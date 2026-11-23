package com.example.cityreport.data.local

import androidx.room.TypeConverter
import com.example.cityreport.model.Categoria

class Converters {
    @TypeConverter
    fun fromCategoria(categoria: Categoria?): String {
        return categoria?.name ?: Categoria.VIALIDAD.name
    }

    @TypeConverter
    fun toCategoria(value: String?): Categoria {
        return try {
            if (value != null) Categoria.valueOf(value) else Categoria.VIALIDAD
        } catch (e: Exception) {
            Categoria.VIALIDAD
        }
    }
}
