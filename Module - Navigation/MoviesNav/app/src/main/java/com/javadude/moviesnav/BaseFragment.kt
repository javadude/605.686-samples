package com.javadude.moviesnav


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class BaseFragment(@LayoutRes val layoutId : Int, @MenuRes val menuId : Int = -1) : Fragment() {
    lateinit var viewModel : MovieViewModel
    private val mainActivity : MainActivity?
        get() = activity as MainActivity?

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // NOTE: This view model instance is scoped to the _activity_, not this fragment,
        //       so it can share data with all fragments
        viewModel = ViewModelProvider(context as AppCompatActivity).get(MovieViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (menuId != -1) {
            setHasOptionsMenu(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (menuId != -1) {
            inflater.inflate(menuId, menu)
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(layoutId, container, false)

    fun startMovieDeleteMode() = mainActivity?.startMovieDeleteMode()
    fun startActorDeleteMode() = mainActivity?.startActorDeleteMode()
    fun invalidateActionMode() = mainActivity?.invalidateActionMode()
    fun dismissActionMode() = mainActivity?.dismissActionMode()

    fun navigate(@IdRes action : Int) = view?.findNavController()?.navigate(action)

    // adding in "swipe to delete" capability
    // NOTE: activity!! is ok here as onViewCreated() is only called when
    //         we're attached to an Activity
    fun RecyclerView.swipeLeft(action : (String) -> Unit) =
        ItemTouchHelper(Swiper(activity!!, action)).attachToRecyclerView(this)


}
