package com.practicum.playlistmaker.data.tracks

import com.practicum.playlistmaker.data.dto.ItunesRequest
import com.practicum.playlistmaker.data.dto.ItunesResponse
import com.practicum.playlistmaker.data.network.NetworkClient
import com.practicum.playlistmaker.data.storage.SharedPrefHistoryStorage
import com.practicum.playlistmaker.domain.api.TrackListRepository
import com.practicum.playlistmaker.domain.mapper.mapToDomain
import com.practicum.playlistmaker.domain.mapper.toDto
import com.practicum.playlistmaker.domain.models.SearchResult
import com.practicum.playlistmaker.domain.models.Track

class TrackListRepositoryImpl(
    private val networkClient: NetworkClient,
    private val sharedPrefHistoryStorage: SharedPrefHistoryStorage
) : TrackListRepository {

    override fun searchTracks(term: String): SearchResult {
        val response = networkClient.doRequest(ItunesRequest(term))
        if (response.resultCode == 200) {
            val itunesResponse = (response as ItunesResponse)
            if (itunesResponse.results.isEmpty()) {
                return SearchResult.Empty
            }
            val resultList = itunesResponse.results.map { it.mapToDomain() }
            return SearchResult.Success(resultList)
        } else {
            return SearchResult.Error
        }
    }

    override fun addTrackToHistory(track: Track) {
        sharedPrefHistoryStorage.write(track.toDto())
    }

    override fun getHistory(): List<Track> {
        return sharedPrefHistoryStorage.read().map { it.mapToDomain() }
    }

    override fun cleanHistory() {
        sharedPrefHistoryStorage.cleanCachedTrackList()
    }
}