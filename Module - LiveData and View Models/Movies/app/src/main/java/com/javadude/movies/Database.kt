package com.javadude.movies

import androidx.lifecycle.MutableLiveData

data class Movie(
    val id : String,
    val title : String,
    val description : String)

data class Actor(
    val id : String,
    val name : String
)

data class Role(
    val movieId : String,
    val actorId : String,
    val roleName : String,
    val order : Int
)

object Database {
    private val movies = mapOf(
        "m1" to Movie("m1", "The Transporter", "Jason Statham kicks a guy in the face"),
        "m2" to Movie("m2", "Transporter 2", "Jason Statham kicks a bunch of guys in the face"),
        "m3" to Movie("m3", "Hobbs and Shaw", "Cars, Explosions and Stuff"),
        "m4" to Movie("m4", "Jumanji", "The Rock smolders")
    )

    private val actors = mapOf(
        "a1" to Actor("a1", "Jason Statham"),
        "a2" to Actor("a2", "The Rock"),
        "a3" to Actor("a3", "Shu Qi"),
        "a4" to Actor("a4", "Amber Valletta"),
        "a5" to Actor("a5", "Kevin Hart")
    )

    private val roles = mapOf(
        "m1" to listOf(
            Role("m1", "a1", "Frank Martin", 1),
            Role("m1", "a3", "Lai", 2)
        ),
        "m2" to listOf(
            Role("m2", "a1", "Frank Martin", 1),
            Role("m2", "a4", "Audrey Billings", 2)
        ),
        "m3" to listOf(
            Role("m3", "a2", "Hobbs", 1),
            Role("m3", "a1", "Shaw", 2)
        ),
        "m4" to listOf(
            Role("m4", "a2", "Spencer", 1),
            Role("m4", "a5", "Fridge", 2)
        )
    )

    fun allMovies() = movies.values.sortedBy { it.title }
    fun allActors() = actors.values.sortedBy { it.name }
    fun rolesForMovie(movieId : String) = roles[movieId] ?: emptyList()
    fun moviesForActor(actorId : String) = roles.filter { it.value.any { it.actorId == actorId } }.mapNotNull { movies[it.key] }

    fun getActor(id : String) = actors[id]
    fun getMovie(id : String) = movies[id]

    fun allMoviesAsync() = MutableLiveData<List<Movie>>().apply {
        executor.execute {
            postValue(allMovies())
        }
    }

    fun allActorsAsync() = MutableLiveData<List<Actor>>().apply {
        executor.execute {
            postValue(allActors())
        }
    }

    fun rolesForMovieAsync(movieId : String) = MutableLiveData<List<Role>>().apply {
        executor.execute {
            postValue(rolesForMovie(movieId))
        }
    }

    fun moviesForActorAsync(actorId : String) = MutableLiveData<List<Movie>>().apply {
        executor.execute {
            postValue(moviesForActor(actorId))
        }
    }
}

