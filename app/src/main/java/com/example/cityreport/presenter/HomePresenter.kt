package com.example.cityreport.presenter

import com.example.cityreport.model.Incidencia
import com.example.cityreport.model.IncidenciaRepository
import com.example.cityreport.view.contracts.HomeContract

class HomePresenter(private val repository: IncidenciaRepository) : HomeContract.Presenter {

    private var view: HomeContract.View? = null
    private var todasLasIncidencias: List<Incidencia> = emptyList()

    override fun attachView(view: HomeContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun cargarIncidencias() {
        todasLasIncidencias = repository.obtenerTodas()
        if (todasLasIncidencias.isEmpty()) {
            view?.mostrarEstadoVacio(true)
        } else {
            view?.mostrarEstadoVacio(false)
            view?.mostrarIncidencias(todasLasIncidencias)
        }
    }

    override fun filtrarIncidencias(query: String) {
        if (query.isBlank()) {
            cargarIncidencias()
            return
        }
        val filtradas = todasLasIncidencias.filter {
            it.titulo.contains(query, ignoreCase = true) ||
                    it.descripcion.contains(query, ignoreCase = true) ||
                    it.categoria.nombreDisplay.contains(query, ignoreCase = true)
        }
        if (filtradas.isEmpty()) {
            view?.mostrarEstadoVacio(true)
        } else {
            view?.mostrarEstadoVacio(false)
            view?.mostrarIncidencias(filtradas)
        }
    }

    override fun onIncidenciaSeleccionada(incidencia: Incidencia) {
        view?.navegarADetalle(incidencia.id)
    }

    override fun onNuevaIncidenciaClick() {
        view?.navegarANuevaIncidencia()
    }
}
