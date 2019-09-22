package com.javadude.moviesnav

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.addTextChangedListener
import com.javadude.moviesnav.db.Movie
import com.javadude.moviesnav.db.RoleInfo


// NOTE: This UI includes a nested list fragment - we must delegate its navigation!
class UIMovieEdit : UI(R.layout.ui_movie_edit), UIList.Navigation<RoleInfo> {
    interface Navigation {
        fun onEditRole(roleId: String?, movieId: String?)
    }
    private var currentMovie : Movie? = null

    private val stateFragment : Navigation
        get() = parentFragment as Navigation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                stateFragment.onEditRole(null, it.id) // tell it we're creating a new role
            }
        }

        viewModel.movieSelectionManager.selections.observe(viewLifecycleOwner) {
            currentMovie = it?.singleOrNull()
            title.setText(currentMovie?.title ?: "(no single movie selected)")
            description.setText(currentMovie?.description ?: "(no single movie selected)")
        }
    }
    // delegated functions for the nested cast list
    override fun onSingleSelect(item : RoleInfo) = stateFragment.onEditRole(item.id, null)
    override fun onCreate(id: String) = throw IllegalStateException("Should not get called")
        // we have an explicit "add" button above the nested list, not using the menu
}
