package com.practicum.playlistmaker.media.data.db

import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.data.db.entity.TracksInPlaylistsEntity
import com.practicum.playlistmaker.media.domain.dto.Playlist
import com.practicum.playlistmaker.sharing.domain.model.Track
import java.time.OffsetDateTime

class PlaylistDbConvertor {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            description = playlist.description,
            coverImagePath = playlist.coverImagePath,
            tracks = playlist.tracks,
            tracksCount = playlist.tracksCount
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id = playlistEntity.id,
            name = playlistEntity.name,
            description = playlistEntity.description,
            coverImagePath = playlistEntity.coverImagePath,
            tracks = playlistEntity.tracks,
            tracksCount = playlistEntity.tracksCount
        )
    }

    fun map(track: Track): TracksInPlaylistsEntity {
        return TracksInPlaylistsEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            createDate = OffsetDateTime.now()
        )
    }
}