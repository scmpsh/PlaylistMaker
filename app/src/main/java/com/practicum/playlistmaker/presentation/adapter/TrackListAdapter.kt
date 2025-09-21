package com.practicum.playlistmaker.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.dto.Track
import com.practicum.playlistmaker.presentation.callback.TrackListCallback
import com.practicum.playlistmaker.presentation.holder.TrackListHolder

class TrackListAdapter : RecyclerView.Adapter<TrackListHolder>() {

    private var trackList = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackListHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.track_list, parent, false)
        return TrackListHolder(view)
    }

    override fun onBindViewHolder(holder: TrackListHolder, position: Int) {
        holder.bind(trackList[position])
    }

    override fun getItemCount(): Int {
        return trackList.size
    }

    fun updateTrackList(newTrackList: List<Track>) {
        val diffCallback = TrackListCallback(trackList, newTrackList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        trackList.clear()
        trackList.addAll(newTrackList)
        diffResult.dispatchUpdatesTo(this)
    }


}