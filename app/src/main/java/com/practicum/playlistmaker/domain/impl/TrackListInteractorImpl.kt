package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.TrackListInteractor
import com.practicum.playlistmaker.domain.api.TrackListRepository
import com.practicum.playlistmaker.domain.models.Track
import java.util.concurrent.Executors

class TrackListInteractorImpl(
    private val repository: TrackListRepository
) : TrackListInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        term: String,
        consumer: TrackListInteractor.TrackListConsumer
    ) {
        executor.execute {
            consumer.consume(repository.searchTracks(term))
        }
    }

    override fun addTrackToHistory(track: Track) {
        repository.addTrackToHistory(track)
    }

    override fun getHistory(): List<Track> {
       return repository.getHistory()
    }

    override fun cleanHistory() {
        repository.cleanHistory()
    }
}