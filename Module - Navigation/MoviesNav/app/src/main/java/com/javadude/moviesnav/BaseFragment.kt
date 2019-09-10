package com.javadude.moviesnav


import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class BaseFragment(@StringRes val titleId : Int, @LayoutRes val layoutId : Int, @MenuRes val menuId : Int = -1) : Fragment() {
    open val isTopLevelForDestination = true
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
        setHasOptionsMenu(true)
        if (titleId != -1) {
            activity?.title = getString(titleId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (isTopLevelForDestination) { // so we don't add extra "about" icons on the toolbar
                                        //    when we have nested fragments
            inflater.inflate(R.menu.menu_main, menu)
        }
        if (menuId != -1) {
            inflater.inflate(menuId, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_about -> {
                navigate(R.id.action_global_about)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(layoutId, container, false)

    fun startMovieDeleteMode() = mainActivity?.startMovieDeleteMode()
    fun startActorDeleteMode() = mainActivity?.startActorDeleteMode()
    fun invalidateActionMode() = mainActivity?.invalidateActionMode()
    fun dismissActionMode() = mainActivity?.dismissActionMode()

    fun navigate(@IdRes action : Int) = view?.findNavController()?.navigate(action)
    fun navigate(action : NavDirections) = view?.findNavController()?.navigate(action)

    // adding in "swipe to delete" capability
    // NOTE: activity!! is ok here as onViewCreated() is only called when
    //         we're attached to an Activity
    fun RecyclerView.swipeLeft(action : (String) -> Unit) =
        ItemTouchHelper(Swiper(activity!!, action)).attachToRecyclerView(this)


}
