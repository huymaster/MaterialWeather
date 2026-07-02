package com.github.huymaster.materialweather

import android.app.Application
import com.github.huymaster.materialweather.di.ApplicationKoinProvider
import com.google.android.material.color.DynamicColors
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initialize()
    }

    private fun initialize() {
        DynamicColors.applyToActivitiesIfAvailable(this)
        setupKoin()
    }

    private fun setupKoin() {
        startKoin {
            androidLogger(if (BuildConfig.DEBUG) Level.DEBUG else Level.WARNING)
            androidContext(this@MainApplication)
            modules(ApplicationKoinProvider.getModules())
        }
    }
}