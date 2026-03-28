package com.practicum.playlistmaker.sharing.domain.model

data class Track(
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: String,
    val artworkUrl100: String,
    val collectionName: String? = null,
    val releaseDate: String?,
    val primaryGenreName: String,
    val country: String,
    val previewUrl: String,
)