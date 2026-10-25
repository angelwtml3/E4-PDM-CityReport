package com.example.cityreport.presenter

import com.example.cityreport.model.IncidenciaRepository
import com.example.cityreport.view.contracts.DetalleIncidenciaContract

class DetalleIncidenciaPresenter(
    private val repository: IncidenciaRepository
) : DetalleIncidenciaContract.Presenter {

    private var view: DetalleIncidenciaContract.View? = null

    override fun attachView(view: DetalleIncidenciaContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun cargarDetalle(id: Int) {
        val incidencia = repository.obtenerPorId(id)
        if (incidencia != null) {
            view?.mostrarDetalle(incidencia)
        } else {
            view?.mostrarError("No se encontró el reporte solicitado.")
        }
    }

    override fun cambiarEstadoResuelta(id: Int, resuelta: Boolean) {
        repository.actualizarEstado(id, resuelta)
        view?.notificarEstadoActualizado(resuelta)
    }
}
