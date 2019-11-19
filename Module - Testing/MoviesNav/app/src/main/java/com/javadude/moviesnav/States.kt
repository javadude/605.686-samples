package com.javadude.moviesnav

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.navigation.fragment.navArgs
import com.javadude.moviesnav.db.Actor
import com.javadude.moviesnav.db.Movie
import com.javadude.moviesnav.db.Role

class StateAbout : State(R.string.about, R.layout.state_about)

class StateActorList : State(R.string.actors, R.layout.state_actor_list), UIActorDisplay.Navigation, UIMultiSelectActorList.Navigation {
    override fun onViewAllMovies()              = navigate(StateActorListDirections.actionViewAllMovies())
    override fun onSingleSelect(item: Actor)    = navigate(StateActorListDirections.actionDisplayActor(item.id))
    override fun onCreate(id: String)           = navigate(StateActorListDirections.actionEditActor(id))
    override fun onSelectMovie(id: String)      = navigate(StateActorListDirections.actionDisplayMovie(id))
    override fun onEditActor() {
        viewModel.actorSelectionManager.selections.value?.singleOrNull()?.let {
            navigate(StateActorListDirections.actionEditActor(it.id))
        }
    }
}
class StateActorEdit : State(R.string.actor, R.layout.state_actor_edit), UIMultiSelectActorList.Navigation {
    private val args : StateActorEditArgs by navArgs()

    override fun onViewAllMovies()              = navigate(StateActorListDirections.actionViewAllMovies())
    override fun doSelect()                     = viewModel.selectActor(args.actorId)
    override fun onSingleSelect(item: Actor)    = navigate(StateActorEditDirections.actionDisplayActor(item.id))
    override fun onCreate(id: String)           = navigate(StateActorEditDirections.actionEditActor(id))
}
class StateActorDisplay : State(R.string.actor, R.layout.state_actor_display), UIActorDisplay.Navigation, UIMultiSelectActorList.Navigation {
    private val args : StateActorDisplayArgs by navArgs()

    override fun onViewAllMovies()              = navigate(StateActorListDirections.actionViewAllMovies())
    override fun onSingleSelect(item: Actor)    = navigate(StateActorDisplayDirections.actionDisplayActor(item.id))
    override fun onSelectMovie(id: String)      = navigate(StateActorDisplayDirections.actionDisplayMovie(id))
    override fun onCreate(id: String)           = navigate(StateActorDisplayDirections.actionEditActor(id))
    override fun onEditActor()                  = navigate(StateActorDisplayDirections.actionEditActor(args.actorId))
    override fun doSelect()                     = viewModel.selectActor(args.actorId)
}

class StateMovieList : State(R.string.movies, R.layout.state_movie_list), UIMovieDisplay.Navigation, UIMovieList.Navigation {
    override fun onViewAllActors()              = navigate(StateMovieListDirections.actionViewAllActors())
    override fun onSingleSelect(item: Movie)    = navigate(StateMovieListDirections.actionDisplayMovie(item.id))
    override fun onSelectActor(id: String)      = navigate(StateMovieListDirections.actionDisplayActor(id))
    override fun onCreate(id: String)           = navigate(StateMovieListDirections.actionEditMovie(id))
    override fun onEditMovie() {
        viewModel.actorSelectionManager.selections.value?.singleOrNull()?.let {
            navigate(StateMovieListDirections.actionEditMovie(it.id))
        }
    }
}
class StateMovieEdit : State(R.string.movie, R.layout.state_movie_edit), UIMovieEdit.Navigation, UIMovieList.Navigation {
    private val args : StateMovieEditArgs by navArgs()
    override fun onViewAllActors()                              = navigate(StateMovieListDirections.actionViewAllActors())
    override fun doSelect()                                     = viewModel.selectMovie(args.movieId)
    override fun onSingleSelect(item: Movie)                    = navigate(StateMovieEditDirections.actionDisplayMovie(item.id))
    override fun onCreate(id: String)                           = navigate(StateMovieEditDirections.actionEditMovie(id))
    override fun onEditRole(roleId: String?, movieId: String?)  = navigate(StateMovieEditDirections.actionEditRole(roleId, movieId))
}

// This is a special case state to handle the fact that StateMovieDisplay and StateMovieList
//    contain the same two fragments in side-by-side mode.
// By having a special "first movie display" state, we know when we're going back to the movie list
//   (This would be easier if the navigation controller exposed its back stack count... grrrr...
//      then we could just use the StateMovieDisplay and check if the back stack count were 2...)
//
// If we're going back from this state AND in side-by-side display, just exit the application
class StateFirstMovieDisplay : StateMovieDisplay() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // if we're in side-by-side display, override "back" handling for this special-case state
        // note that this handler will be removed when the view is killed (using viewLifecycleOwner)
        //   so we do not need to worry about removing it, and onViewCreated will only be called
        //   again if the previous view were destroyed
        val fragments = childFragmentManager.fragments
        if (fragments.size == 2 && fragments.all { it.isInLayout }) { // in side-by-side mode
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
                activity?.finish()
            }
        }
    }
}

open class StateMovieDisplay : State(R.string.movie, R.layout.state_movie_display), UIMovieDisplay.Navigation, UIMovieList.Navigation {
    private val args : StateMovieDisplayArgs by navArgs()

    override fun onViewAllActors()              = navigate(StateMovieListDirections.actionViewAllActors())
    override fun doSelect()                     = viewModel.selectMovie(args.movieId)
    override fun onSingleSelect(item: Movie)    = navigate(StateMovieEditDirections.actionDisplayMovie(item.id))
    override fun onSelectActor(id: String)      = navigate(StateMovieDisplayDirections.actionDisplayActor(id))
    override fun onCreate(id: String)           = navigate(StateMovieEditDirections.actionEditMovie(id))
    override fun onEditMovie()                  = navigate(StateMovieEditDirections.actionEditMovie(args.movieId))
}

class StateRoleEdit : State(R.string.role, R.layout.state_role_edit), UIMovieEdit.Navigation, UIRoleEdit.Navigation, UIList.Navigation<Role> {
    private val args : StateRoleEditArgs by navArgs()
    override fun getRoleId()                                    = args.roleId
    override fun getMovieId()                                   = args.movieId
    override fun onSingleSelect(item: Role)                     = navigate(StateRoleEditDirections.actionEditRole(item.id, null))
    override fun onCreate(id: String)                           = navigate(StateRoleEditDirections.actionEditRole(null, args.movieId))
    override fun onEditRole(roleId: String?, movieId: String?)  = navigate(StateMovieEditDirections.actionEditRole(roleId, movieId))
}