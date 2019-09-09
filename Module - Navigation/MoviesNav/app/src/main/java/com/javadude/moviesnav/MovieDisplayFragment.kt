package com.javadude.moviesnav

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.Observer


class MovieDisplayFragment : BaseFragment(R.layout.fragment_movie_display) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = view.findViewById<TextView>(R.id.title)
        val description = view.findViewById<TextView>(R.id.description)

        viewModel.selectedMovies.observe(viewLifecycleOwner, Observer {
            val movie = it.singleOrNull()
            title.text = movie?.title ?: "(no single movie selected)"
            description.text = movie?.description ?: "(no single movie selected)"
        })
    }
}
