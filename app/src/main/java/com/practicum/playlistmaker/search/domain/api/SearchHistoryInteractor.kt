package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.sharing.domain.model.Track

interface SearchHistoryInteractor {
    fun saveToHistory(track: Track)
    fun getHistory(): List<Track>?
    fun cleanHistory()
}