package com.javadude.moviesnav

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.navArgs


class MovieDisplayFragment : BaseFragment(R.string.movie, R.layout.fragment_movie_display, R.menu.menu_display) {
    private val args : MovieDisplayFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectMovie(args.movieId)

        val title = view.findViewById<TextView>(R.id.title)
        val description = view.findViewById<TextView>(R.id.description)

        viewModel.movieSelectionManager.selections.observe(viewLifecycleOwner) {
            val movie = it?.singleOrNull()
            title.text = movie?.title ?: "(no single movie selected)"
            description.text = movie?.description ?: "(no single movie selected)"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when(item.itemId) {
            R.id.action_edit -> {
                navigate(MovieDisplayFragmentDirections.actionEditMovie(args.movieId))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
