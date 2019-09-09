package com.javadude.moviesnav

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter2(
    val onClicked : (Movie) -> Unit,
    val onIconClicked : (Movie) -> Unit,
    val onLongClicked : (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter2.MovieViewHolder>() {
    var selectedMovies : Set<Movie> = emptySet()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(MovieDiffUtilCallback(movies, movies, value, field))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    var movies : List<Movie> = emptyList()
        set(value) {
            val diffResult = DiffUtil.calculateDiff(MovieDiffUtilCallback(value, field, selectedMovies, selectedMovies))
            field = value
            diffResult.dispatchUpdatesTo(this)
        }

    override fun getItemCount() = movies.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movie, parent, false))

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) =
        holder.bind(movies[position])

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title = itemView.findViewById<TextView>(R.id.title)
        private val icon = itemView.findViewById<ImageView>(R.id.icon)
        init {
            itemView.setOnClickListener {
                onClicked(movies[adapterPosition])
            }
            icon.setOnClickListener {
                onIconClicked(movies[adapterPosition])
            }
            itemView.setOnLongClickListener {
                onLongClicked(movies[adapterPosition])
                true
            }
        }
        fun bind(movie : Movie) {
            title.text = movie.title
            // selection state is inherited by child views! nifty!
            itemView.isSelected = movie in selectedMovies
        }
    }


    // DiffUtil implementation to improve performance and add animation
    class MovieDiffUtilCallback(
            private val newMovies : List<Movie>,
            private val oldMovies: List<Movie>,
            private val newSelections : Set<Movie>,
            private val oldSelections : Set<Movie>)
        : DiffUtil.Callback() {

        override fun getOldListSize() = oldMovies.size
        override fun getNewListSize() = newMovies.size
        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
            oldMovies[oldItemPosition].id == newMovies[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            // we only care about the title and selection status for display purposes
            val old = oldMovies[oldItemPosition]
            val new = newMovies[newItemPosition]
            val oldSelected = old in oldSelections
            val newSelected = new in newSelections
            return (old.title == new.title && oldSelected == newSelected)
        }
    }
}
