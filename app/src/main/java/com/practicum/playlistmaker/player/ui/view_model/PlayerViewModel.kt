package com.practicum.playlistmaker.player.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.api.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.dto.Playlist
import com.practicum.playlistmaker.player.service.AudioPlayerState
import com.practicum.playlistmaker.player.service.PlayerServiceControl
import com.practicum.playlistmaker.player.ui.mapper.toDomain
import com.practicum.playlistmaker.player.ui.model.TrackUi
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val trackId: Int,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private var playerServiceControl: PlayerServiceControl? = null

    private var isFavorite = false

    private var playlists = emptyList<Playlist>()

    private val playerStateLiveData =
        MutableLiveData<PlayerState>(PlayerState.Default(isFavorite))

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    fun setService(service: PlayerServiceControl, track: TrackUi) {
        playerServiceControl = service
        playerServiceControl?.setTrack(track.previewUrl, track.trackName, track.artistName)
        viewModelScope.launch {
            playerServiceControl?.getPlayerState()?.collect { state ->
                handleServiceState(state)
            }
        }
    }

    private fun handleServiceState(state: AudioPlayerState) {
        val newState = when (state) {
            is AudioPlayerState.Default -> PlayerState.Default(isFavorite, playlists)
            is AudioPlayerState.Prepared -> PlayerState.Prepared(isFavorite, playlists)
            is AudioPlayerState.Playing -> PlayerState.Playing(
                state.progress,
                isFavorite,
                playlists
            )
            is AudioPlayerState.Paused -> PlayerState.Paused(
                state.progress,
                isFavorite,
                playlists
            )
        }
        renderState(newState)
    }

    fun onResume() {
        playerServiceControl?.hideNotification()
    }

    fun onPause() {
        playerServiceControl?.showNotification()
    }

    fun initFavoriteStatus() {
        viewModelScope.launch {
            favoriteTrackInteractor.findAllFavoriteTrackIds().collect {
                isFavorite = it.contains(trackId)
                updateFavoriteInCurrentState()
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist, trackUi: TrackUi): Boolean {
        if (playlist.tracks.contains(trackUi.trackId)) {
            return false
        } else {
            viewModelScope.launch {
                playlistInteractor.addTrackToPlaylist(playlist, trackUi.toDomain())
                updateFavoriteInCurrentState()
            }
            return true
        }
    }

    override fun onCleared() {
        super.onCleared()
        playerServiceControl?.pausePlayer()
    }

    fun onPlayButtonClicked() {
        when (playerServiceControl?.getPlayerState()?.value) {
            is AudioPlayerState.Playing -> {
                playerServiceControl?.pausePlayer()
            }

            is AudioPlayerState.Prepared, is AudioPlayerState.Paused -> {
                playerServiceControl?.startPlayer()
            }

            else -> {}
        }
    }

    fun onFavoriteClicked(trackUi: TrackUi) {
        isFavorite = !isFavorite

        viewModelScope.launch {
            if (isFavorite) {
                favoriteTrackInteractor.addFavoriteTrack(trackUi.toDomain())
            } else {
                favoriteTrackInteractor.removeFavoriteTrack(trackUi.toDomain())
            }
            updateFavoriteInCurrentState()
        }
    }

    private fun updateFavoriteInCurrentState() {
        val newState = when (val currentState = playerStateLiveData.value) {
            is PlayerState.Playing -> PlayerState.Playing(
                currentState.progress,
                isFavorite,
                playlists
            )

            is PlayerState.Paused -> PlayerState.Paused(
                currentState.progress,
                isFavorite,
                playlists
            )

            is PlayerState.Prepared -> PlayerState.Prepared(isFavorite, playlists)
            else -> PlayerState.Default(isFavorite, playlists)
        }
        renderState(newState)
    }

    private fun renderState(state: PlayerState) {
        playerStateLiveData.postValue(state)
    }

    fun getAllPlaylists() {
        viewModelScope.launch {
            playlistInteractor.findAllPlaylists().collect {
                playlists = it
                updateFavoriteInCurrentState()
            }
        }
    }
}
