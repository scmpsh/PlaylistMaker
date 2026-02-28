package com.practicum.playlistmaker.search.data

import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.data.mapper.mapToDomain
import com.practicum.playlistmaker.search.data.mapper.toDto
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.sharing.domain.model.Track
import com.practicum.playlistmaker.utils.Resource
import com.practicum.playlistmaker.utils.storage.StorageClient

class SearchHistoryRepositoryImpl(
    private val storageClient: StorageClient<ArrayList<TrackDto>>,
) : SearchHistoryRepository {

    override fun saveToHistory(track: Track) {
        val tracks = storageClient.getData() ?: arrayListOf()

        tracks.removeIf { it.trackId == track.trackId }
        tracks.add(0, track.toDto())

        if (tracks.size > MAX_HISTORY_SIZE) {
            tracks.removeAt(tracks.lastIndex)
        }

        storageClient.storeData(tracks)
    }

    override fun getHistory(): Resource<List<Track>>? {
        val tracks = storageClient.getData()
        return when {
            tracks == null -> null
            tracks.isEmpty() -> null
            else -> {
                val historyTracks = tracks.map { it.mapToDomain() }
                Resource.Success(historyTracks)
            }
        }
    }

    override fun cleanHistory() {
        storageClient.removeData()
    }

    companion object {
        private const val MAX_HISTORY_SIZE = 10
    }
}
