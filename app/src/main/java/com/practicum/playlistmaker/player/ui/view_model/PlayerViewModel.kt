package com.practicum.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val url: String,
    private val mediaPlayer: MediaPlayer
) : ViewModel() {

    private val playerStateLiveData =
        MutableLiveData<PlayerState>(PlayerState.Default())

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private var timerJob: Job? = null

    init {
        preparePlayer()
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

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            renderState(PlayerState.Prepared())
        }
        mediaPlayer.setOnCompletionListener {
            renderState(PlayerState.Prepared())
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        renderState(
            PlayerState.Playing(getCurrentPlayerPosition())
        )
        startTimerUpdate()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        pauseTimer()
        renderState(
            PlayerState.Paused(getCurrentPlayerPosition())
        )
    }

    private fun startTimerUpdate() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(PLAY_TIME_RENDER_DELAY_MILLIS)
                renderState(
                    PlayerState.Playing(getCurrentPlayerPosition())
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
        renderState(PlayerState.Prepared())
        if (mediaPlayer.currentPosition > 0) {
            mediaPlayer.seekTo(0)
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            ?: DEFAULT_TIME
    }

    companion object {
        private const val PLAY_TIME_RENDER_DELAY_MILLIS = 300L
        private const val DEFAULT_TIME = "00:00"
    }
}
