package com.javadude.movies

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MovieViewModel>()
        // newer way to access view model; requires activity KTX dependency
    private lateinit var cast: TextView
    private lateinit var filmography: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val allMovies = findViewById<TextView>(R.id.all_movies)
        val allActors = findViewById<TextView>(R.id.all_actors)
        val name = findViewById<TextView>(R.id.name)
        val title = findViewById<TextView>(R.id.title)
        val description = findViewById<TextView>(R.id.description)
        val actor1Button = findViewById<Button>(R.id.button_actor_1)
        val actor2Button = findViewById<Button>(R.id.button_actor_2)
        val movie1Button = findViewById<Button>(R.id.button_movie_1)
        val movie2Button = findViewById<Button>(R.id.button_movie_2)
        cast = findViewById(R.id.cast)
        filmography = findViewById(R.id.filmography)

        actor1Button.setOnClickListener {
            executor.execute {
                viewModel.selectedActor.postValue(Database.getActor("a1"))
            }
        }
        actor2Button.setOnClickListener {
            executor.execute {
                viewModel.selectedActor.postValue(Database.getActor("a2"))
            }
        }
        movie1Button.setOnClickListener {
            executor.execute {
                viewModel.selectedMovie.postValue(Database.getMovie("m1"))
            }
        }
        movie2Button.setOnClickListener {
            executor.execute {
                viewModel.selectedMovie.postValue(Database.getMovie("m2"))
            }
        }

        viewModel.allMovies.observe(this, Observer {
            allMovies.text = it?.let {
                it.joinToString("\n") {
                    it.title
                }
            } ?: "no movies in database"
        })

        viewModel.allActors.observe(this, Observer {
            allActors.text = it?.let {
                it.joinToString("\n") {
                    it.name
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

        viewModel.resolvedCast.observe(this, Observer {
            cast.text = it.joinToString("\n") {
                val (actor, role) = it
                "$role: $actor"
            }
        })

        viewModel.filmography.observe(this, Observer {
            filmography.text = it.joinToString("\n") {
                it.title
            }
        })
    }
}
