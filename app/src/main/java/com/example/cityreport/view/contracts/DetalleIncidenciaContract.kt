package com.example.cityreport.view.contracts

import com.example.cityreport.model.Incidencia

interface DetalleIncidenciaContract {
    interface View {
        fun mostrarDetalle(incidencia: Incidencia)
        fun mostrarError(mensaje: String)
        fun notificarEstadoActualizado(resuelta: Boolean)
        fun cerrarPantalla()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun cargarDetalle(id: Int)
        fun cambiarEstadoResuelta(id: Int, resuelta: Boolean)
    }
}
