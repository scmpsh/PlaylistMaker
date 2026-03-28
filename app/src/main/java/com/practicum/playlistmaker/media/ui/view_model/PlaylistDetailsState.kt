package com.practicum.playlistmaker.media.ui.view_model

import com.practicum.playlistmaker.media.domain.dto.Playlist
import com.practicum.playlistmaker.sharing.domain.model.Track

sealed interface PlaylistDetailsState {
    object Loading : PlaylistDetailsState
    data class Content(
        val playlist: Playlist,
        val tracks: List<Track>,
        val totalDuration: Int,
        val shareMessage: String? = null
    ) : PlaylistDetailsState
    object Deleted : PlaylistDetailsState
}