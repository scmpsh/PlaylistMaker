package com.practicum.playlistmaker.presentation

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.dto.Track
import java.text.SimpleDateFormat
import java.util.Locale


const val TRACK_EXTRA = "TRACK_EXTRA"

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var trackNameView: TextView
    private lateinit var artistNameView: TextView
    private lateinit var trackDuration: TextView
    private lateinit var albumGroup: Group
    private lateinit var yearGroup: Group
    private lateinit var albumNameView: TextView
    private lateinit var yearView: TextView
    private lateinit var genreView: TextView
    private lateinit var countryView: TextView
    private lateinit var artworkView: ImageView
    private lateinit var backButton: ImageView
    private lateinit var playButton: ImageView
    private lateinit var pauseButton: ImageView
    private lateinit var playTimeView: TextView

    private var mediaPlayer = MediaPlayer()
    private lateinit var handler: Handler
    private val playTimeRunnable = renderPlayTime()

    private var playerState = STATE_DEFAULT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_audio_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.player)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val track = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK_EXTRA, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK_EXTRA)
        }
        handler = Handler(Looper.getMainLooper())
        initViews()

        if (track != null) {
            fillViewsWithTrackData(track)
            preparePlayer(track.previewUrl)
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun initViews() {
        trackNameView = findViewById(R.id.playerTrackName)
        artistNameView = findViewById(R.id.playerTrackArtistName)
        trackDuration = findViewById(R.id.durationValue)
        albumGroup = findViewById(R.id.albumGroup)
        yearGroup = findViewById(R.id.yearGroup)
        albumNameView = findViewById(R.id.albumValue)
        yearView = findViewById(R.id.yearValue)
        genreView = findViewById(R.id.genreValue)
        countryView = findViewById(R.id.countryValue)
        artworkView = findViewById(R.id.playerTrackImage)
        backButton = findViewById(R.id.backButtonPlayer)
        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)
        playTimeView = findViewById(R.id.playerTrackPlayTime)

        backButton.setOnClickListener { finish() }
    }

    private fun preparePlayer(previewUrl: String) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            pauseButton.visibility = View.GONE
            playButton.visibility = View.VISIBLE
            playerState = STATE_PREPARED
            handler.removeCallbacks(playTimeRunnable)
            setPlayerToBeginning()
        }

        playButton.setOnClickListener {
            playbackControl()
        }
        pauseButton.setOnClickListener {
            playbackControl()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        pauseButton.visibility = View.VISIBLE
        playButton.visibility = View.GONE
        playerState = STATE_PLAYING
        postRenderPlayTimeTask()
    }

    private fun pausePlayer() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
        pauseButton.visibility = View.GONE
        playButton.visibility = View.VISIBLE
        playerState = STATE_PAUSED
        handler.removeCallbacks(playTimeRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun postRenderPlayTimeTask() {
        handler.post(playTimeRunnable)
    }

    private fun renderPlayTime() = object : Runnable {
        override fun run() {
            playTimeView.text = SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(mediaPlayer.currentPosition)
            handler.postDelayed(this, THREE_HUNDRED_DELAY_MILLIS)
        }
    }


    private fun setPlayerToBeginning() {
        playTimeView.text = getString(R.string.default_play_time)
        if (mediaPlayer.currentPosition > 0) {
            mediaPlayer.seekTo(0)
        }
    }


    private fun fillViewsWithTrackData(track: Track) {
        trackNameView.text = track.trackName
        artistNameView.text = track.artistName
        trackDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
        albumNameView.text = track.collectionName
        yearView.text = track.releaseDate.take(4)
        genreView.text = track.primaryGenreName
        countryView.text = track.country

        if (albumNameView.text.isNullOrEmpty()) {
            albumGroup.visibility = View.GONE
        }
        if (yearView.text.isNullOrEmpty()) {
            yearGroup.visibility = View.GONE
        }

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast(SLASH, TRACK_IMAGE_SIZE_512))
            .placeholder(R.drawable.ic_track_placeholder_312)
            .centerCrop()
            .transform(RoundedCorners(resources.getDimensionPixelSize(R.dimen.dimen_8dp)))
            .into(artworkView)
    }

    companion object {
        private const val TRACK_IMAGE_SIZE_512 = "512x512bb.jpg"
        private const val SLASH = "/"
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3

        private const val THREE_HUNDRED_DELAY_MILLIS = 300L
    }
}