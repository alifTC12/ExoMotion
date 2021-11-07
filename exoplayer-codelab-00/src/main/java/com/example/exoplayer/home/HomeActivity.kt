package com.example.exoplayer.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.appcompat.app.AppCompatActivity
import com.example.exoplayer.R
import com.example.exoplayer.databinding.ActivityHomeBinding
import com.example.exoplayer.domain.Movie
import com.example.exoplayer.domain.MovieSection
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import java.util.*
import kotlin.math.abs
import kotlin.math.absoluteValue

class HomeActivity : AppCompatActivity(), HomeListAdapter.Companion.Interaction {

    private var player: SimpleExoPlayer? = null

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
        setUpToolbar()
        setPlayerSwipeToMinimizeListener()
        setClickListeners()
        initPlayer()
    }

    private fun initPlayer() {
        player = SimpleExoPlayer.Builder(this)
            .build()
            .also {
                viewBinding.playerView.player = player

                val mediaItem = MediaItem.Builder()
                    .setUri(getString(R.string.media_url_dash))
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .build()

                it.setMediaItem(mediaItem)
            }

        player?.apply {
            prepare()
        }
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initPlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if (Util.SDK_INT < 24 || player == null) {
            initPlayer()
        }
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        viewBinding.playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        player?.run {
            release()
        }

        player = null
    }

    private fun setUpToolbar() {
        setSupportActionBar(viewBinding.toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayShowHomeEnabled(false)
            setDisplayHomeAsUpEnabled(false)
        }
    }

    private fun setClickListeners() {
        viewBinding.playerSectionView.setOnClickListener {
            animateExpandPlayerSection()
        }
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

        val movieSection6 = MovieSection(
            id = UUID.randomUUID().toString(),
            title = "Section 2",
            movies = listOf(movie, movie, movie, movie, movie)
        )

        val movieSection7 = MovieSection(
            id = UUID.randomUUID().toString(),
            title = "Section 2",
            movies = listOf(movie, movie, movie, movie, movie)
        )

        val movieSection8 = MovieSection(
            id = UUID.randomUUID().toString(),
            title = "Section 2",
            movies = listOf(movie, movie, movie, movie, movie)
        )

        viewBinding.apply {
            homeRv.adapter = HomeListAdapter(this@HomeActivity).apply {
                submitList(
                    listOf(
                        movieSection,
                        movieSection2,
                        movieSection3,
                        movieSection4,
                        movieSection5,
                        movieSection6,
                        movieSection7,
                        movieSection8
                    )
                )
            }
            homeRv.setHasFixedSize(true)
        }
    }

    override fun onMovieClicked() {
        viewBinding.root.transitionToEnd()
    }

    private fun setPlayerSwipeToMinimizeListener() {
        viewBinding.playerView.setOnTouchListener(object : View.OnTouchListener {
            var startY = 0F
            var startX = 0F
            val touchSlop = ViewConfiguration.get(this@HomeActivity).scaledTouchSlop

            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startY = event.y
                        startX = event.x

                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val dy = event.y - startY

                        if (isAClick(startX, event.x, startY, event.y)) {
                            animateExpandPlayerSection()
                        } else if (dy.absoluteValue > touchSlop) {
                            animateMinimizePlayerSection()
                        }
                    }
                }

                return false
            }

            private fun isAClick(startX: Float, endX: Float, startY: Float, endY: Float): Boolean {
                val differenceX = abs(startX - endX)
                val differenceY = abs(startY - endY)
                return (differenceX > touchSlop || differenceY > touchSlop).not()
            }
        })
    }

    private fun animateMinimizePlayerSection() {
        viewBinding.motionLayout.apply {
            if (currentState == R.id.minimized) return@apply

            setTransition(R.id.expanded, R.id.minimized)
            transitionToState(R.id.minimized)
        }
    }

    private fun animateExpandPlayerSection() {
        viewBinding.motionLayout.apply {
            if (currentState == R.id.expanded) return@apply

            setTransition(R.id.minimized, R.id.expanded)
            transitionToState(R.id.expanded)
        }
    }
}