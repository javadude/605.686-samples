package com.javadude.moviesnav

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.javadude.moviesnav.db.RoleInfo

// NOTE: This UI includes a nested list fragment - we must delegate its navigation!
class UIMovieDisplay : UI(R.layout.ui_movie_display, R.menu.menu_display), UIList.Navigation<RoleInfo> {
    interface Navigation {
        fun onEditMovie()
        fun onSelectActor(id: String)
    }
    private val stateFragment : Navigation
        get() = parentFragment as Navigation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = view.findViewById<TextView>(R.id.title)
        val description = view.findViewById<TextView>(R.id.description)

        viewModel.movieSelectionManager.selections.observe(viewLifecycleOwner) {
            val movie = it?.singleOrNull()
            title.text = movie?.title ?: "(no single movie selected)"
            description.text = movie?.description ?: "(no single movie selected)"
        }
        registerMenuHandler(R.id.action_edit) {
            stateFragment.onEditMovie()
        }
    }

    // delegated functions for the nested cast list
    override fun onSingleSelect(item: RoleInfo) = stateFragment.onSelectActor(item.actorId)
    override fun onCreate(id: String) = throw IllegalStateException("Should not happen")
}
