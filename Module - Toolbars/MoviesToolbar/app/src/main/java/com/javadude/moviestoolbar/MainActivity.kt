package com.javadude.moviestoolbar

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MovieViewModel3>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // START NEW FOR TOOLBAR
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        // END NEW FOR TOOLBAR

        val allMovies = findViewById<TextView>(R.id.all_movies)
        val allActors = findViewById<TextView>(R.id.all_actors)
        val name = findViewById<TextView>(R.id.name)
        val title = findViewById<TextView>(R.id.title)
        val description = findViewById<TextView>(R.id.description)
        val filmography = findViewById<TextView>(R.id.filmography)
        val cast = findViewById<TextView>(R.id.cast)

        fun Int.onClick(listener : () -> Unit) {
            findViewById<View>(this).setOnClickListener {
                listener()
            }
        }

        R.id.button_actor_1.onClick {
            viewModel.selectedActorId.value = "a1"
        }
        R.id.button_actor_2.onClick {
            viewModel.selectedActorId.value = "a2"
        }
        R.id.button_movie_1.onClick {
            viewModel.selectedMovieId.value = "m1"
        }
        R.id.button_movie_2.onClick {
            viewModel.selectedMovieId.value = "m2"
        }
        R.id.button_delete_actor_1.onClick {
            viewModel.deleteActor("a1")
        }
        R.id.button_delete_actor_2.onClick {
            viewModel.deleteActor("a2")
        }
        R.id.button_delete_movie_1.onClick {
            viewModel.deleteMovie("m1")
        }
        R.id.button_delete_movie_2.onClick {
            viewModel.deleteMovie("m2")
        }
        R.id.button_delete_movie_3.onClick {
            viewModel.deleteMovie("m3")
        }

        viewModel.message.observe(this) {
            filmography.text = it
        }

        viewModel.allMovies.observe(this) {
            allMovies.text = it?.let {
                it.joinToString("\n") {movie ->
                    movie.title
                }
            } ?: "no movies in database"
        }

        viewModel.allActors.observe(this) {
            allActors.text = it?.let {
                it.joinToString("\n") {actor ->
                    actor.name
                }
            } ?: "no actors in database"
        }

        viewModel.selectedMovie.observe(this) {
            title.text = it?.title ?: "(no movie selected)"
            description.text = it?.description ?: "(no movie selected)"
        }

        viewModel.selectedActor.observe(this) {
            name.text = it?.name ?: "(no actor selected)"
        }

        viewModel.cast2.observe(this) {
            cast.text = it.joinToString("\n") {role ->
                "${role.roleName}: ${role.actor.name}"
            }
        }

        viewModel.filmography.observe(this) {
            filmography.text = it.joinToString("\n") {movie ->
                movie.title
            }
        }
    }


    // START NEW FOR TOOLBAR
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private var nextMovieNumber = 10
    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_create_movie -> {
                viewModel.addMovie(
                    Movie("m$nextMovieNumber", "Movie $nextMovieNumber", "Description $nextMovieNumber"),
                    Role("m$nextMovieNumber", "a1", "Random Hero 1", 1),
                    Role("m$nextMovieNumber", "a2", "Random Hero 2", 2)
                )
                nextMovieNumber++
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
    // END NEW FOR TOOLBAR

}
