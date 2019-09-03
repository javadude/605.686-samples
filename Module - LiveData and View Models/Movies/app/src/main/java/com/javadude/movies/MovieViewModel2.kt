package com.javadude.movies

import android.app.Application
import androidx.lifecycle.*


//fun <X, Y> switchMap(source : LiveData<X>, defaultValue : Y, function : (X?) -> LiveData<Y>?) =
//    Transformations.switchMap(source) {
//        function(it) ?: MutableLiveData<Y>().apply { this.value = defaultValue }
//    }

fun <X, Y> LiveData<X>.switchMap(defaultValue : Y, function : (X?) -> LiveData<Y>?): LiveData<Y> =
    Transformations.switchMap(this) {
        function(it) ?: MutableLiveData<Y>().apply { this.value = defaultValue }
    }

class MovieViewModel2(application: Application) : AndroidViewModel(application) {
    val allMovies = Database.allMoviesAsync()
    val allActors = Database.allActorsAsync()

    val selectedMovie = MutableLiveData<Movie>().apply { value = null }
    val selectedActor = MutableLiveData<Actor>().apply { value = null }

    private val cast = selectedMovie.switchMap(emptyList()) {
        it?.let { Database.rolesForMovieAsync(it.id) }
    }

//    private val cast = switchMap(selectedMovie, emptyList()) {
//        it?.let { Database.rolesForMovieAsync(it.id) }
//    }

//    private val castOld = Transformations.switchMap(selectedMovie) {
//        it?.let {
//            Database.rolesForMovieAsync(it.id)
//        } ?: MutableLiveData<List<Role>>().apply { value = emptyList() }
//    }

    val resolvedCast = MediatorLiveData<List<Pair<String, String>>>().apply {
        addSource(cast) {
            executor.execute {
                postValue(it.map {
                    Pair(Database.getActor(it.actorId)?.name ?: "(actor ${it.actorId} not found)", it.roleName)
                })
            }
        }
    }

    val filmography = selectedActor.switchMap(emptyList()) {
        it?.let { Database.moviesForActorAsync(it.id) }
    }

//    val filmography = switchMap(selectedActor, emptyList()) {
//        it?.let { Database.moviesForActorAsync(it.id) }
//    }
//
//    val filmographyOld = Transformations.switchMap(selectedActor) {
//        it?.let {
//            Database.moviesForActorAsync(it.id)
//        } ?: MutableLiveData<List<Movie>>().apply { value = emptyList() }
//    }
}