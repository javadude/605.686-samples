package com.javadude.moviesdb

import android.app.Application
import com.facebook.stetho.Stetho

class MovieApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}