package com.practicum.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.practicum.playlistmaker.media.domain.dto.Playlist

class PlaylistBottomSheetViewHolder(private val binding: ItemPlaylistBottomSheetBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        binding.playlistName.text = playlist.name
        binding.tracksCount.text = itemView.context.resources.getQuantityString(
            R.plurals.tracks_count,
            playlist.tracksCount,
            playlist.tracksCount
        )

        Glide.with(itemView)
            .load(playlist.coverImagePath)
            .placeholder(R.drawable.ic_track_placeholder)
            .transform(
                CenterCrop(),
                RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.dimen_2dp))
            )
            .into(binding.cover)
    }

    companion object {
        fun from(parent: ViewGroup): PlaylistBottomSheetViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemPlaylistBottomSheetBinding.inflate(inflater, parent, false)
            return PlaylistBottomSheetViewHolder(binding)
        }
    }
}
