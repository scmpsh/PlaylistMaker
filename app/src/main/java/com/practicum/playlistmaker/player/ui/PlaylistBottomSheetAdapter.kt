package com.practicum.playlistmaker.player.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.media.domain.dto.Playlist
import com.practicum.playlistmaker.media.ui.PlaylistBottomSheetViewHolder

class PlaylistBottomSheetAdapter(
    private val onPlaylistClick: (Playlist) -> Unit,
) : RecyclerView.Adapter<PlaylistBottomSheetViewHolder>() {

    private val items = mutableListOf<Playlist>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlaylistBottomSheetViewHolder {
        return PlaylistBottomSheetViewHolder.Companion.from(parent)
    }

    override fun onBindViewHolder(holder: PlaylistBottomSheetViewHolder, position: Int) {
        val playlist = items[position]
        holder.bind(playlist)
        holder.itemView.setOnClickListener { onPlaylistClick(playlist) }
    }

    override fun getItemCount(): Int = items.size

    fun updatePlaylists(newItems: List<Playlist>) {
        val diffCallback = PlaylistBottomSheetCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }
}