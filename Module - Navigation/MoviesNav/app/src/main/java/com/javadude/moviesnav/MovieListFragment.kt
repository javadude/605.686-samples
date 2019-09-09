package com.javadude.moviesnav

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView

class MovieListFragment : BaseFragment(R.layout.fragment_movie_list) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val movieList = view.findViewById<RecyclerView>(R.id.movie_list)

        val adapter = MovieAdapter2(
            onClicked = {
                viewModel.onClicked(it, singleSelectAction = {
                    navigate(R.id.action_display_movie)
                })
            },
            onIconClicked = { viewModel.onIconClicked(it) },
            onLongClicked = { viewModel.onLongClicked(it) }
        )

        movieList.adapter = adapter

        viewModel.allMovies.observe(this, Observer {
            adapter.movies = it ?: emptyList()
        })

        // selection support
        viewModel.selectedMovies.observe(this, Observer {
            adapter.selectedMovies = it ?: emptySet()
            invalidateActionMode()
        })

        // adding contextual action mode when multiple items selected
        viewModel.multiMovieSelectMode.observe(this, Observer {
            if (it == true) { // handles null as false...
                startMovieDeleteMode()
            } else {
                dismissActionMode()
            }
        })

        movieList.swipeLeft {
            viewModel.deleteMovieAt(it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
        inflater.inflate(R.menu.menu_main, menu)

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_create_movie -> {
                val newMovie = Movie()
                viewModel.addMovie(newMovie)
                viewModel.selectedMovies.value = setOf(newMovie)
                navigate(R.id.action_create_movie)
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
}
