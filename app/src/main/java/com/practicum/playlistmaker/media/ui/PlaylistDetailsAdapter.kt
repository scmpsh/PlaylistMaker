package com.practicum.playlistmaker.media.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.ui.SearchCallback
import com.practicum.playlistmaker.search.ui.SearchHolder
import com.practicum.playlistmaker.search.ui.view_model.SearchUiItem
import com.practicum.playlistmaker.sharing.domain.model.Track

class PlaylistDetailsAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onTrackLongClick: (Track) -> Unit
) : RecyclerView.Adapter<SearchHolder>() {

    private val items = mutableListOf<SearchUiItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchHolder {
        return SearchHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SearchHolder, position: Int) {
        val item = items[position] as SearchUiItem.TrackItem
        holder.bind(item.track, onTrackClick)
        holder.itemView.setOnLongClickListener {
            onTrackLongClick(item.track)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateTrackList(newItems: List<SearchUiItem>) {
        val diffCallback = SearchCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }
}
