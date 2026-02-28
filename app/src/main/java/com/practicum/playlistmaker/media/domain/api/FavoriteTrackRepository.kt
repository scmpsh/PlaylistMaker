package com.practicum.playlistmaker.media.domain.api

import com.practicum.playlistmaker.sharing.domain.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTrackRepository {

    suspend fun addFavoriteTrack(track: Track)

    suspend fun removeFavoriteTrack(track: Track)

    fun findAllFavoriteTracks(): Flow<List<Track>>

    fun findAllFavoriteTrackIds(): Flow<List<Int>>
}