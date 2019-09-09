package com.javadude.moviesnav

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView

class MovieListFragment : ListFragment<Movie>(true) {
    override fun startDeleteMode() { startMovieDeleteMode() }
    override fun deleteItemById(id: String) = viewModel.deleteMovieById(id)

    override val getText1 = { movie: Movie -> movie.title }
    override val rowLayoutRes = R.layout.movie
    override val singleSelectAction : () -> Unit = { navigate(R.id.action_display_movie) }
    override val selectionManager : SelectionManager<Movie>
        get() = viewModel.movieSelectionManager
    override val allItems: LiveData<List<Movie>>
        get() = viewModel.allMovies
    override val createNavigationAction = R.id.action_create_movie

    override fun createNewItem() =
        Movie().apply {
            viewModel.addMovie(this)
        }
}

class ActorListFragment : ListFragment<Actor>(true) {
    override fun startDeleteMode() { startActorDeleteMode() }
    override fun deleteItemById(id: String) = viewModel.deleteActorById(id)

    override val getText1 = { actor:Actor -> actor.name }
    override val rowLayoutRes = R.layout.actor
    override val singleSelectAction : () -> Unit = { navigate(R.id.action_display_actor) }
    override val selectionManager : SelectionManager<Actor>
        get() = viewModel.actorSelectionManager
    override val allItems: LiveData<List<Actor>>
        get() = viewModel.allActors
    override val createNavigationAction = R.id.action_create_actor

    override fun createNewItem() =
        Actor().apply {
            viewModel.addActor(this)
        }
}

class RoleInfoListFragment : ListFragment<RoleInfo>(false) {
    override fun startDeleteMode() { startActorDeleteMode() }
    override fun deleteItemById(id: String) = viewModel.deleteRoleById(id)

    override val getText1 = { roleInfo: RoleInfo -> roleInfo.roleName }
    override val getText2 = { roleInfo: RoleInfo -> roleInfo.actorName }
    override val rowLayoutRes = R.layout.cast_entry
    override val singleSelectAction : () -> Unit = { navigate(R.id.action_display_actor) }
    override val selectionManager : SelectionManager<RoleInfo>
        get() = viewModel.roleInfoSelectionManager
    override val allItems: LiveData<List<RoleInfo>>
        get() = viewModel.cast
    override val createNavigationAction = R.id.action_create_actor

    override fun createNewItem() = throw IllegalStateException("Should never be called")
}

abstract class ListFragment<T:HasId>(createAllowed : Boolean)
        : BaseFragment(R.layout.fragment_list, if (createAllowed) R.menu.menu_list else -1) {
    abstract val allItems : LiveData<List<T>>
    abstract val rowLayoutRes : Int
    abstract val selectionManager : SelectionManager<T>
    abstract val singleSelectAction : () -> Unit
    abstract val createNavigationAction : Int
    abstract val getText1 : (T) -> String
    open val getText2 : (T) -> String = {""}
    abstract fun startDeleteMode()
    abstract fun deleteItemById(id : String)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val adapter = GenericAdapter(rowLayoutRes, selectionManager, singleSelectAction, getText1, getText2)

        recyclerView.adapter = adapter

        allItems.observe(this) {
            adapter.items = it ?: emptyList()
        }

        // selection support
        selectionManager.selections.observe(this) {
            adapter.selections = it ?: emptySet()
            invalidateActionMode()
        }

        // adding contextual action mode when multiple items selected
        selectionManager.multiSelectMode.observe(this) {
            if (it == true) { // handles null as false...
                startDeleteMode()
            } else {
                dismissActionMode()
            }
        }

        recyclerView.swipeLeft {
            deleteItemById(it)
        }
    }

    abstract fun createNewItem() : T

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_add -> {
                val newItem = createNewItem()
                selectionManager.selections.value = setOf(newItem)
                navigate(createNavigationAction)
                true
            }
            else ->super.onOptionsItemSelected(item)
        }
}
