package com.javadude.moviesnav

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.navArgs

class RoleEditFragment : BaseFragment(R.string.role, R.layout.fragment_role_edit) {
    private val args : RoleEditFragmentArgs by navArgs()
    private var currentRole : Role? = null
    private var saved = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val roleId = args.roleId
        if (roleId != null) {
            viewModel.selectRole(roleId)
            saved = true
        } else {
            val movieId = args.movieId ?: throw IllegalStateException("Either roleId or movieId must be specified")
            viewModel.actorSelectionManager.clearSelections()
            currentRole = Role().apply { this.movieId = movieId }
        }

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
            currentRole?.let {role ->
                roleName.setText(role.roleName)
                order.setText(role.order.toString())
                viewModel.selectActor(role.actorId)
            }
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
