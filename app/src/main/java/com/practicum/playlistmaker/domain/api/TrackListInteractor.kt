package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.SearchResult
import com.practicum.playlistmaker.domain.models.Track

interface TrackListInteractor {

    fun searchTracks(term: String, consumer: TrackListConsumer)
    fun addTrackToHistory(track: Track)
    fun getHistory(): List<Track>
    fun cleanHistory()

    interface TrackListConsumer {
        fun consume(searchResult: SearchResult)
    }
}