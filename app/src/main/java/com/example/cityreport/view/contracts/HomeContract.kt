package com.example.cityreport.view.contracts

import com.example.cityreport.model.Incidencia

interface HomeContract {
    interface View {
        fun mostrarIncidencias(lista: List<Incidencia>)
        fun mostrarEstadoVacio(visible: Boolean)
        fun navegarADetalle(id: Int)
        fun navegarANuevaIncidencia()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun cargarIncidencias()
        fun filtrarIncidencias(query: String)
        fun onIncidenciaSeleccionada(incidencia: Incidencia)
        fun onNuevaIncidenciaClick()
    }
}
