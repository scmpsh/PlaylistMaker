package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.SearchResult
import com.practicum.playlistmaker.domain.models.Track

interface TrackListRepository {

    fun searchTracks(term: String): SearchResult
    fun addTrackToHistory(track: Track)
    fun getHistory(): List<Track>
    fun cleanHistory()

}