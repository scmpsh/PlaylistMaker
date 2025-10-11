package com.practicum.playlistmaker.presentation

import android.os.Build
import android.os.Bundle
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
        initViews()

        if (track != null) {
            fillViewsWithTrackData(track)
        }
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

        backButton.setOnClickListener { finish() }

    }

    private fun fillViewsWithTrackData(track: Track) {
        trackNameView.text = track.trackName
        artistNameView.text = track.artistName
        trackDuration.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTime.toLong())
        albumNameView.text = track.collectionName
        yearView.text = track.releaseDate.substring(0, 4)
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
    }
}