package com.practicum.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.PEEK_HEIGHT_AUTO
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.player.ui.model.TrackUi
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerFragment : Fragment() {

    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private val playerViewModel by viewModel<PlayerViewModel> {
        parametersOf(
            getTrackFromExtra()?.previewUrl,
            getTrackFromExtra()?.trackId,
        )
    }

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val playlistAdapter = PlaylistAdapterBottomSheet { playlist ->
        val track = getTrackFromExtra()
        if (track != null) {
            val isSaved = playerViewModel.addTrackToPlaylist(playlist, track)
            if (isSaved) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                Toast.makeText(
                    requireContext(),
                    getString(R.string.added_to_playlist, playlist.name),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.already_added_to_playlist, playlist.name),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = getTrackFromExtra()

        if (track != null) {
            fillViewsWithTrackData(track)
            playerViewModel.initFavoriteStatus()

            binding.playButton.setOnClickListener {
                playerViewModel.onPlayButtonClicked()
            }
            binding.pauseButton.setOnClickListener {
                playerViewModel.onPlayButtonClicked()
            }
            binding.favoriteButton.setOnClickListener {
                playerViewModel.onFavoriteClicked(track)
            }
            binding.favoriteButtonActive.setOnClickListener {
                playerViewModel.onFavoriteClicked(track)
            }

            playerViewModel.observePlayerState().observe(viewLifecycleOwner) {
                if (it.isPlayButtonEnabled) {
                    showPlayButton()
                } else {
                    showPauseButton()
                }
                showFavoriteButton(it.isFavorite)
                binding.playerTrackPlayTime.text = it.progress
                playlistAdapter.playlists.clear()
                playlistAdapter.playlists.addAll(it.playlists)
                playlistAdapter.notifyDataSetChanged()
            }
        }

        binding.backButtonPlayer.setOnClickListener {
            findNavController().navigateUp()
        }

        setupBottomSheet()

        binding.queueButton.setOnClickListener {
            playerViewModel.getAllPlaylists()
            bottomSheetBehavior.peekHeight = PEEK_HEIGHT_AUTO
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_audioPlayerFragment_to_createPlaylistFragment)
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }

                    else -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.overlay.alpha = slideOffset + 1f
            }
        })

        binding.playlistsRecyclerView.adapter = playlistAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showFavoriteButton(favorite: Boolean) {
        with(binding) {
            favoriteButton.isVisible = !favorite
            favoriteButtonActive.isVisible = favorite
        }
    }

    private fun getTrackFromExtra(): TrackUi? {
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(TRACK_EXTRA, TrackUi::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable(TRACK_EXTRA)
        }
        return track
    }

    private fun showPauseButton() {
        binding.pauseButton.isVisible = true
        binding.playButton.isVisible = false
    }

    private fun showPlayButton() {
        binding.pauseButton.isVisible = false
        binding.playButton.isVisible = true
    }

    private fun fillViewsWithTrackData(track: TrackUi) {
        with(binding) {
            playerTrackName.text = track.trackName
            playerTrackArtistName.text = track.artistName
            durationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(track.trackTime.toLong())
            albumValue.text = track.collectionName
            yearValue.text = track.releaseDate.take(4)
            genreValue.text = track.primaryGenreName
            countryValue.text = track.country

            if (albumValue.text.isNullOrEmpty()) {
                albumGroup.isVisible = false
            }
            if (yearValue.text.isNullOrEmpty()) {
                yearGroup.isVisible = false
            }

        }
        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast(SLASH, TRACK_IMAGE_SIZE_512))
            .placeholder(R.drawable.ic_track_placeholder_312)
            .transform(
                CenterCrop(),
                RoundedCorners(resources.getDimensionPixelSize(R.dimen.dimen_8dp))
            )
            .into(binding.playerTrackImage)
    }

    companion object {
        private const val TRACK_IMAGE_SIZE_512 = "512x512bb.jpg"
        private const val SLASH = "/"

        private const val TRACK_EXTRA = "TRACK_EXTRA"

        fun createArgs(track: TrackUi): Bundle {
            return bundleOf(
                TRACK_EXTRA to track,
            )
        }
    }
}