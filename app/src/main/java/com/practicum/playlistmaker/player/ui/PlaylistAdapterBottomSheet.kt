package com.practicum.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ItemPlaylistBottomSheetBinding
import com.practicum.playlistmaker.media.domain.dto.Playlist

class PlaylistAdapterBottomSheet(private val clickListener: PlaylistClickListener) :
    RecyclerView.Adapter<PlaylistViewHolderBottomSheet>() {

    var playlists = ArrayList<Playlist>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistViewHolderBottomSheet {
        val layoutInspector = LayoutInflater.from(parent.context)
        return PlaylistViewHolderBottomSheet(
            ItemPlaylistBottomSheetBinding.inflate(
                layoutInspector,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PlaylistViewHolderBottomSheet, position: Int) {
        holder.bind(playlists[position])
        holder.itemView.setOnClickListener { clickListener.onPlaylistClick(playlists[position]) }
    }

    override fun getItemCount(): Int = playlists.size

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}

class PlaylistViewHolderBottomSheet(private val binding: ItemPlaylistBottomSheetBinding) :
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
}