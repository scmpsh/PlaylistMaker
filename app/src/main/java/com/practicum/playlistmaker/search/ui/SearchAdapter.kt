package com.practicum.playlistmaker.search.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.view_model.SearchUiItem

class SearchAdapter(
    private val onTrackClick: (Track) -> Unit,
    private val onClearHistoryClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items = mutableListOf<SearchUiItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.track_list -> SearchHolder.from(parent)
            else -> ClearHistoryHolder.from(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = items[position]) {
            is SearchUiItem.TrackItem ->
                (holder as SearchHolder).bind(item.track, onTrackClick)

            is SearchUiItem.ClearHistoryItem ->
                (holder as ClearHistoryHolder).bind(onClearHistoryClick)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int =
        when (items[position]) {
            is SearchUiItem.TrackItem -> R.layout.track_list
            is SearchUiItem.ClearHistoryItem -> R.layout.clear_history
        }

    fun updateTrackList(newItems: List<SearchUiItem>) {
        val diffCallback = SearchCallback(items, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        items.clear()
        items.addAll(newItems)
        diffResult.dispatchUpdatesTo(this)
    }
}