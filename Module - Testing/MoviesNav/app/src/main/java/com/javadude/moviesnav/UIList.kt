package com.javadude.moviesnav

import android.os.Bundle
import android.view.View
import androidx.annotation.MenuRes
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.javadude.moviesnav.db.Actor
import com.javadude.moviesnav.db.HasId
import com.javadude.moviesnav.db.Movie
import com.javadude.moviesnav.db.RoleInfo

class UIMovieList : UIList<Movie>(true, true, R.menu.menu_movie_list) {
    interface Navigation : UIList.Navigation<Movie> {
        fun onViewAllActors()
    }
    override fun startDeleteMode() { startMovieDeleteMode() }
    override fun deleteItemById(id: String) = viewModel.deleteMovieById(id)
    override fun getText1(item: Movie) = item.title
    override val rowLayoutRes = R.layout.item_movie
    override val selectionManager get() = viewModel.movieSelectionManager
    override val allItems get() = viewModel.allMovies
    override fun createNewItem() = Movie().apply { viewModel.addMovie(this) }

    private val stateFragment : Navigation
        get() = parentFragment as Navigation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerMenuHandler(R.id.action_view_all_actors) {
            stateFragment.onViewAllActors()
        }
    }
}

class UIFilmographyList : UIList<Movie>(false, true) {
    override fun getText1(item: Movie) = item.title
    override val rowLayoutRes = R.layout.item_movie
    override val selectionManager get() = viewModel.movieSelectionManager
    override val allItems get() = viewModel.filmography
}

class UIMultiSelectActorList : UIActorList(true, R.menu.menu_actor_list) {
    interface Navigation : UIList.Navigation<Actor> {
        fun onViewAllMovies()
    }
    private val stateFragment : Navigation
        get() = parentFragment as Navigation

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registerMenuHandler(R.id.action_view_all_movies) {
            stateFragment.onViewAllMovies()
        }
    }
}
class UISingleSelectActorList : UIActorList(false)

abstract class UIActorList(multiSelectAllowed : Boolean, @MenuRes menuId : Int = -1) : UIList<Actor>(true, multiSelectAllowed, menuId) {
    override fun startDeleteMode() { startActorDeleteMode() }
    override fun deleteItemById(id: String) = viewModel.deleteActorById(id)
    override fun getText1(item: Actor) = item.name
    override val rowLayoutRes = R.layout.item_actor
    override val selectionManager get() = viewModel.actorSelectionManager
    override val allItems get() = viewModel.allActors
    override fun createNewItem() = Actor().apply { viewModel.addActor(this) }
}

class UIRoleInfoList : UIList<RoleInfo>(false, true) {
    override fun getText1(item: RoleInfo) = item.roleName
    override fun getText2(item: RoleInfo) = item.actorName
    override val rowLayoutRes = R.layout.item_cast_entry
    override val selectionManager get() = viewModel.roleInfoSelectionManager
    override val allItems get() = viewModel.cast
}

abstract class UIList<T: HasId>(private val deleteAllowed : Boolean,
                                private val multiSelectAllowed : Boolean,
                                @MenuRes menuId : Int = -1)
        : UI(R.layout.ui_list, menuId) {

    interface Navigation<T> where T:Any, T: HasId {
        fun onSingleSelect(item : T)
        fun onCreate(id : String)
    }

    @Suppress("UNCHECKED_CAST")
    private val stateFragment : Navigation<T>
        get() = parentFragment as Navigation<T>

    abstract val rowLayoutRes : Int
    abstract val selectionManager : SelectionManager<T>
    abstract val allItems : LiveData<List<T>>
    abstract fun getText1(item : T) : String
    open fun getText2(item : T) = ""

    open fun startDeleteMode() : Unit =
        throw IllegalStateException(if (deleteAllowed) "deleteAllowed is true but startDeleteMode() was not overridden" else "Should not be called")
    open fun deleteItemById(id: String) : Unit =
        throw IllegalStateException(if (deleteAllowed) "deleteAllowed is true but deleteItemById() was not overridden" else "Should not be called")
    open fun createNewItem() : T =
        throw IllegalStateException("action_add was present on the toolbar but createNewItem() was not overridden")

    // NOTE: In NAVIGATION video, I accidentally used "this" as the lifecycleOwner for the observers
    //       (because I copied the code from an activity and didn't check carefully enough!
    //       I've changed it to viewLifecycleOwner here. (It was ok in the other fragments) 
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

        val adapter = GenericAdapter(rowLayoutRes, multiSelectAllowed, selectionManager, { stateFragment.onSingleSelect(it) }, ::getText1, ::getText2)

        recyclerView.adapter = adapter

        allItems.observe(viewLifecycleOwner) {
            adapter.items = it ?: emptyList()
        }

        // selection support
        selectionManager.selections.observe(viewLifecycleOwner) {
            adapter.selections = it ?: emptySet()
            if (deleteAllowed) {
                invalidateActionMode()
            }
        }

        // adding contextual action mode when multiple items selected
        if (deleteAllowed) {
            selectionManager.multiSelectMode.observe(viewLifecycleOwner) {
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

        registerMenuHandler(R.id.action_add) {
            val newItem = createNewItem()
            selectionManager.selections.value = setOf(newItem)
            stateFragment.onCreate(newItem.id)
        }
    }
}
