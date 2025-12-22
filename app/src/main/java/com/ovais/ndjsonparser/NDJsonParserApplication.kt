package com.ovais.ndjsonparser

import android.app.Application
import com.ovais.ndjsonparser.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class NDJsonParserApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        startKoin {
            androidContext(this@NDJsonParserApplication)
            modules(appModule)
        }
    }
}

