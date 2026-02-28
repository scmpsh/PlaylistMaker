package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.sharing.domain.model.Track

class SearchHistoryInteractorImpl(
    private val repository: SearchHistoryRepository
) : SearchHistoryInteractor {

    override fun saveToHistory(track: Track) {
        repository.saveToHistory(track)
    }

    override fun getHistory(): List<Track>? = repository.getHistory()?.data

    override fun cleanHistory() {
        repository.cleanHistory()
    }


}