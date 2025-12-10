package com.practicum.playlistmaker.search.ui

import androidx.recyclerview.widget.DiffUtil
import com.practicum.playlistmaker.search.ui.view_model.SearchUiItem

class SearchCallback(
    private val oldList: List<SearchUiItem>,
    private val newList: List<SearchUiItem>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size
    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
        val oldItem = oldList[oldPos]
        val newItem = newList[newPos]

        return if (oldItem is SearchUiItem.TrackItem && newItem is SearchUiItem.TrackItem) {
            oldItem.track.trackId == newItem.track.trackId
        } else {
            oldItem == newItem
        }
    }


    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}