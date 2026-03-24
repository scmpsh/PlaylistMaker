package com.practicum.playlistmaker.media.ui.view_model

import com.practicum.playlistmaker.media.domain.dto.Playlist

sealed interface PlaylistState {
    object Empty : PlaylistState
    data class Content(val playlists: List<Playlist>) : PlaylistState
}