package com.javadude.moviesnav

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.annotation.MenuRes
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.navigation.NavDirections
import androidx.recyclerview.widget.RecyclerView

class MovieListFragment : ListFragment<Movie>(R.string.movies, true, true, R.menu.menu_movie_list) {
    override fun startDeleteMode() { startMovieDeleteMode() }
    override fun deleteItemById(id: String) = viewModel.deleteMovieById(id)
    override fun getText1(item: Movie) = item.title
    override val rowLayoutRes = R.layout.movie
    override fun getSingleSelectNavigation(item : Movie) = MovieListFragmentDirections.actionDisplayMovie(item.id)
    override fun getSelectionManager() = viewModel.movieSelectionManager
    override fun getAllItems() = viewModel.allMovies
    override fun getCreationNavigation(id: String) = MovieListFragmentDirections.actionCreateMovie(id)
    override fun createNewItem() =
        Movie().apply {
            viewModel.addMovie(this)
        }

    override fun onOptionsItemSelected(item: MenuItem) =
        when(item.itemId) {
            R.id.action_view_all_actors -> {
                navigate(MovieListFragmentDirections.actionViewAllActors())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}

class FilmographyListFragment : ListFragment<Movie>(-1, false, true) {
    override fun startDeleteMode() = throw IllegalStateException("Should not be called")
    override fun deleteItemById(id: String) = throw IllegalStateException("Should not be called")
    override fun getText1(item: Movie) = item.title
    override val rowLayoutRes = R.layout.movie
    override fun getSingleSelectNavigation(item : Movie) = ActorDisplayFragmentDirections.actionDisplayMovie(item.id)
    override fun getSelectionManager() = viewModel.movieSelectionManager
    override fun getAllItems() = viewModel.filmography
    override fun getCreationNavigation(id: String) = throw IllegalStateException("should never be called")
    override fun createNewItem() = throw IllegalStateException("Should never be called")
}

class MultiSelectActorListFragment : ActorListFragment(true, R.menu.menu_actor_list) {
    override fun getSingleSelectNavigation(item : Actor) = MultiSelectActorListFragmentDirections.actionDisplayActor(item.id)
    override fun getCreationNavigation(id: String) = MultiSelectActorListFragmentDirections.actionCreateActor(id)
}
class SingleSelectActorListFragment : ActorListFragment(false) {
    override fun getSingleSelectNavigation(item : Actor) : NavDirections? = null
    override fun getCreationNavigation(id: String) = throw IllegalStateException("Should never happen")
}

abstract class ActorListFragment(multiSelectAllowed : Boolean, @MenuRes menuId : Int = -1) : ListFragment<Actor>(R.string.actors, true, multiSelectAllowed, menuId) {
    override fun startDeleteMode() { startActorDeleteMode() }
    override fun deleteItemById(id: String) = viewModel.deleteActorById(id)
    override fun getText1(item:Actor) = item.name
    override val rowLayoutRes = R.layout.actor
    override fun getSelectionManager() = viewModel.actorSelectionManager
    override fun getAllItems() = viewModel.allActors
    override fun createNewItem() =
        Actor().apply {
            viewModel.addActor(this)
        }
    override fun onOptionsItemSelected(item: MenuItem) =
        when(item.itemId) {
            R.id.action_view_all_movies -> {
                navigate(MultiSelectActorListFragmentDirections.actionViewAllMovies())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
}

class MovieDisplayRoleInfoListFragment : RoleInfoListFragment() {
    override fun getSingleSelectNavigation(item : RoleInfo) = MovieDisplayFragmentDirections.actionDisplayActor(item.actorId)
}
class MovieEditRoleInfoListFragment : RoleInfoListFragment() {
    override fun getSingleSelectNavigation(item : RoleInfo) = MovieEditFragmentDirections.actionEditRole(item.id, viewModel.movieSelectionManager.selections.value?.singleOrNull()?.id)
}

abstract class RoleInfoListFragment : ListFragment<RoleInfo>(-1, false, true) {
    override fun startDeleteMode() { startActorDeleteMode() }
    override fun deleteItemById(id: String) = viewModel.deleteRoleById(id)
    override fun getText1(item: RoleInfo) = item.roleName
    override fun getText2(item: RoleInfo) = item.actorName
    override val rowLayoutRes = R.layout.cast_entry
    override fun getSelectionManager() = viewModel.roleInfoSelectionManager
    override fun getAllItems() = viewModel.cast
    override fun getCreationNavigation(id: String) = throw IllegalStateException("should never be called")
    override fun createNewItem() = throw IllegalStateException("Should never be called")
}

abstract class ListFragment<T:HasId>(@StringRes titleId : Int,
                                     private val deleteAllowed : Boolean,
                                     private val multiSelectAllowed : Boolean,
                                     @MenuRes menuId : Int = -1)
        : BaseFragment(titleId, R.layout.fragment_list, menuId) {
    abstract val rowLayoutRes : Int
    abstract fun getAllItems() : LiveData<List<T>>
    abstract fun getSelectionManager() : SelectionManager<T>
    abstract fun getText1(item : T) : String
    open fun getText2(item : T) = ""
    abstract fun getSingleSelectNavigation(item : T) : NavDirections?
    abstract fun createNewItem() : T
    abstract fun getCreationNavigation(id : String) : NavDirections
    abstract fun startDeleteMode()
    abstract fun deleteItemById(id : String)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val adapter = GenericAdapter(rowLayoutRes, multiSelectAllowed, getSelectionManager(), { getSingleSelectNavigation(it)?.let {dir -> navigate(dir)} }, ::getText1, ::getText2)

        recyclerView.adapter = adapter

        getAllItems().observe(this) {
            adapter.items = it ?: emptyList()
        }

        // selection support
        getSelectionManager().selections.observe(this) {
            adapter.selections = it ?: emptySet()
            if (deleteAllowed) {
                invalidateActionMode()
            }
        }

        // adding contextual action mode when multiple items selected
        if (deleteAllowed) {
            getSelectionManager().multiSelectMode.observe(this) {
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
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_add -> {
                val newItem = createNewItem()
                getSelectionManager().selections.value = setOf(newItem)
                navigate(getCreationNavigation(newItem.id))
                true
            }
            else ->super.onOptionsItemSelected(item)
        }

}
