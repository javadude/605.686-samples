package com.javadude.moviesdb

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MovieViewModel3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MovieViewModel3::class.java)

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

        viewModel.message.observe(this, Observer {
            filmography.text = it
        })

        viewModel.allMovies.observe(this, Observer {
            allMovies.text = it?.let {
                it.joinToString("\n") {movie ->
                    movie.title
                }
            } ?: "no movies in database"
        })

        viewModel.allActors.observe(this, Observer {
            allActors.text = it?.let {
                it.joinToString("\n") {actor ->
                    actor.name
                }
            } ?: "no actors in database"
        })

        viewModel.selectedMovie.observe(this, Observer {
            title.text = it?.title ?: "(no movie selected)"
            description.text = it?.description ?: "(no movie selected)"
        })

        viewModel.selectedActor.observe(this, Observer {
            name.text = it?.name ?: "(no actor selected)"
        })

        viewModel.cast2.observe(this, Observer {
            cast.text = it.joinToString("\n") {role ->
                "${role.roleName}: ${role.actor.name}"
            }
        })

        viewModel.filmography.observe(this, Observer {
            filmography.text = it.joinToString("\n") {movie ->
                movie.title
            }
        })
    }
}
