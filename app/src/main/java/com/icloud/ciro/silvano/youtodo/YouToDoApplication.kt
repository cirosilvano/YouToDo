package com.icloud.ciro.silvano.youtodo

import android.app.Application
import com.google.android.material.color.DynamicColors

/**
 * Classe che viene implemetata per poter applicare all'intera applicazione i colori dinamici
 */

class YouToDoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        /**
         * Metodo applyToActivitiesIfAvailable della classe DynamicColors
         * Applica i colori dinamici all'attività specificata con la sovrapposizione del tema designata dall'attributo del tema "dynamicColorThemeOverlay".
         */

        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}