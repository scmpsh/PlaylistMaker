package com.practicum.playlistmaker.player.ui.view_model

import com.practicum.playlistmaker.media.domain.dto.Playlist

sealed class PlayerState(
    val isPlayButtonEnabled: Boolean,
    val progress: String,
    val isFavorite: Boolean,
    val playlists: List<Playlist>,
) {
    class Default(isFavorite: Boolean = false, playlists: List<Playlist> = emptyList()) :
        PlayerState(true, "00:00", isFavorite, playlists)

    class Prepared(isFavorite: Boolean = false, playlists: List<Playlist> = emptyList()) :
        PlayerState(true, "00:00", isFavorite, playlists)

    class Playing(
        progress: String,
        isFavorite: Boolean = false,
        playlists: List<Playlist> = emptyList()
    ) :
        PlayerState(false, progress, isFavorite, playlists)

    class Paused(
        progress: String,
        isFavorite: Boolean = false,
        playlists: List<Playlist> = emptyList()
    ) :
        PlayerState(true, progress, isFavorite, playlists)
}
