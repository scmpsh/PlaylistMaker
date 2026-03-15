package com.practicum.playlistmaker.media.domain.dto

data class Playlist(
    val id: Int,
    val name: String,
    val description: String,
    val coverImagePath: String,
    val tracks: List<Int>,
    val tracksCount: Int
)
