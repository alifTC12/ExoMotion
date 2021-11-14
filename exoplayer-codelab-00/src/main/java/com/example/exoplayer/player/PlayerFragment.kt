package com.example.exoplayer.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.exoplayer.R
import com.example.exoplayer.databinding.FragmentPlayerBinding
import com.example.exoplayer.domain.Movie
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import java.util.*
import kotlin.math.absoluteValue

private const val TAG = "PlayerFragment"

internal class PlayerFragment : Fragment(), PlayerMotion {

    private var player: SimpleExoPlayer? = null
    private var _binding: FragmentPlayerBinding? = null
    private val binding: FragmentPlayerBinding
        get() = _binding!!
    private val playbackStateListener: Player.EventListener = playbackStateListener()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.playerView.setOnTouchListener(object: View.OnTouchListener {
//            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
//                if (event?.actionMasked == MotionEvent.ACTION_MOVE) {
//                    return false
//                }
//
//                return true
//            }
//        })
        initPlayer()
        setOnClickListeners()
        setUpMovieList()
        setUpChipsFilter()
        setTriggerFiltersVisibility()
    }

    private fun setTriggerFiltersVisibility() {
        binding.appBarLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            binding.motionMoviesLayout.apply {
                if (appBarLayout.height - verticalOffset.absoluteValue <= 150) {
                    setTransition(R.id.filter_visible_transition)
                    progress =
                        1 - ((appBarLayout.height.toFloat() - verticalOffset.absoluteValue) / 150)
                }
            }
        })
    }

    private fun setUpChipsFilter() {
        val filters = listOf("All", "Computer Keyboard", "Related", "Recently Upload")
        filters.forEachIndexed { index, filter ->
            val chip = Chip(requireActivity())
            val chipDrawable = ChipDrawable.createFromAttributes(
                requireContext(), null, 0, R.style.ChipStyle
            )

            chip.apply {
                setChipDrawable(chipDrawable)
                id = View.generateViewId()
                text = filter
                isCheckable = true
                isChecked = index == 0
                shapeAppearanceModel =
                    shapeAppearanceModel.withCornerSize(30F)
            }

            binding.moviesFilterChipGroup.addView(chip)

            if (chip.isChecked) chip.setTextAppearance(R.style.ChipSelected)
            else chip.setTextAppearance(R.style.ChipDefault)
        }
    }

    private fun setUpMovieList() {
        binding.moviesRv.apply {
            adapter = MovieListMaxWidthAdapter().apply {
                val movie = Movie(
                    id = UUID.randomUUID().toString(),
                    imgUrl = "https://images.unsplash.com/photo-1500462918059-b1a0cb512f1d?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=987&q=80"
                )
                val movie2 = Movie(
                    id = UUID.randomUUID().toString(),
                    imgUrl = "https://images.unsplash.com/photo-1500462918059-b1a0cb512f1d?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=987&q=80"
                )
                val movie3 = Movie(
                    id = UUID.randomUUID().toString(),
                    imgUrl = "https://images.unsplash.com/photo-1500462918059-b1a0cb512f1d?ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=987&q=80"
                )
                submitList(listOf(movie, movie2, movie3))
            }
        }
    }

    private fun setOnClickListeners() {
        binding.apply {
            closeIv.setOnClickListener {
                rootPlayerContainer.jumpToState(R.id.gone)
                releasePlayer()
            }
        }
    }

    private fun initPlayer() {
        player = SimpleExoPlayer.Builder(requireContext())
            .build()
            .also {
                binding.playerViewContainer.player = it

                val mediaItem = MediaItem.Builder()
                    .setUri(getString(R.string.media_url_dash))
                    .setMimeType(MimeTypes.APPLICATION_MPD)
                    .build()

                it.setMediaItem(mediaItem)
            }

        player?.apply {
            playWhenReady = true
            addListener(playbackStateListener)
            prepare()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            removeListener(playbackStateListener)
            release()
        }

        player = null
    }

    override fun startShowContents() {
        binding.rootPlayerContainer.apply {
            setTransition(R.id.gone, R.id.expanded)
            setTransitionDuration(500)
            transitionToState(R.id.expanded)
        }
        initPlayer()
    }

    private fun playbackStateListener() = object : Player.EventListener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d(TAG, "changed state to $stateString")
        }
    }
}