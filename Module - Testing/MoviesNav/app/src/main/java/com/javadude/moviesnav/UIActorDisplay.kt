package com.javadude.moviesnav

import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.javadude.moviesnav.db.Movie

// NOTE: This UI includes a nested list fragment - we must delegate its navigation!
class UIActorDisplay : UI(R.layout.ui_actor_display, R.menu.menu_display), UIList.Navigation<Movie> {
    interface Navigation {
        fun onEditActor()
        fun onSelectMovie(id: String)
    }
    private val stateFragment : Navigation
        get() = parentFragment as Navigation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val name = view.findViewById<TextView>(R.id.name)

        viewModel.actorSelectionManager.selections.observe(viewLifecycleOwner) {
            val actor = it?.singleOrNull()
            name.text = actor?.name ?: "(no single actor selected)"
        }

        registerMenuHandler(R.id.action_edit) {
            stateFragment.onEditActor()
        }
    }

    // delegated functions for the nested movie list
    override fun onSingleSelect(item: Movie) = stateFragment.onSelectMovie(item.id)
    override fun onCreate(id: String) = throw IllegalStateException("Should not happen")
}
