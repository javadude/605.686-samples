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

        val adapter = GenericAdapter<Movie>(
            rowLayoutRes = R.layout.movie,
            onClicked = {
                viewModel.movieSelectionManager.onClicked(it, singleSelectAction = {
                    navigate(R.id.action_display_movie)
                })
            },
            onLongClicked = { viewModel.movieSelectionManager.onLongClicked(it) },
            getText1 = { it.title }
        )

        movieList.adapter = adapter

        viewModel.allMovies.observe(this) {
            adapter.items = it ?: emptyList()
        }

        // selection support
        viewModel.movieSelectionManager.selections.observe(this) {
            adapter.selections = it ?: emptySet()
            invalidateActionMode()
        }

        // adding contextual action mode when multiple items selected
        viewModel.movieSelectionManager.multiSelectMode.observe(this) {
            if (it == true) { // handles null as false...
                startMovieDeleteMode()
            } else {
                dismissActionMode()
            }
        }

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
                viewModel.movieSelectionManager.selections.value = setOf(newMovie)
                navigate(R.id.action_create_movie)
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
}
