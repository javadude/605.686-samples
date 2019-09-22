package com.javadude.moviesnav.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
abstract class MovieDao {
//    @Query("SELECT * FROM Movie ORDER BY title")
//    abstract fun allMovies() : List<Movie>
//
//    @Query("SELECT * FROM Actor ORDER BY name")
//    abstract fun allActors() : List<Actor>

    @Query("SELECT * FROM Actor WHERE id = :id")
    abstract fun getActorAsync(id : String) : LiveData<Actor>

    @Query("SELECT * FROM Movie WHERE id = :id")
    abstract fun getMovieAsync(id : String) : LiveData<Movie>

    @Query("SELECT * FROM Role WHERE id = :id")
    abstract fun getRoleAsync(id : String) : LiveData<Role>

    @Query("SELECT * FROM Movie ORDER BY title")
    abstract fun allMoviesAsync() : LiveData<List<Movie>>

    @Query("SELECT * FROM Actor ORDER BY name")
    abstract fun allActorsAsync() : LiveData<List<Actor>>

    @Query("SELECT r.id, a.id as actorId, a.name AS actorName, r.roleName, r.`order` FROM Actor a, Role r WHERE r.movieId = :movieId AND r.actorId = a.id ORDER BY r.`order`")
    abstract fun rolesForMovieAsync(movieId : String) : LiveData<List<RoleInfo>>

    @Query("SELECT m.id, m.title, m.description FROM Movie m, Role r WHERE m.id = r.movieId AND r.actorId = :actorId")
    abstract fun moviesForActorAsync(actorId : String) : LiveData<List<Movie>>

    @Insert
    abstract fun insert(movie : Movie)
    @Insert
    abstract fun insert(actor : Actor)
    @Insert
    abstract fun insert(role : Role)
    @Insert
    abstract fun insert(vararg role : Role)

    @Update
    abstract fun update(movie : Movie)
    @Update
    abstract fun update(actor : Actor)
    @Update
    abstract fun update(role : Role)

    @Delete
    abstract fun delete(vararg movie : Movie)
    @Delete
    abstract fun delete(vararg actor : Actor)
    @Delete
    abstract fun delete(vararg role : Role)

    @Query("DELETE FROM Movie WHERE id = :id")
    abstract fun deleteMovie(id : String)
    @Query("DELETE FROM Actor WHERE id = :id")
    abstract fun deleteActor(id : String)
    @Query("DELETE FROM Role WHERE id = :id")
    abstract fun deleteRole(id : String)
}