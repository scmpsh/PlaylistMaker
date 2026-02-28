package com.practicum.playlistmaker.media.domain.impl

import com.practicum.playlistmaker.media.domain.api.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.api.FavoriteTrackRepository
import com.practicum.playlistmaker.sharing.domain.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTrackInteractorImpl(
    private val favoriteTrackRepository: FavoriteTrackRepository
) : FavoriteTrackInteractor {

    override suspend fun addFavoriteTrack(track: Track) {
        favoriteTrackRepository.addFavoriteTrack(track)
    }

    override suspend fun removeFavoriteTrack(track: Track) {
        favoriteTrackRepository.removeFavoriteTrack(track)
    }

    override fun findAllFavoriteTracks(): Flow<List<Track>> {
        return favoriteTrackRepository.findAllFavoriteTracks()
    }

    override fun findAllFavoriteTrackIds(): Flow<List<Int>> {
        return favoriteTrackRepository.findAllFavoriteTrackIds()
    }
}