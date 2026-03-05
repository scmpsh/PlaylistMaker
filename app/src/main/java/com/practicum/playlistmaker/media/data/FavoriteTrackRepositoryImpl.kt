package com.practicum.playlistmaker.media.data

import com.practicum.playlistmaker.media.data.db.TrackDbConvertor
import com.practicum.playlistmaker.media.data.db.dao.TrackDao
import com.practicum.playlistmaker.media.domain.api.FavoriteTrackRepository
import com.practicum.playlistmaker.sharing.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class FavoriteTrackRepositoryImpl(
    val trackDao: TrackDao,
    val trackDbConvertor: TrackDbConvertor,
) : FavoriteTrackRepository {

    override suspend fun addFavoriteTrack(track: Track) {
        trackDao.insertTrack(trackDbConvertor.map(track))
    }

    override suspend fun removeFavoriteTrack(track: Track) {
        trackDao.deleteTrack(trackDbConvertor.map(track))
    }

    override fun findAllFavoriteTracks(): Flow<List<Track>> {
        return trackDao.findAllTracks()
            .distinctUntilChanged()
            .map { tracks ->
                tracks
                    .sortedByDescending { it.createDate }
                    .map { trackDbConvertor.map(it, true) }
            }
    }

    override fun findAllFavoriteTrackIds(): Flow<List<Int>> = flow {
        val result = trackDao.findAllTracksIds()
        emit(result)
    }
}