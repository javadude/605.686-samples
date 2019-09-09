package com.javadude.moviesdb

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.io.PrintWriter
import java.io.StringWriter
import java.util.concurrent.Executors

fun <X, Y> LiveData<X>.switchMap(defaultValue : Y?, function : (X?) -> LiveData<Y>?): LiveData<Y> =
    Transformations.switchMap(this) {
        function(it) ?: MutableLiveData<Y>().apply { this.value = defaultValue }
    }

class MovieViewModel3(application: Application) : AndroidViewModel(application) {
    private val executor = Executors.newSingleThreadExecutor()
    private val db = Room.databaseBuilder(application, Database::class.java, "MOVIES")
        .addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)

                db.execSQL("INSERT INTO Movie (id, title, description) VALUES('m1', 'The Transporter', 'Jason Statham kicks a guy in the face')")
                db.execSQL("INSERT INTO Movie (id, title, description) VALUES('m2', 'Transporter 2', 'Jason Statham kicks a bunch of guys in the face')")
                db.execSQL("INSERT INTO Movie (id, title, description) VALUES('m3', 'Hobbs and Shaw', 'Cars, Explosions and Stuff')")
                db.execSQL("INSERT INTO Movie (id, title, description) VALUES('m4', 'Jumanji', 'The Rock smolders')")


                db.execSQL("INSERT INTO Actor (id, name) VALUES('a1', 'Jason Statham')")
                db.execSQL("INSERT INTO Actor (id, name) VALUES('a2', 'The Rock')")
                db.execSQL("INSERT INTO Actor (id, name) VALUES('a3', 'Shu Qi')")
                db.execSQL("INSERT INTO Actor (id, name) VALUES('a4', 'Amber Valletta')")
                db.execSQL("INSERT INTO Actor (id, name) VALUES('a5', 'Kevin Hart')")


                db.execSQL("INSERT INTO Role (movieId, actorId, roleName, `order`) VALUES('m1', 'a1', 'Frank Martin', 1)")
                db.execSQL("INSERT INTO Role (movieId, actorId, roleName, `order`) VALUES('m1', 'a3', 'Lai', 2)")
                db.execSQL("INSERT INTO Role (movieId, actorId, roleName, `order`) VALUES('m2', 'a1', 'Frank Martin', 1)")
                db.execSQL("INSERT INTO Role (movieId, actorId, roleName, `order`) VALUES('m2', 'a4', 'Audrey Billings', 2)")
                db.execSQL("INSERT INTO Role (movieId, actorId, roleName, `order`) VALUES('m3', 'a2', 'Hobbs', 1)")
                db.execSQL("INSERT INTO Role (movieId, actorId, roleName, `order`) VALUES('m3', 'a1', 'Shaw', 2)")
                db.execSQL("INSERT INTO Role (movieId, actorId, roleName, `order`) VALUES('m4', 'a2', 'Spencer', 1)")
                db.execSQL("INSERT INTO Role (movieId, actorId, roleName, `order`) VALUES('m4', 'a5', 'Fridge', 2)")
            }
        })
        .build()

    val message = MutableLiveData<String>().apply { value = "" }

    val allMovies = db.dao.allMoviesAsync()
    val allActors = db.dao.allActorsAsync()

    val selectedMovieId = MutableLiveData<String>().apply { value = null }
    val selectedActorId = MutableLiveData<String>().apply { value = null }

    val selectedMovie = selectedMovieId.switchMap(null) {
        it?.let { db.dao.getMovieAsync(it) }
    }
    val selectedActor = selectedActorId.switchMap(null) {
        it?.let { db.dao.getActorAsync(it) }
    }

    val cast = selectedMovie.switchMap(emptyList()) {
        it?.let { db.dao.rolesForMovieAsync(it.id) }
    }

    val cast2 = selectedMovie.switchMap(emptyList()) {
        it?.let { db.dao.rolesForMovieAsync2(it.id) }
    }

    val filmography = selectedActor.switchMap(emptyList()) {
        it?.let { db.dao.moviesForActorAsync(it.id) }
    }

    fun deleteMovie(movieId : String) {
        executor.execute {
            try {
                db.dao.deleteMovie(movieId)
            } catch (t : Throwable) {
                postStackTraceAsMessage(t)
            }
        }
    }

    fun deleteActor(actorId : String) {
        executor.execute {
            try {
                db.dao.deleteActor(actorId)
            } catch (t : Throwable) {
                postStackTraceAsMessage(t)
            }
        }
    }

    private fun postStackTraceAsMessage(t : Throwable) {
        StringWriter().use { sw ->
            PrintWriter(sw).use { pw ->
                t.printStackTrace(pw)
            }
            message.postValue(sw.toString())
        }
    }
}