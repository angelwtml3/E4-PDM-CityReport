package com.example.cityreport

import com.example.cityreport.model.Categoria
import com.example.cityreport.model.Incidencia
import com.example.cityreport.model.IncidenciaRepository
import com.example.cityreport.presenter.DetalleIncidenciaPresenter
import com.example.cityreport.presenter.HomePresenter
import com.example.cityreport.presenter.NuevaIncidenciaPresenter
import com.example.cityreport.view.contracts.DetalleIncidenciaContract
import com.example.cityreport.view.contracts.HomeContract
import com.example.cityreport.view.contracts.NuevaIncidenciaContract
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PresenterTest {

    private class FakeRepository : IncidenciaRepository {
        private val list = mutableListOf(
            Incidencia(1, "Bache 1", "Descripcion 1", Categoria.VIALIDAD),
            Incidencia(2, "Fuga 2", "Descripcion 2", Categoria.AGUA)
        )

        override fun obtenerTodas(): List<Incidencia> = list.toList()

        override fun obtenerPorId(id: Int): Incidencia? = list.find { it.id == id }

        override fun guardar(inc: Incidencia) {
            list.add(inc)
        }

        override fun eliminar(id: Int) {
            list.removeAll { it.id == id }
        }

        override fun actualizarEstado(id: Int, resuelta: Boolean) {
            val index = list.indexOfFirst { it.id == id }
            if (index != -1) {
                list[index] = list[index].copy(resuelta = resuelta)
            }
        }
    }

    @Test
    fun testHomePresenterCargarIncidencias() {
        val repo = FakeRepository()
        val presenter = HomePresenter(repo)
        var incidenciasMostradas: List<Incidencia>? = null

        val fakeView = object : HomeContract.View {
            override fun mostrarIncidencias(lista: List<Incidencia>) {
                incidenciasMostradas = lista
            }

            override fun mostrarEstadoVacio(visible: Boolean) {}
            override fun navegarADetalle(id: Int) {}
            override fun navegarANuevaIncidencia() {}
        }

        presenter.attachView(fakeView)
        presenter.cargarIncidencias()

        assertNotNull(incidenciasMostradas)
        assertEquals(2, incidenciasMostradas?.size)
    }

    @Test
    fun testNuevaIncidenciaPresenterValidation() {
        val repo = FakeRepository()
        val presenter = NuevaIncidenciaPresenter(repo)
        var errorMensaje: String? = null

        val fakeView = object : NuevaIncidenciaContract.View {
            override fun mostrarCategorias(categorias: List<Categoria>) {}
            override fun mostrarCoordenadas(lat: Double, lng: Double) {}
            override fun mostrarFotoCapturada(fotoUri: String) {}
            override fun mostrarErrorValidacion(mensaje: String) {
                errorMensaje = mensaje
            }

            override fun mostrarMensajeExito(mensaje: String) {}
            override fun reporteGuardadoExitosamente() {}
            override fun solicitarPermisosCamara() {}
            override fun solicitarPermisosUbicacion() {}
            override fun solicitarPermisosNotificacion() {}
        }

        presenter.attachView(fakeView)
        presenter.guardarReporte("", "Descripcion sin titulo", Categoria.VIALIDAD, 0.0, 0.0, null)

        assertEquals("El título es obligatorio", errorMensaje)
    }

    @Test
    fun testDetalleIncidenciaPresenterCambiarEstado() {
        val repo = FakeRepository()
        val presenter = DetalleIncidenciaPresenter(repo)
        var estadoActualizado: Boolean? = null

        val fakeView = object : DetalleIncidenciaContract.View {
            override fun mostrarDetalle(incidencia: Incidencia) {}
            override fun mostrarError(mensaje: String) {}
            override fun notificarEstadoActualizado(resuelta: Boolean) {
                estadoActualizado = resuelta
            }

            override fun cerrarPantalla() {}
        }

        presenter.attachView(fakeView)
        presenter.cambiarEstadoResuelta(1, true)

        assertEquals(true, estadoActualizado)
        assertTrue(repo.obtenerPorId(1)?.resuelta == true)
    }
}
