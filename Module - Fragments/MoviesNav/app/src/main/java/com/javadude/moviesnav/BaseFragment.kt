package com.javadude.moviesnav


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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

abstract class UI(@LayoutRes layoutId : Int, @MenuRes menuId : Int = -1) : BaseFragment(-1, layoutId, menuId)

abstract class State(
    @StringRes titleId : Int,
    @LayoutRes layoutId : Int) : BaseFragment(titleId, layoutId, -1) {

    open fun doSelect() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        doSelect()
    }
}

abstract class BaseFragment(@StringRes val titleId : Int, @LayoutRes val layoutId : Int, @MenuRes val menuId : Int) : Fragment() {
    lateinit var viewModel : MovieViewModel
    private val mainActivity : MainActivity?
        get() = activity as MainActivity?

    private val menuActions = mutableMapOf<Int, () -> Unit>()
    fun registerMenuHandler(@IdRes menuItemId: Int, handler: () -> Unit) {
        menuActions[menuItemId] = handler
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // NOTE: This view model instance is scoped to the _activity_, not this fragment,
        //       so it can share data with all fragments
        viewModel = ViewModelProvider(context as AppCompatActivity).get(MovieViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(menuId != -1)
    }

    override fun onResume() {
        super.onResume()
        if (titleId != -1) {
            activity?.title = getString(titleId)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if (menuId != -1) {
            inflater.inflate(menuId, menu)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        menuActions[item.itemId]?.let {handler ->
            handler()
            true
        } ?: super.onOptionsItemSelected(item)

    private fun Class<*>.findNavInterface() : Class<*>? =
        declaredClasses.firstOrNull { it.isInterface && it.simpleName == "Navigation" } ?:
                superclass?.findNavInterface()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(layoutId, container, false)

        // run some checks to see if we implement the UI interfaces
        childFragmentManager.fragments.filterIsInstance<UI>().forEach { nestedUI ->
            nestedUI::class.java.findNavInterface()?.let { navInterface ->
                require(navInterface.isAssignableFrom(this::class.java)) { throw IllegalStateException("${nestedUI.javaClass.name} can only be used as a child of a State/UI fragment that implements ${navInterface.name}. Its parent fragment is ${this.javaClass.name}") }
            }
        }
        return view
    }

    fun startMovieDeleteMode() = mainActivity?.startMovieDeleteMode()
    fun startActorDeleteMode() = mainActivity?.startActorDeleteMode()
    fun invalidateActionMode() = mainActivity?.invalidateActionMode()
    fun dismissActionMode() = mainActivity?.dismissActionMode()

    fun navigate(action : NavDirections) { view?.findNavController()?.navigate(action) }

    // adding in "swipe to delete" capability
    // NOTE: activity!! is ok here as onViewCreated() is only called when
    //         we're attached to an Activity
    fun RecyclerView.swipeLeft(action : (String) -> Unit) =
        ItemTouchHelper(Swiper(activity!!, action)).attachToRecyclerView(this)
}
