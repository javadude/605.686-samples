package com.javadude.moviesnav

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.javadude.moviesnav.db.Actor
import com.javadude.moviesnav.db.Movie
import com.javadude.moviesnav.db.Role
import com.javadude.moviesnav.db.RoleInfo

fun <X, Y> LiveData<X>.switchMap(defaultValue : Y?, function : (X?) -> LiveData<Y>?): LiveData<Y> =
    Transformations.switchMap(this) {
        function(it) ?: MutableLiveData<Y>().apply { this.value = defaultValue }
    }

inline fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, crossinline observer : (T?) -> Unit) =
    observe(lifecycleOwner, Observer {
        observer(it)
    })

open class MovieViewModel : ViewModel() {

    private val executor = ServiceLocator.executor
    private val db = ServiceLocator.db

    val allMovies = db.dao.allMoviesAsync()
    val allActors = db.dao.allActorsAsync()

    val movieSelectionManager = SelectionManager<Movie>()
    val actorSelectionManager = SelectionManager<Actor>()
    val roleInfoSelectionManager = SelectionManager<RoleInfo>()
    val roleSelectionManager = SelectionManager<Role>()

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

    val filmography = actorSelectionManager.selections.switchMap(emptyList()) {
        it?.singleOrNull()?.let { actor -> db.dao.moviesForActorAsync(actor.id) }
    }

    fun addMovie(movie: Movie) = executor.execute {
        db.dao.insert(movie)
    }
    fun addActor(actor: Actor) = executor.execute {
        db.dao.insert(actor)
    }
    fun addRole(role : Role) = executor.execute {
        db.dao.insert(role)
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

    fun deleteMovieById(id: String) {
        executor.execute {
            db.dao.deleteMovie(id)
        }
        movieSelectionManager.clearSelections()
    }
    fun deleteActorById(id: String) {
        executor.execute {
            db.dao.deleteActor(id)
        }
    }
    @Suppress("unused")
    fun deleteRoleById(id: String) {
        executor.execute {
            db.dao.deleteRole(id)
        }
    }
    fun selectMovie(id: String) =
        movieSelectionManager.selectWhenReady(db.dao.getMovieAsync(id))
    fun selectActor(id: String) =
        actorSelectionManager.selectWhenReady(db.dao.getActorAsync(id))
    fun selectRole(id: String) =
        roleSelectionManager.selectWhenReady(db.dao.getRoleAsync(id))

    fun save(movie : Movie) =
        executor.execute {
            db.dao.update(movie)
        }
    fun save(actor : Actor) =
        executor.execute {
            db.dao.update(actor)
        }
    fun save(role : Role) =
        executor.execute {
            db.dao.update(role)
        }
}

