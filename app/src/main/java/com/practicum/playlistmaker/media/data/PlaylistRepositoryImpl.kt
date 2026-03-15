package com.practicum.playlistmaker.media.data

import com.practicum.playlistmaker.media.data.db.PlaylistDbConvertor
import com.practicum.playlistmaker.media.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.media.data.db.dao.TracksInPlaylistsDao
import com.practicum.playlistmaker.media.domain.api.PlaylistRepository
import com.practicum.playlistmaker.media.domain.dto.Playlist
import com.practicum.playlistmaker.sharing.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val tracksInPlaylistsDao: TracksInPlaylistsDao,
    private val playlistDbConvertor: PlaylistDbConvertor,
) : PlaylistRepository {

    override suspend fun addPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlistDbConvertor.map(playlist))
    }

    override suspend fun removePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlistDbConvertor.map(playlist))
    }

    override fun findAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.findAllPlaylists()
            .distinctUntilChanged()
            .map { playlists ->
                playlists.map { playlistDbConvertor.map(it) }
            }
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        val updatedTracks = playlist.tracks.toMutableList()
        updatedTracks.add(track.trackId)
        val updatedPlaylist = playlist.copy(
            tracks = updatedTracks,
            tracksCount = updatedTracks.size
        )
        updatePlaylist(updatedPlaylist)
        tracksInPlaylistsDao.insertTrack(playlistDbConvertor.map(track))
    }
}