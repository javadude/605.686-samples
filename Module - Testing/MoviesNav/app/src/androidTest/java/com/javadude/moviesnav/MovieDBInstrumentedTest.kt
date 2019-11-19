package com.javadude.moviesnav

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.javadude.moviesnav.db.Actor
import com.javadude.moviesnav.db.Database
import com.javadude.moviesnav.db.Movie
import com.javadude.moviesnav.db.Role
import com.javadude.moviesnav.db.RoleInfo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executor

/**
 * Instrumented test, which will execute on an Android device.
 * NOTE: Everything in this file is run in the test app; there is no communication with a
 * real application! For these tests, we set up an in-memory database because we're creating
 * instances of the DB and view model in the test application itself, not in the actual application
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@LargeTest
@RunWith(AndroidJUnit4::class)
class MovieDBInstrumentedTest {
    // tells android arch components to use the current thread to run background tasks
    // this is useful to test LiveData, as each modification will be immediately
    //   followed by the observer call so we don't have to play waiting games between threads...
    @get:Rule val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private lateinit var db : Database
    private lateinit var viewModel: MovieViewModel

    // note - method names in instrumented tests must be "normal"; cannot have embedded blanks like local tests

    @Before
    fun setupDatabase() {
        ServiceLocator.db = Room.inMemoryDatabaseBuilder(context, Database::class.java).build()
        ServiceLocator.executor = Executor { it.run() } // run on same thread
        db = ServiceLocator.db
        viewModel = MovieViewModel()
    }

    // testing the database by itself. this is easiest with synchronous functions
    //   (ones that don't return livedata; livedata example is farther down...)
    @Test fun checkStuff() {
        assertEquals(emptyList<Movie>(), db.dao.allMoviesSync())
        assertEquals(emptyList<Actor>(), db.dao.allActorsSync())
        assertEquals(emptyList<Role>(), db.dao.allRolesSync())

        val movies = (0..2).map { Movie("m$it", "Movie $it","Description $it") }
        val actors = (0..5).map { Actor("a$it", "Actor $it") }
        val roles = (0..2).flatMap { movieId ->
            (movieId*2..movieId*2+1).map {actorId ->
                Role("r$movieId$actorId", "m$movieId", "a$actorId", "Role m$movieId a$actorId", actorId-(movieId*2))
            }
        }
        val roleInfos = roles.map { RoleInfo(it.id, it.actorId, "Actor ${it.actorId.substring(1)}", it.roleName, it.order) }

        db.dao.insert(movies[1])
        assertEquals(listOf(movies[1]), db.dao.allMoviesSync())

        db.dao.insert(movies[2])
        assertEquals(listOf(movies[1], movies[2]), db.dao.allMoviesSync())

        db.dao.insert(movies[0])
        assertEquals(listOf(movies[0], movies[1], movies[2]), db.dao.allMoviesSync())

        actors.forEachIndexed { index, actor ->
            db.dao.insert(actor)
            assertEquals(actors.subList(0, index+1), db.dao.allActorsSync())
        }
        roles.forEachIndexed { index, role ->
            db.dao.insert(role)
            assertEquals(roles.subList(0, index+1), db.dao.allRolesSync())
        }

        assertEquals(listOf(roleInfos[0], roleInfos[1]), db.dao.rolesForMovieSync(movies[0].id))
    }

    @Test fun checkLiveData() {
        val m1 = Movie("m1", "Sample Movie", "A lovely sample movie")
        val m2 = Movie("m2", "Another Movie", "I want my 90 minutes back")

        var stage = 1
        val observer = Observer<List<Movie>> {
            when (stage) {
                1 -> {
                    assertEquals(emptyList<Movie>(), db.dao.allMoviesSync())
                    println("!!!OBSERVER 1 DONE!!!")
                }
                2 -> {
                    assertEquals(listOf(m1), db.dao.allMoviesSync())
                    println("!!!OBSERVER 2 DONE!!!")
                }
                3 -> {
                    assertEquals(listOf(m2, m1), db.dao.allMoviesSync())
                    println("!!!OBSERVER 3 DONE!!!")
                }
            }
        }
        try {
            viewModel.allMovies.observeForever(observer)

            stage = 2
            db.dao.insert(m1)

            stage = 3
            db.dao.insert(m2)

            println("!!!AFTER INSERTS!!!")
            assertEquals(listOf(m2, m1), db.dao.allMoviesSync())
            println("!!!AFTER ASSERT!!!")

        } finally {
            viewModel.allMovies.removeObserver(observer)
        }
    }
}
