package com.javadude.moviesrecyclerview

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MovieViewModel>()

    private var actionMode : ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val movieList = findViewById<RecyclerView>(R.id.movie_list)

        val adapter = MovieAdapter2(
            onClicked = viewModel::onClicked, // function reference!
            onIconClicked = { viewModel.onIconClicked(it) }, // normal lambda
            onLongClicked = { viewModel.onLongClicked(it) }
        )

        movieList.adapter = adapter

        viewModel.allMovies.observe(this) {
            adapter.movies = it ?: emptyList()
        }

        // selection support
        viewModel.selectedMovies.observe(this) {
            adapter.selectedMovies = it ?: emptySet()
            actionMode?.invalidate()
        }

        // adding contextual action mode when multiple items selected
        viewModel.multiMovieSelectMode.observe(this) {
            if (it == true) { // handles null as false...
                // start action mode
                if (actionMode == null) {
                    startSupportActionMode(DeleteMovieActionMode())
                }
            } else {
                // dismiss action mode
                actionMode?.finish()
            }
        }

        // adding in "swipe to delete" capability
        ItemTouchHelper(Swiper()).attachToRecyclerView(movieList)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    private var nextMovieNumber = 10
    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.action_create_movie -> {
                viewModel.addMovie(
                    Movie("m$nextMovieNumber", "Movie $nextMovieNumber", "Description $nextMovieNumber"),
                    Role("m$nextMovieNumber", "a1", "Random Hero 1", 1),
                    Role("m$nextMovieNumber", "a2", "Random Hero 2", 2)
                )
                nextMovieNumber++
                true
            }
            else ->super.onOptionsItemSelected(item)
        }

    abstract inner class DeleteMode(val deleteAction : () -> Unit) : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            actionMode = mode
            mode.menuInflater.inflate(R.menu.menu_delete, menu)
            return true
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem) =
            when(item.itemId) {
                R.id.action_delete -> {
                    deleteAction()
                    mode.finish()
                    true
                }
                else -> false
            }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu) : Boolean {
            val numSelections = viewModel.selectedMovies.value?.size ?: 0
            mode.title = "$numSelections selected"
            return true
        }
        override fun onDestroyActionMode(mode: ActionMode) {
            actionMode = null
        }
    }

    inner class DeleteMovieActionMode : DeleteMode( { viewModel.deleteSelectedMovies() } )
    inner class DeleteActorActionMode : DeleteMode( { viewModel.deleteSelectedActors() } )

    // Handler for LEFT-SWIPE = delete
    inner class Swiper : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        private val deleteIcon = ContextCompat.getDrawable(this@MainActivity, R.drawable.ic_delete_sweep_white_24dp)
        private val background = ColorDrawable(ContextCompat.getColor(this@MainActivity, R.color.delete_swipe_background))
        private val deleteIconMargin = this@MainActivity.resources.getDimension(R.dimen.delete_icon_margin).toInt()

        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder) = false // should never get called

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            viewModel.deleteMovieAt(viewHolder.adapterPosition)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

            // get the bounds of the viewHolder
            //    (we're only allowing left swipe, so we don't need to know the left margin)
            val top = viewHolder.itemView.top
            val right = viewHolder.itemView.right
            val bottom = viewHolder.itemView.bottom

            // set the bounds and draw the red background
            //   when swiping left, dX will be the negative amount the view has been swiped so far
            background.setBounds(right+dX.toInt(), top, right, bottom)
            background.draw(c)

            // draw the trash can icon
            deleteIcon?.let {
                // make it as big as possible to fit as a square vertically within the
                //   specified margins between the top and bottom of the view
                val iconSize = bottom - top - deleteIconMargin*2
                it.setBounds(
                    right - iconSize - deleteIconMargin,
                    top + deleteIconMargin,
                    right-deleteIconMargin,
                    bottom - deleteIconMargin)
                it.draw(c)
            }
        }
    }
}
