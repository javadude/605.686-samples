package com.javadude.moviesrecyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class MovieAdapter(
    val onClicked : (Movie) -> Unit,
    val onIconClicked : (Movie) -> Unit,
    val onLongClicked : (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    var selectedMovies : Set<Movie> = emptySet()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var movies : List<Movie> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
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
                val movie = movies[adapterPosition]
                Toast.makeText(itemView.context, "Tapped movie '${movie.title}'", Toast.LENGTH_SHORT).show()
                onClicked(movie)
            }
            icon.setOnClickListener {
                val movie = movies[adapterPosition]
                Toast.makeText(itemView.context, "Tapped movie '${movie.title}'", Toast.LENGTH_SHORT).show()
                onIconClicked(movie)
            }
            itemView.setOnLongClickListener {
                val movie = movies[adapterPosition]
                Toast.makeText(itemView.context, "Tapped movie '${movie.title}'", Toast.LENGTH_SHORT).show()
                onLongClicked(movie)
                true
            }
        }
        fun bind(movie : Movie) {
            title.text = movie.title
            val selected = movie in selectedMovies
            itemView.isSelected = selected
            icon.isSelected = selected
            title.isSelected = selected
        }
    }
}
