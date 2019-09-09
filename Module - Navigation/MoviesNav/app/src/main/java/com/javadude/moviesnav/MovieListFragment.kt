//package com.javadude.moviesnav
//
//import android.os.Bundle
//import android.view.MenuItem
//import android.view.View
//import androidx.recyclerview.widget.RecyclerView
//
//class MovieListFragment : BaseFragment(R.layout.fragment_movie_list, R.menu.menu_list) {
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        val movieList = view.findViewById<RecyclerView>(R.id.movie_list)
//
//        val adapter = GenericAdapter(
//            rowLayoutRes = R.layout.movie,
//            selectionManager = viewModel.movieSelectionManager,
//            singleSelectAction = { navigate(R.id.action_display_movie) },
//            getText1 = { it.title }
//        )
//
//        movieList.adapter = adapter
//
//        viewModel.allMovies.observe(this) {
//            adapter.items = it ?: emptyList()
//        }
//
//        // selection support
//        viewModel.movieSelectionManager.selections.observe(this) {
//            adapter.selections = it ?: emptySet()
//            invalidateActionMode()
//        }
//
//        // adding contextual action mode when multiple items selected
//        viewModel.movieSelectionManager.multiSelectMode.observe(this) {
//            if (it == true) { // handles null as false...
//                startMovieDeleteMode()
//            } else {
//                dismissActionMode()
//            }
//        }
//
//        movieList.swipeLeft {
//            viewModel.deleteMovieAt(it)
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem) =
//        when (item.itemId) {
//            R.id.action_add -> {
//                val newMovie = Movie()
//                viewModel.addMovie(newMovie)
//                viewModel.movieSelectionManager.selections.value = setOf(newMovie)
//                navigate(R.id.action_create_movie)
//                true
//            }
//            else ->super.onOptionsItemSelected(item)
//        }
//}
