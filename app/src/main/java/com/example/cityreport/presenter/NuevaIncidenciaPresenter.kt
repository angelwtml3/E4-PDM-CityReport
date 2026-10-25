package com.example.cityreport.presenter

import com.example.cityreport.model.Categoria
import com.example.cityreport.model.Incidencia
import com.example.cityreport.model.IncidenciaRepository
import com.example.cityreport.view.contracts.NuevaIncidenciaContract
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class NuevaIncidenciaPresenter(
    private val repository: IncidenciaRepository
) : NuevaIncidenciaContract.Presenter {

    private var view: NuevaIncidenciaContract.View? = null

    override fun attachView(view: NuevaIncidenciaContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun cargarCategorias() {
        view?.mostrarCategorias(Categoria.values().toList())
    }

    override fun guardarReporte(
        titulo: String,
        descripcion: String,
        categoria: Categoria,
        lat: Double,
        lng: Double,
        fotoUri: String?
    ) {
        if (titulo.isBlank()) {
            view?.mostrarErrorValidacion("El título es obligatorio")
            return
        }
        if (descripcion.isBlank()) {
            view?.mostrarErrorValidacion("La descripción es obligatoria")
            return
        }

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val fechaActual = dateFormat.format(Date())

        val nuevaIncidencia = Incidencia(
            id = 0,
            titulo = titulo.trim(),
            descripcion = descripcion.trim(),
            categoria = categoria,
            latitud = lat,
            longitud = lng,
            fotoUri = fotoUri,
            fecha = fechaActual,
            resuelta = false
        )

        repository.guardar(nuevaIncidencia)
        view?.mostrarMensajeExito("Reporte guardado correctamente")
        view?.reporteGuardadoExitosamente()
    }

    override fun onFotoCapturada(fotoUri: String) {
        view?.mostrarFotoCapturada(fotoUri)
    }

    override fun onUbicacionObtenida(lat: Double, lng: Double) {
        view?.mostrarCoordenadas(lat, lng)
    }
}
