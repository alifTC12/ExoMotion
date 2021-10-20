package com.example.exoplayer.home

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.exoplayer.databinding.ItemMovieSectionBinding
import com.example.exoplayer.domain.MovieSection

class HomeListAdapter : ListAdapter<MovieSection, MovieSectionViewHolder>(MovieSectionDiffUtil) {
    private val layoutManagerStates = hashMapOf<String, Parcelable?>()

    override fun onViewRecycled(holder: MovieSectionViewHolder) {
        super.onViewRecycled(holder)

        layoutManagerStates[holder.getId()] = holder.getLayoutManager()?.onSaveInstanceState()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieSectionViewHolder {
        val binding =
            ItemMovieSectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieSectionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieSectionViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.getLayoutManager()?.apply {
            val state = layoutManagerStates[holder.getId()]
            state?.let { onRestoreInstanceState(it) }
        }
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
    RecyclerView.ViewHolder(binding.root), NestedRecyclerViewViewHolder {
    private lateinit var movieSection: MovieSection

    init {
        binding.moviesRv.adapter = MovieListAdapter()
    }

    fun bind(movieSection: MovieSection) {
        this.movieSection = movieSection

        binding.apply {
            titleTv.text = movieSection.title
            (moviesRv.adapter as MovieListAdapter).submitList(movieSection.movies)
            moviesRv.setHasFixedSize(true)
        }
    }

    override fun getId(): String {
        return movieSection.id
    }

    override fun getLayoutManager(): RecyclerView.LayoutManager? {
        return binding.moviesRv.layoutManager
    }
}