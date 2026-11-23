package com.example.cityreport

import com.example.cityreport.model.Categoria
import com.example.cityreport.model.Incidencia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RoomDataStoreTest {

    @Test
    fun testIncidenciaEntityProperties() {
        val incidencia = Incidencia(
            id = 10,
            titulo = "Alumbrado fallido",
            descripcion = "Calle sin luz",
            categoria = Categoria.ALUMBRADO,
            latitud = 19.4000,
            longitud = -99.1000,
            fotoUri = "file:///tmp/image.jpg",
            fecha = "20/09/2026",
            resuelta = false
        )

        assertEquals(10, incidencia.id)
        assertEquals("Alumbrado fallido", incidencia.titulo)
        assertEquals(Categoria.ALUMBRADO, incidencia.categoria)
        assertFalse(incidencia.resuelta)

        val incidenciaActualizada = incidencia.copy(resuelta = true)
        assertTrue(incidenciaActualizada.resuelta)
    }
}
