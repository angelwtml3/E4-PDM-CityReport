package com.example.cityreport.model

class IncidenciaRepositoryImpl private constructor() : IncidenciaRepository {

    companion object {
        @Volatile
        private var instance: IncidenciaRepositoryImpl? = null

        fun getInstance(): IncidenciaRepositoryImpl {
            return instance ?: synchronized(this) {
                instance ?: IncidenciaRepositoryImpl().also { instance = it }
            }
        }
    }

    private val listaIncidencias = mutableListOf<Incidencia>()
    private var siguienteId = 1

    init {
        // Datos de prueba iniciales
        guardar(
            Incidencia(
                id = 0,
                titulo = "Bache en calle Principal",
                descripcion = "Hay un bache enorme que afecta el tráfico vehicular y pone en riesgo a los peatones.",
                categoria = Categoria.VIALIDAD,
                latitud = 19.4326,
                longitud = -99.1332,
                fecha = "19/09/2026",
                resuelta = false
            )
        )
        guardar(
            Incidencia(
                id = 0,
                titulo = "Fuga de agua potable",
                descripcion = "Fuga constante frente al parque central. Desperdicio considerable de agua.",
                categoria = Categoria.AGUA,
                latitud = 19.4340,
                longitud = -99.1350,
                fecha = "18/09/2026",
                resuelta = true
            )
        )
        guardar(
            Incidencia(
                id = 0,
                titulo = "Luminaria apagada",
                descripcion = "Lámpara de alumbrado público sin funcionar en la esquina de la av. Juárez.",
                categoria = Categoria.ALUMBRADO,
                latitud = 19.4310,
                longitud = -99.1320,
                fecha = "17/09/2026",
                resuelta = false
            )
        )
    }

    override fun obtenerTodas(): List<Incidencia> {
        return listaIncidencias.toList()
    }

    override fun obtenerPorId(id: Int): Incidencia? {
        return listaIncidencias.find { it.id == id }
    }

    override fun guardar(inc: Incidencia) {
        if (inc.id == 0) {
            val nuevaIncidencia = inc.copy(id = siguienteId++)
            listaIncidencias.add(nuevaIncidencia)
        } else {
            val index = listaIncidencias.indexOfFirst { it.id == inc.id }
            if (index != -1) {
                listaIncidencias[index] = inc
            }
        }
    }

    override fun eliminar(id: Int) {
        listaIncidencias.removeAll { it.id == id }
    }

    override fun actualizarEstado(id: Int, resuelta: Boolean) {
        val index = listaIncidencias.indexOfFirst { it.id == id }
        if (index != -1) {
            listaIncidencias[index] = listaIncidencias[index].copy(resuelta = resuelta)
        }
    }
}
