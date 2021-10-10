package com.example.exoplayer.home

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.exoplayer.databinding.ActivityHomeBinding
import com.example.exoplayer.domain.Movie
import com.example.exoplayer.domain.MovieSection
import java.util.*

class HomeActivity : AppCompatActivity() {

    private val viewBinding by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        ActivityHomeBinding.inflate(layoutInflater)
    }

    private val movie: Movie
        get() = Movie(
            id = UUID.randomUUID().toString(),
            imgUrl = "https://images.unsplash.com/photo-1500462918059-b1a0cb512f1d?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=987&q=80"
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        setUpHomeList()
    }

    private fun setUpHomeList() {
        val movieSection = MovieSection(
            id = UUID.randomUUID().toString(),
            title = "Section 1",
            movies = listOf(movie, movie, movie, movie, movie)
        )

        val movieSection2 = MovieSection(
            id = UUID.randomUUID().toString(),
            title = "Section 2",
            movies = listOf(movie, movie, movie, movie, movie)
        )

        val movieSection3 = MovieSection(
            id = UUID.randomUUID().toString(),
            title = "Section 2",
            movies = listOf(movie, movie, movie, movie, movie)
        )

        val movieSection4 = MovieSection(
            id = UUID.randomUUID().toString(),
            title = "Section 2",
            movies = listOf(movie, movie, movie, movie, movie)
        )

        val movieSection5 = MovieSection(
            id = UUID.randomUUID().toString(),
            title = "Section 2",
            movies = listOf(movie, movie, movie, movie, movie)
        )


        viewBinding.apply {
            homeRv.adapter = HomeListAdapter().apply {
                submitList(
                    listOf(
                        movieSection,
                        movieSection2,
                        movieSection3,
                        movieSection4,
                        movieSection5
                    )
                )
            }
            homeRv.setHasFixedSize(true)
        }
    }
}