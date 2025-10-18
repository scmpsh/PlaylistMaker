package com.practicum.playlistmaker.presentation.adapter

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.TrackPreferences
import com.practicum.playlistmaker.data.dto.Track
import com.practicum.playlistmaker.presentation.AudioPlayerActivity
import com.practicum.playlistmaker.presentation.TRACK_EXTRA
import com.practicum.playlistmaker.presentation.callback.TrackListCallback
import com.practicum.playlistmaker.presentation.holder.ClearTracksButtonHolder
import com.practicum.playlistmaker.presentation.holder.TrackListHolder

class TrackListAdapter(
    val sharedPreferences: SharedPreferences,
    val trackList: MutableList<Track>,
    val onClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val trackPreferences = TrackPreferences()

    private var clearButtonVisibility = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.track_list -> TrackListHolder(view)
            else -> ClearTracksButtonHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TrackListHolder) {
            val track = trackList[position]
            holder.bind(track)
            holder.itemView.setOnClickListener {
                onTrackClick(it, track)
            }
        }
        if (holder is ClearTracksButtonHolder) {
            holder.bind(onClick, clearButtonVisibility)
        }
    }

    private fun onTrackClick(itemView: View, track: Track) {
        trackPreferences.write(sharedPreferences, track)

        val displayIntent = Intent(itemView.context, AudioPlayerActivity::class.java)
        val bundle = Bundle()

        bundle.putSerializable(TRACK_EXTRA, track)
        displayIntent.putExtras(bundle)
        itemView.context.startActivity(displayIntent)
    }

    override fun getItemCount(): Int {
        return trackList.size + 1
    }

    override fun getItemViewType(position: Int): Int =
        if (position < trackList.size)
            R.layout.track_list
        else
            R.layout.clear_track_list_button

    fun updateTrackList(newTrackList: List<Track>) {
        val diffCallback = TrackListCallback(trackList, newTrackList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        trackList.clear()
        trackList.addAll(newTrackList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setClearButtonVisibility(visible: Boolean) {
        val oldVisibility = clearButtonVisibility
        clearButtonVisibility = visible
        if (oldVisibility != visible) {
            notifyItemRangeChanged(itemCount - 1, 1)
        }
    }
}