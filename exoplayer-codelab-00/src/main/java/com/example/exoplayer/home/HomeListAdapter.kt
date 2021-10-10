package com.example.exoplayer.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exoplayer.databinding.ItemMovieSectionBinding
import com.example.exoplayer.domain.MovieSection

class HomeListAdapter : ListAdapter<MovieSection, MovieSectionViewHolder>(MovieSectionDiffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieSectionViewHolder {
        val binding =
            ItemMovieSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieSectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieSectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}


object MovieSectionDiffUtil : DiffUtil.ItemCallback<MovieSection>() {
    override fun areItemsTheSame(oldItem: MovieSection, newItem: MovieSection): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MovieSection, newItem: MovieSection): Boolean {
        return oldItem == newItem
    }
}

class MovieSectionViewHolder(private val binding: ItemMovieSectionBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(movieSection: MovieSection) {
        binding.apply {
            titleTv.text = movieSection.title
            moviesRv.adapter = MovieListAdapter().apply {
                submitList(movieSection.movies)
            }
            moviesRv.setHasFixedSize(true)
        }
    }
}