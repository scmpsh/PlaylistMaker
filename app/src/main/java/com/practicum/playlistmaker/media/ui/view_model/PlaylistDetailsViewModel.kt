package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.model.Track
import kotlinx.coroutines.launch

class PlaylistDetailsViewModel(
    private val playlistId: Int,
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _state = MutableLiveData<PlaylistDetailsState>()
    val state: LiveData<PlaylistDetailsState> = _state

    init {
        loadPlaylistDetails()
    }

    fun loadPlaylistDetails() {
        _state.value = PlaylistDetailsState.Loading
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            playlistInteractor.getTracksInPlaylist(playlist.tracks).collect { tracks ->
                val totalDurationMillis = tracks.sumOf { it.trackTime.toLongOrNull() ?: 0L }
                _state.postValue(
                    PlaylistDetailsState.Content(
                        playlist,
                        tracks,
                        totalDurationMillis.toInt()
                    )
                )
            }
        }
    }

    fun removeTrack(track: Track) {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState is PlaylistDetailsState.Content) {
                playlistInteractor.removeTrackFromPlaylist(currentState.playlist, track.trackId)
                loadPlaylistDetails()
            }
        }
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState is PlaylistDetailsState.Content) {
                playlistInteractor.deletePlaylist(currentState.playlist)
                _state.postValue(PlaylistDetailsState.Deleted)
            }
        }
    }

    fun sharePlaylist(noTracksMessage: String, playlistInfo: String) {
        val currentState = _state.value
        if (currentState is PlaylistDetailsState.Content) {
            if (currentState.tracks.isEmpty()) {
                _state.postValue(currentState.copy(shareMessage = noTracksMessage))
            } else {
                sharingInteractor.sharePlaylist(playlistInfo)
            }
        }
    }
}