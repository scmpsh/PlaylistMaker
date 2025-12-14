package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun saveToHistory(track: Track)
    fun getHistory(): List<Track>?
    fun cleanHistory()
}