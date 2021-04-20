package com.javadude.moviesnav

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.navArgs


class MovieEditFragment : BaseFragment(R.string.movie, R.layout.fragment_movie_edit) {
    private val args : MovieEditFragmentArgs by navArgs()
    private var currentMovie : Movie? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectMovie(args.movieId)

        val title = view.findViewById<EditText>(R.id.title)
        val description = view.findViewById<EditText>(R.id.description)
        val addButton = view.findViewById<ImageView>(R.id.add_button)

        title.addTextChangedListener(afterTextChanged = {
            currentMovie?.let {movie ->
                movie.title = it.toString()
                viewModel.save(movie)
            }
        })
        description.addTextChangedListener(afterTextChanged = {
            currentMovie?.let {movie ->
                movie.description = it.toString()
                viewModel.save(movie)
            }
        })
        addButton.setOnClickListener {
            currentMovie?.let {
                navigate(MovieEditFragmentDirections.actionEditRole(null, it.id)) // tell it we're creating a new role
            }
        }

        viewModel.movieSelectionManager.selections.observe(viewLifecycleOwner) {
            currentMovie = it?.singleOrNull()
            title.setText(currentMovie?.title ?: "(no single movie selected)")
            description.setText(currentMovie?.description ?: "(no single movie selected)")
        }
    }
}
