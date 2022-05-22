package com.icloud.ciro.silvano.youtodo

import android.app.Application
import com.google.android.material.color.DynamicColors

class YouToDoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}