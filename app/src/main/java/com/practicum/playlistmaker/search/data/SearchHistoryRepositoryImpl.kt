package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.data.mapper.mapToDomain
import com.practicum.playlistmaker.search.data.mapper.toDto
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.Resource
import com.practicum.playlistmaker.util.storage.StorageClient

class SearchHistoryRepositoryImpl(
    private val storageClient: StorageClient<ArrayList<TrackDto>>
) : SearchHistoryRepository {

    override fun saveToHistory(track: Track) {
        val tracks = storageClient.getData() ?: arrayListOf()
        tracks.add(0, track.toDto())
        storageClient.storeData(tracks)
    }

    override fun getHistory(): Resource<List<Track>>? {
        val tracks = storageClient.getData()
        return when {
            tracks == null -> null
            tracks.isEmpty() -> null
            else -> Resource.Success(tracks.map { it.mapToDomain() })
        }
    }

    override fun cleanHistory() {
        storageClient.removeData()
    }

    override fun removeAt(index: Int) {
        val tracks = storageClient.getData() ?: arrayListOf()
        tracks.removeAt(index)
        storageClient.storeData(tracks)
    }
}