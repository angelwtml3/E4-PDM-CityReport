package com.example.cityreport.view.contracts

import com.example.cityreport.model.Categoria

interface NuevaIncidenciaContract {
    interface View {
        fun mostrarCategorias(categorias: List<Categoria>)
        fun mostrarCoordenadas(lat: Double, lng: Double)
        fun mostrarFotoCapturada(fotoUri: String)
        fun mostrarErrorValidacion(mensaje: String)
        fun mostrarMensajeExito(mensaje: String)
        fun reporteGuardadoExitosamente()
        fun solicitarPermisosCamara()
        fun solicitarPermisosUbicacion()
        fun solicitarPermisosNotificacion()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun cargarCategorias()
        fun guardarReporte(
            titulo: String,
            descripcion: String,
            categoria: Categoria,
            lat: Double,
            lng: Double,
            fotoUri: String?
        )
        fun onFotoCapturada(fotoUri: String)
        fun onUbicacionObtenida(lat: Double, lng: Double)
    }
}
