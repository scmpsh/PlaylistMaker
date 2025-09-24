package com.practicum.playlistmaker.presentation.callback

import androidx.recyclerview.widget.DiffUtil
import com.practicum.playlistmaker.data.dto.Track

class TrackListCallback(
    private val oldList: List<Track>,
    private val newList: List<Track>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean =
        oldList[oldItemPosition].trackName === newList[newItemPosition].trackName

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        val (oldTrackName, oldArtistName, oldTrackTime, oldArtworkUrl100) = oldList[oldItemPosition]
        val (newTrackName, newArtistName, newTrackTime, newArtworkUrl100) = newList[newItemPosition]
        return oldTrackName == newTrackName
                && oldArtistName == newArtistName
                && oldTrackTime == newTrackTime
                && oldArtworkUrl100 == newArtworkUrl100
    }

}