package com.practicum.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivityAudioPlayerBinding
import com.practicum.playlistmaker.player.ui.model.TrackUi
import com.practicum.playlistmaker.player.ui.view_model.PlayerStateType
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import java.text.SimpleDateFormat
import java.util.Locale


const val TRACK_EXTRA = "TRACK_EXTRA"

class AudioPlayerActivity : AppCompatActivity() {


    private lateinit var binding: ActivityAudioPlayerBinding

    private var viewModel: PlayerViewModel? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_EXTRA, TrackUi::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_EXTRA)
        }

        if (track != null) {
            fillViewsWithTrackData(track)

            viewModel = ViewModelProvider(
                this,
                PlayerViewModel.getFactory(track.previewUrl, Creator.getMediaPlayer())
            )[PlayerViewModel::class.java]

            binding.playButton.setOnClickListener {
                viewModel?.onPlayButtonClicked()
            }
            binding.pauseButton.setOnClickListener {
                viewModel?.onPlayButtonClicked()
            }

            viewModel?.observePlayerState()?.observe(this) {
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

        binding.backButtonPlayer.setOnClickListener { finish() }
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
    }
}