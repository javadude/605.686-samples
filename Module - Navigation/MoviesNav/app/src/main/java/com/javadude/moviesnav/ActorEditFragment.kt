package com.javadude.moviesnav

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.navArgs

class ActorEditFragment : BaseFragment(R.string.actor, R.layout.fragment_actor_edit) {
    private val args : ActorEditFragmentArgs by navArgs()
    private var currentActor : Actor? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.selectActor(args.actorId)

        val name = view.findViewById<EditText>(R.id.name)
        name.addTextChangedListener(afterTextChanged = {
            currentActor?.let { actor ->
                actor.name = it.toString()
                viewModel.save(actor)
            }
        })

        viewModel.actorSelectionManager.selections.observe(viewLifecycleOwner) {
            currentActor = it?.singleOrNull()
            name.setText(currentActor?.name ?: "(no single actor selected)")
        }
    }
}
