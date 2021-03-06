package com.example.exoplayer.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exoplayer.R
import com.example.exoplayer.databinding.ItemMovieBinding
import com.example.exoplayer.domain.Movie

class MovieListAdapter(private val interaction: HomeListAdapter.Companion.Interaction) :
    ListAdapter<Movie, MovieListAdapter.MovieViewHolder>(MovieDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                interaction.onMovieClicked()
            }
        }

        fun bind(movie: Movie) {
            // TODO remove dummy thumbnail

            val thumbnails =
                listOf(
                    R.drawable.ic_movie_1,
                    R.drawable.ic_movie_2,
                    R.drawable.ic_movie_3
                ).shuffled()
            binding.thumbnailIv.setImageDrawable(
                AppCompatResources.getDrawable(
                    binding.root.context,
                    thumbnails.first()
                )
            )
        }
    }
}

private object MovieDiffUtil : DiffUtil.ItemCallback<Movie>() {
    override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
        return oldItem == newItem
    }
}