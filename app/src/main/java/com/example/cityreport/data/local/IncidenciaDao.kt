package com.example.cityreport.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cityreport.model.Incidencia
import kotlinx.coroutines.flow.Flow

@Dao
interface IncidenciaDao {

    @Query("SELECT * FROM incidencias ORDER BY id DESC")
    fun obtenerTodasFlow(): Flow<List<Incidencia>>

    @Query("SELECT * FROM incidencias ORDER BY id DESC")
    fun obtenerTodas(): List<Incidencia>

    @Query("SELECT * FROM incidencias WHERE id = :id")
    fun obtenerPorId(id: Int): Incidencia?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(incidencia: Incidencia): Long

    @Update
    fun actualizar(incidencia: Incidencia)

    @Query("UPDATE incidencias SET resuelta = :resuelta WHERE id = :id")
    fun actualizarEstado(id: Int, resuelta: Boolean)

    @Query("DELETE FROM incidencias WHERE id = :id")
    fun eliminarPorId(id: Int)

    @Delete
    fun eliminar(incidencia: Incidencia)
}
