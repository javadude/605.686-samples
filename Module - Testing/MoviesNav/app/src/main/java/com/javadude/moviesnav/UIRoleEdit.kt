package com.javadude.moviesnav

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import com.javadude.moviesnav.db.Actor
import com.javadude.moviesnav.db.Role

// Note - this UI includes a nested list, but the list is not used for navigation (only selection)
//        so we don't need to include it in a Navigation interface
//        We do need to implement it though and ignore it
class UIRoleEdit : UI(R.layout.ui_role_edit), UIList.Navigation<Actor> {
    override fun onSingleSelect(item: Actor) {} // NOT navigating! Just choosing an actor
    override fun onCreate(id: String) {} // should never be called - no create option for it

    interface Navigation {
        fun getRoleId() : String?
        fun getMovieId() : String?
    }
    private var currentRole : Role? = null
    private var saved = false

    private val stateFragment : Navigation
        get() = parentFragment as Navigation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        stateFragment.getRoleId()?.let {roleId ->
            viewModel.selectRole(roleId)
            saved = true
        } ?: stateFragment.getMovieId()?.let {movieId ->
//            viewModel.roleSelectionManager.clearSelections()
            viewModel.actorSelectionManager.clearSelections()
            currentRole = Role().apply { this.movieId = movieId }
        } ?: throw IllegalStateException("Either roleId or movieId must be specified")

        val roleName = view.findViewById<EditText>(R.id.role)
        val order = view.findViewById<EditText>(R.id.order)

        roleName.addTextChangedListener(afterTextChanged = {
            currentRole?.let {role ->
                role.roleName = it.toString()
                tryToSaveRole()
            }
        })
        order.addTextChangedListener(afterTextChanged = {
            currentRole?.let {role ->
                role.order = it.toString().toIntOrNull() ?: 1
                tryToSaveRole()
            }
        })

        viewModel.roleSelectionManager.selections.observe(viewLifecycleOwner) {
            currentRole = it?.singleOrNull()
            saved = currentRole?.let {role ->
                roleName.setText(role.roleName)
                order.setText(role.order.toString())
                viewModel.selectActor(role.actorId)
                true
            } ?: false
        }
        viewModel.actorSelectionManager.selections.observe(viewLifecycleOwner) {
            currentRole?.actorId = it?.singleOrNull()?.id ?: ""
            tryToSaveRole()
        }
    }
    private fun tryToSaveRole() {
        currentRole?.let {
            if (it.actorId.isNotEmpty() && it.movieId.isNotEmpty() && it.roleName.isNotEmpty()) {
                if (saved) {
                    viewModel.save(it)
                } else {
                    viewModel.addRole(it)
                    saved = true
                }
            }
        }
    }
}
