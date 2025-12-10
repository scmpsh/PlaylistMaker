package com.practicum.playlistmaker.search.domain.impl

import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.util.Resource
import java.util.concurrent.Executors

class SearchInteractorImpl(
    private val repository: SearchRepository,
) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(
        term: String,
        consumer: SearchInteractor.SearchConsumer
    ) {
        executor.execute {
            when (val resource = repository.searchTracks(term)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }
}