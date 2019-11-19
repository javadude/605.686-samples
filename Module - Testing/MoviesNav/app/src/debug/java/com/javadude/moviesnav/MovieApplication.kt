@file:Suppress("unused")

package com.javadude.moviesnav

import com.facebook.stetho.Stetho

class MovieApplication : MovieApplicationBase() {
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}