package com.practicum.playlistmaker.search.domain.api;

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource

interface SearchHistoryRepository {
    fun saveToHistory(track: Track)
    fun getHistory(): Resource<List<Track>>?
    fun cleanHistory()
    fun removeAt(index: Int)
}
