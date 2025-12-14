package com.practicum.playlistmaker.player.ui.mapper

import com.practicum.playlistmaker.player.ui.model.TrackUi
import com.practicum.playlistmaker.search.domain.models.Track

fun Track.toUi(): TrackUi = TrackUi(
    trackId = this.trackId,
    trackName = this.trackName,
    artistName = this.artistName,
    trackTime = this.trackTime,
    artworkUrl100 = this.artworkUrl100,
    collectionName = this.collectionName,
    releaseDate = this.releaseDate,
    primaryGenreName = this.primaryGenreName,
    country = this.country,
    previewUrl = this.previewUrl
)
