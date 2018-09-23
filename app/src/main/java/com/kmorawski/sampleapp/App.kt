package com.kmorawski.sampleapp

import android.app.Application
import org.koin.android.ext.android.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(getApiModule(resources), viewModelsModule, loggerModule))
    }
}