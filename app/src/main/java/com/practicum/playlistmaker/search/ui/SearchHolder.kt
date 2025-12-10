package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackListBinding
import com.practicum.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class SearchHolder(
    private val binding: TrackListBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(track: Track, onTrackClick: (Track) -> Unit) {
        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTime.toLong())
        Glide.with(binding.root)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.ic_track_placeholder)
            .into(binding.trackImage)

        binding.root.setOnClickListener { onTrackClick(track) }
    }

    companion object {
        fun from(parent: ViewGroup): SearchHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = TrackListBinding.inflate(inflater, parent, false)
            return SearchHolder(binding)
        }
    }
}