package com.javadude.moviesnav

import com.javadude.moviesnav.db.Database
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object ServiceLocator {
    lateinit var db : Database
    var executor: Executor = Executors.newSingleThreadExecutor()
}