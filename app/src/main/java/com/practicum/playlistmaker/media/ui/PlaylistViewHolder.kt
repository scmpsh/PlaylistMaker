package com.practicum.playlistmaker.media.ui

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemPlaylistBinding
import com.practicum.playlistmaker.media.domain.dto.Playlist
import java.io.File

class PlaylistViewHolder(private val binding: ItemPlaylistBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(playlist: Playlist) {
        binding.playlistName.text = playlist.name
        
        val count = playlist.tracks.size
        binding.tracksCount.text = itemView.context.resources.getQuantityString(
            R.plurals.tracks_count,
            count,
            count
        )

        val imageUri =
            if (playlist.coverImagePath.isNotEmpty()) Uri.fromFile(File(playlist.coverImagePath)) else null

        Glide.with(binding.root)
            .load(imageUri)
            .placeholder(R.drawable.ic_track_placeholder_312)
            .transform(
                CenterCrop(),
                RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.dimen_8dp))
            )
            .into(binding.playlistCover)
    }
}
