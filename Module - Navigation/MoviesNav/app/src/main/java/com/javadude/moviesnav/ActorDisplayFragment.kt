package com.javadude.moviesnav

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.navArgs

class ActorDisplayFragment : BaseFragment(R.string.actor, R.layout.fragment_actor_display, R.menu.menu_display) {
    private val args : ActorDisplayFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectActor(args.actorId)

        val name = view.findViewById<TextView>(R.id.name)

        viewModel.actorSelectionManager.selections.observe(viewLifecycleOwner) {
            val actor = it?.singleOrNull()
            name.text = actor?.name ?: "(no single actor selected)"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when(item.itemId) {
            R.id.action_edit -> {
                navigate(ActorDisplayFragmentDirections.actionEditActor(args.actorId))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}
