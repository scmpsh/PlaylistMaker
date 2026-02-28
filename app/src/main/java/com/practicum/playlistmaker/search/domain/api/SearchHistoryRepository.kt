package com.practicum.playlistmaker.search.domain.api;

import com.practicum.playlistmaker.sharing.domain.model.Track
import com.practicum.playlistmaker.utils.Resource

interface SearchHistoryRepository {
    fun saveToHistory(track: Track)
    fun getHistory(): Resource<List<Track>>?
    fun cleanHistory()
}
