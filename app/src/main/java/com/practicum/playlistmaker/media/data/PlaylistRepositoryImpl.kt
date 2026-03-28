package com.practicum.playlistmaker.media.data

import com.practicum.playlistmaker.media.data.db.PlaylistDbConvertor
import com.practicum.playlistmaker.media.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.media.data.db.dao.TracksInPlaylistsDao
import com.practicum.playlistmaker.media.domain.api.PlaylistRepository
import com.practicum.playlistmaker.media.domain.dto.Playlist
import com.practicum.playlistmaker.sharing.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
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
        if (!updatedTracks.contains(track.trackId)) {
            updatedTracks.add(track.trackId)
            val updatedPlaylist = playlist.copy(
                tracks = updatedTracks,
                tracksCount = updatedTracks.size
            )
            updatePlaylist(updatedPlaylist)
            tracksInPlaylistsDao.insertTrack(playlistDbConvertor.map(track))
        }
    }

    override suspend fun getPlaylistById(id: Int): Playlist {
        return playlistDbConvertor.map(playlistDao.getPlaylistById(id))
    }

    override fun getTracksInPlaylist(trackIds: List<Int>): Flow<List<Track>> {
        return tracksInPlaylistsDao.getTracks()
            .distinctUntilChanged()
            .map { tracks ->
                tracks.filter { trackIds.contains(it.trackId) }
                    .map { playlistDbConvertor.map(it) }
                    .sortedByDescending { track ->
                        trackIds.indexOf(track.trackId)
                    }
            }
    }

    override suspend fun removeTrackFromPlaylist(playlist: Playlist, trackId: Int) {
        val updatedTracks = playlist.tracks.toMutableList()
        updatedTracks.remove(trackId)
        val updatedPlaylist = playlist.copy(
            tracks = updatedTracks,
            tracksCount = updatedTracks.size
        )
        updatePlaylist(updatedPlaylist)

        cleanupTrack(trackId)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlistDbConvertor.map(playlist))
        playlist.tracks.forEach { trackId ->
            cleanupTrack(trackId)
        }
    }

    private suspend fun cleanupTrack(trackId: Int) {
        val allPlaylists = findAllPlaylists().first()
        val isTrackInOtherPlaylists = allPlaylists.any { it.tracks.contains(trackId) }

        if (!isTrackInOtherPlaylists) {
            tracksInPlaylistsDao.deleteTrackById(trackId)
        }
    }
}