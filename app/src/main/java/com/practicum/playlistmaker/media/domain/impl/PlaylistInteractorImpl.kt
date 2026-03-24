package com.practicum.playlistmaker.media.domain.impl

import com.practicum.playlistmaker.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.api.PlaylistRepository
import com.practicum.playlistmaker.media.domain.dto.Playlist
import com.practicum.playlistmaker.sharing.domain.model.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl(
    private val playlistRepository: PlaylistRepository
) : PlaylistInteractor {

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistRepository.addPlaylist(playlist)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }

    override suspend fun removePlaylist(playlist: Playlist) {
        playlistRepository.removePlaylist(playlist)
    }

    override fun findAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.findAllPlaylists()
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        playlistRepository.addTrackToPlaylist(playlist, track)
    }
}