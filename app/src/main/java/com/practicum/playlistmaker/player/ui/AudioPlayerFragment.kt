package com.practicum.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentAudioPlayerBinding
import com.practicum.playlistmaker.player.ui.model.TrackUi
import com.practicum.playlistmaker.player.ui.view_model.PlayerStateType
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerFragment : Fragment() {


    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private val playerViewModel by viewModel<PlayerViewModel> {
        parametersOf(getTrackFromExtra()?.previewUrl)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val track = getTrackFromExtra()

        if (track != null) {
            fillViewsWithTrackData(track)

            binding.playButton.setOnClickListener {
                playerViewModel.onPlayButtonClicked()
            }
            binding.pauseButton.setOnClickListener {
                playerViewModel.onPlayButtonClicked()
            }

            playerViewModel.observePlayerState().observe(viewLifecycleOwner) {
                if (it.stateType == PlayerStateType.STATE_PREPARED
                    || it.stateType == PlayerStateType.STATE_PAUSED
                ) {
                    showPlayButton()
                }
                if (it.stateType == PlayerStateType.STATE_PLAYING) {
                    showPauseButton()
                }
                binding.playerTrackPlayTime.text = it.playerTime
            }
        }

        binding.backButtonPlayer.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        binding.playerTrackName.text = track.trackName
        binding.playerTrackArtistName.text = track.artistName
        binding.durationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTime.toLong())
        binding.albumValue.text = track.collectionName
        binding.yearValue.text = track.releaseDate.take(4)
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country

        if (binding.albumValue.text.isNullOrEmpty()) {
            binding.albumGroup.isVisible = false
        }
        if (binding.yearValue.text.isNullOrEmpty()) {
            binding.yearGroup.isVisible = false
        }

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast(SLASH, TRACK_IMAGE_SIZE_512))
            .placeholder(R.drawable.ic_track_placeholder_312)
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.dimen_8dp)))
            .into(binding.playerTrackImage)
    }

    companion object {
        private const val TRACK_IMAGE_SIZE_512 = "512x512bb.jpg"
        private const val SLASH = "/"

        private const val TRACK_EXTRA = "TRACK_EXTRA"

        fun createArgs(track: TrackUi): Bundle {
            return bundleOf(
                TRACK_EXTRA to track
            )
        }
    }
}