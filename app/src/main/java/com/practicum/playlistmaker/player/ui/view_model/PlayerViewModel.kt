package com.practicum.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.api.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.dto.Playlist
import com.practicum.playlistmaker.player.ui.mapper.toDomain
import com.practicum.playlistmaker.player.ui.model.TrackUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val url: String,
    private val trackId: Int,
    private val mediaPlayer: MediaPlayer,
    private val favoriteTrackInteractor: FavoriteTrackInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {

    private var timerJob: Job? = null

    private var isFavorite = false

    private var playlists = emptyList<Playlist>()


    private val playerStateLiveData =
        MutableLiveData<PlayerState>(PlayerState.Default(isFavorite))

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    init {
        preparePlayer()
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
        mediaPlayer.stop()
        mediaPlayer.release()
        renderState(PlayerState.Default())
        timerJob?.cancel()
    }

    fun onPlayButtonClicked() {
        when (playerStateLiveData.value) {
            is PlayerState.Playing -> {
                pausePlayer()
            }

            is PlayerState.Prepared, is PlayerState.Paused -> {
                startPlayer()
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

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            renderState(PlayerState.Prepared(isFavorite))
        }
        mediaPlayer.setOnCompletionListener {
            renderState(PlayerState.Prepared(isFavorite))
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        renderState(
            PlayerState.Playing(getCurrentPlayerPosition(), isFavorite)
        )
        startTimerUpdate()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        pauseTimer()
        renderState(
            PlayerState.Paused(getCurrentPlayerPosition(), isFavorite)
        )
    }

    private fun startTimerUpdate() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(PLAY_TIME_RENDER_DELAY_MILLIS)
                renderState(
                    PlayerState.Playing(getCurrentPlayerPosition(), isFavorite, playlists)
                )
            }
        }
    }

    private fun renderState(state: PlayerState) {
        playerStateLiveData.postValue(state)
    }

    private fun pauseTimer() {
        timerJob?.cancel()
    }

    private fun resetTimer() {
        timerJob?.cancel()
        renderState(PlayerState.Prepared(isFavorite))
        if (mediaPlayer.currentPosition > 0) {
            mediaPlayer.seekTo(0)
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition)
            ?: DEFAULT_TIME
    }

    fun getAllPlaylists() {
        viewModelScope.launch {
            playlistInteractor.findAllPlaylists().collect {
                playlists = it
                updateFavoriteInCurrentState()
            }
        }
    }

    companion object {
        private const val PLAY_TIME_RENDER_DELAY_MILLIS = 300L
        private const val DEFAULT_TIME = "00:00"
    }
}