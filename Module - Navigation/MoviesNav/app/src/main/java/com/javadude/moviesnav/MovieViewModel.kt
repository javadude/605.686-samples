package com.javadude.moviesnav

import android.app.Application
import androidx.lifecycle.*
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

fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, observer : (T?) -> Unit) =
    observe(lifecycleOwner, Observer {
        observer(it)
    })

open class MovieViewModel(application: Application) : AndroidViewModel(application) {
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

    val movieSelectionManager = SelectionManager<Movie>()
    val actorSelectionManager = SelectionManager<Actor>()

    // turns out that kotlin already defines a singleOrNull function...
    //    and here I thought I was being so original...
//    fun <E> Set<E>.singleOrNull() =
//        if (size != 1)
//            null
//        else
//            first()
//

    val cast = movieSelectionManager.selections.switchMap(emptyList()) {
        it?.singleOrNull()?.let { movie -> db.dao.rolesForMovieAsync(movie.id) }
    }

    val cast2 = movieSelectionManager.selections.switchMap(emptyList()) {
        it?.singleOrNull()?.let { actor -> db.dao.rolesForMovieAsync2(actor.id) }
    }

    val filmography = actorSelectionManager.selections.switchMap(emptyList()) {
        it?.singleOrNull()?.let { actor -> db.dao.moviesForActorAsync(actor.id) }
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

    fun addMovie(movie: Movie, vararg roles : Role) = executor.execute {
        db.dao.insert(movie)
//        roles.forEach {
//            db.dao.insert(it)
//        }
        // or
        db.dao.insert(*roles)
    }
    fun addActor(actor: Actor) = executor.execute {
        db.dao.insert(actor)
    }

    fun deleteSelectedMovies() {
        executor.execute {
            movieSelectionManager.selections.value?.let {
                db.dao.delete(*(it.toTypedArray()))
            }
            actorSelectionManager.clearSelections()
        }
    }

    fun deleteSelectedActors() {
        executor.execute {
            actorSelectionManager.selections.value?.let {
                db.dao.delete(*(it.toTypedArray()))
            }
            actorSelectionManager.clearSelections()
        }
    }

    fun deleteMovieAt(position: Int) {
        executor.execute {
            allMovies.value?.let {
                db.dao.delete(it[position])
            }
        }
        movieSelectionManager.clearSelections()
    }
    fun deleteActorAt(position: Int) {
        executor.execute {
            allActors.value?.let {
                db.dao.delete(it[position])
            }
        }
    }
}

