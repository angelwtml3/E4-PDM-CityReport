package com.example.cityreport.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cityreport.model.Categoria
import com.example.cityreport.model.Incidencia
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Incidencia::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun incidenciaDao(): IncidenciaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "city_report.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val dao = getInstance(context).incidenciaDao()
                            if (dao.obtenerTodas().isEmpty()) {
                                dao.insertar(
                                    Incidencia(
                                        titulo = "Bache en calle Principal",
                                        descripcion = "Hay un bache enorme que afecta el tráfico vehicular y pone en riesgo a los peatones.",
                                        categoria = Categoria.VIALIDAD,
                                        latitud = 19.4326,
                                        longitud = -99.1332,
                                        fecha = "19/09/2026",
                                        resuelta = false
                                    )
                                )
                                dao.insertar(
                                    Incidencia(
                                        titulo = "Fuga de agua potable",
                                        descripcion = "Fuga constante frente al parque central. Desperdicio considerable de agua.",
                                        categoria = Categoria.AGUA,
                                        latitud = 19.4340,
                                        longitud = -99.1350,
                                        fecha = "18/09/2026",
                                        resuelta = true
                                    )
                                )
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
