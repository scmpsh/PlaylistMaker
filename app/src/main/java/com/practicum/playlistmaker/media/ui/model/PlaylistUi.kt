package com.practicum.playlistmaker.media.ui.model

data class PlaylistUi(
    val id: Int,
    val name: String,
    val coverUri: String?,
    val tracksCount: Int
)
