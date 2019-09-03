package com.javadude.movies

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import java.util.concurrent.Executors

val executor = Executors.newSingleThreadExecutor()

class MovieViewModel(application: Application) : AndroidViewModel(application) {
    val allMovies = MutableLiveData<List<Movie>>().apply {
        executor.execute {
            postValue(Database.allMovies())
        }
    }
    val allActors = MutableLiveData<List<Actor>>().apply {
        executor.execute {
            postValue(Database.allActors())
        }
    }

    val selectedMovie = MutableLiveData<Movie>().apply { value = null }
    val selectedActor = MutableLiveData<Actor>().apply { value = null }

    private val cast = MediatorLiveData<List<Role>>().apply {
        value = emptyList()
        addSource(selectedMovie) {
            it?.let {
                executor.execute {
                    postValue(Database.rolesForMovie(it.id))
                }
            } ?: run {
                value = emptyList()
            }
        }
    }

    val resolvedCast = MediatorLiveData<List<Pair<String, String>>>().apply {
        addSource(cast) {
            executor.execute {
                postValue(it.map {
                    Pair(Database.getActor(it.actorId)?.name ?: "(actor ${it.actorId} not found)", it.roleName)
                })
            }
        }
    }

    val filmography = MediatorLiveData<List<Movie>>().apply {
        addSource(selectedActor) {
            it?.let {
                executor.execute {
                    postValue(Database.moviesForActor(it.id))
                }
            } ?: run {
                value = emptyList()
            }
        }
    }
}