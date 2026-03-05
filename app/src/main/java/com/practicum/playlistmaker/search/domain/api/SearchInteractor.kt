package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.sharing.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {

    fun searchTracks(term: String): Flow<Pair<List<Track>?, String?>>
}