package com.javadude.moviesnav

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.javadude.moviesnav.db.Actor

class UIActorEdit : UI(R.layout.ui_actor_edit) {
    private var currentActor : Actor? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
