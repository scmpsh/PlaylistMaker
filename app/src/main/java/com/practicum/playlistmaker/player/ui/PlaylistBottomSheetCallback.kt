package com.practicum.playlistmaker.player.ui

import androidx.recyclerview.widget.DiffUtil
import com.practicum.playlistmaker.media.domain.dto.Playlist

class PlaylistBottomSheetCallback(
    private val oldList: List<Playlist>,
    private val newList: List<Playlist>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldList[oldPos].id == newList[newPos].id
    }

    override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
        return oldList[oldPos] == newList[newPos]
    }
}