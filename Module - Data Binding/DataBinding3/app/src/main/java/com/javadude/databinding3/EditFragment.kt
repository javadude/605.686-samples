package com.javadude.databinding3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.navGraphViewModels
import com.javadude.databinding3.databinding.FragmentEditBinding

/**
 * A Fragment to edit a contact. Nice and small...
 */
class EditFragment : Fragment() {
    // NOTE: This is a fairly new kotlin extension that lazily performs view model lookup
    //       In this case, it's looking up a view model that's scoped to the navigation
    //       graph, so all destinations in the nav graph share the same view model
    private val viewModel: ContactViewModel by navGraphViewModels(R.id.nav_graph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentEditBinding.inflate(inflater, container, false)

        // passing in variables - in this case, just the view model
        binding.model = viewModel

        // NOTE: BE SURE TO ADD THIS LINE IF DATABINDING EXPRESSIONS WILL TOUCH LIVE DATA!
        //       Otherwise, the databinding will not show value changes
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}