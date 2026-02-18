package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {

    fun searchTracks(term: String): Flow<Pair<List<Track>?, String?>>
}