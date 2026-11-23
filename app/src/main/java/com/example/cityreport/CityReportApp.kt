package com.example.cityreport

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.cityreport.data.local.AppDatabase
import com.example.cityreport.data.local.UserPreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class CityReportApp : Application() {

    lateinit var preferencesRepository: UserPreferencesRepository
        private set

    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getInstance(this)
        preferencesRepository = UserPreferencesRepository(this)

        // Cargar y aplicar la preferencia del tema visual al iniciar la aplicación
        CoroutineScope(Dispatchers.Main).launch {
            val isDarkTheme = preferencesRepository.isDarkThemeFlow.first()
            if (isDarkTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
    }

    companion object {
        lateinit var instance: CityReportApp
            private set
    }
}
