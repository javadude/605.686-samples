package com.javadude.databinding3

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.javadude.databinding3.databinding.FragmentListBinding


/**
 * A Fragment to display a list of contacts... Not quite as small as [EditFragment], but close
 *
 * We needed to add a [ListModel] class in here to pass in the "on click" function for items
 * in the [BindingRecyclerView] because we don't manage selections in there. If the
 * [BindingRecyclerView] managed selections, we would only need some code to trigger navigation.
 */
class ListFragment : Fragment() {
    // NOTE: This is a fairly new kotlin extension that lazily performs view model lookup
    //       In this case, it's looking up a view model that's scoped to the navigation
    //       graph, so all destinations in the nav graph share the same view model
    private val viewModel: ContactViewModel by navGraphViewModels(R.id.nav_graph)

    inner class ListModel(val viewModel: ContactViewModel) {
        fun onPersonClicked(person: Person) {
            // NOTE - I'd rather have the BindingRecyclerView manage the selection
            //        (similar to the Spinner), but I also wanted to demonstrate calling a
            //        function from the layout XML and didn't want to cloud the BindingRecyclerView
            //        with a lot of selection processing
            //        This means that handling selections in this example is up to the row_layout
            //        passed into the BindingRecyclerView
            viewModel.selectedPerson.value = person
            findNavController().navigate(R.id.action_edit)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentListBinding.inflate(inflater, container, false)

        // passing in variables - in this case, the list model, which contains the view model
        //    and an on-click handler
        binding.model = ListModel(viewModel)

        // NOTE: BE SURE TO ADD THIS LINE IF DATABINDING EXPRESSIONS WILL TOUCH LIVE DATA!
        //       Otherwise, the databinding will not show value changes
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}