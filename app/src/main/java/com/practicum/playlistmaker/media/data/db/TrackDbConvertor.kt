package com.practicum.playlistmaker.media.data.db

import com.practicum.playlistmaker.media.data.db.entity.TrackEntity
import com.practicum.playlistmaker.sharing.domain.model.Track
import java.time.OffsetDateTime

class TrackDbConvertor {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate ?: "",
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl,
            createDate = OffsetDateTime.now()
        )
    }

    fun map(trackEntity: TrackEntity, isFavorite: Boolean): Track {
        return Track(
            trackId = trackEntity.trackId,
            trackName = trackEntity.trackName,
            artistName = trackEntity.artistName,
            trackTime = trackEntity.trackTime,
            artworkUrl100 = trackEntity.artworkUrl100,
            collectionName = trackEntity.collectionName,
            releaseDate = trackEntity.releaseDate,
            primaryGenreName = trackEntity.primaryGenreName,
            country = trackEntity.country,
            previewUrl = trackEntity.previewUrl
        )
    }
}