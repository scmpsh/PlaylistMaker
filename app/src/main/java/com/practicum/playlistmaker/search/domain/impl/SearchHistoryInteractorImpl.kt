package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun saveToHistory(track: Track) {
        val tracks = repository.getHistory()?.data?.toMutableList() ?: arrayListOf()

        if (tracks.none { it.trackId == track.trackId }
            && tracks.size == MAX_HISTORY_SIZE) {
            removeAt(tracks.lastIndex)
        }
        if (tracks.any { it.trackId == track.trackId }) {
            removeAt(tracks.indexOf(track))
        }
        repository.saveToHistory(track)
    }

    private fun removeAt(index: Int) {
        repository.removeAt(index)
    }

    override fun getHistory(): List<Track>? = repository.getHistory()?.data


    override fun cleanHistory() {
        repository.cleanHistory()
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }
}