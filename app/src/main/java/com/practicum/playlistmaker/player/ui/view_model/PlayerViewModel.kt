package com.practicum.playlistmaker.player.ui.view_model

import android.media.MediaPlayer
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val url: String,
    private val mediaPlayer: MediaPlayer,
    private val handler: Handler
) : ViewModel() {

    private val playerStateLiveData =
        MutableLiveData(PlayerState(PlayerStateType.STATE_DEFAULT, START_TIME))

    fun observePlayerState(): LiveData<PlayerState> = playerStateLiveData

    private val timerRunnable = Runnable {
        if (playerStateLiveData.value?.stateType == PlayerStateType.STATE_PLAYING) {
            startTimerUpdate()
        }
    }

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        handler.removeCallbacks(timerRunnable)
    }

    fun onPlayButtonClicked() {
        if (playerStateLiveData.value?.stateType == PlayerStateType.STATE_PREPARED
            || playerStateLiveData.value?.stateType == PlayerStateType.STATE_PAUSED
        ) {
            startPlayer()
        }
        if (playerStateLiveData.value?.stateType == PlayerStateType.STATE_PLAYING) {
            pausePlayer()
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            renderState(PlayerState(PlayerStateType.STATE_PREPARED, START_TIME))
        }
        mediaPlayer.setOnCompletionListener {
            renderState(PlayerState(PlayerStateType.STATE_PREPARED, START_TIME))
            resetTimer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        renderState(
            PlayerState(
                PlayerStateType.STATE_PLAYING,
                playerStateLiveData.value?.playerTime ?: START_TIME
            )
        )
        startTimerUpdate()
    }

    private fun pausePlayer() {
        pauseTimer()
        mediaPlayer.pause()
        renderState(
            PlayerState(
                PlayerStateType.STATE_PAUSED,
                playerStateLiveData.value?.playerTime ?: START_TIME
            )
        )
    }

    private fun startTimerUpdate() {
        renderState(
            PlayerState(
                PlayerStateType.STATE_PLAYING,
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
            )
        )
        handler.postDelayed(timerRunnable, PLAY_TIME_RENDER_DELAY_MILLIS)
    }

    private fun renderState(state: PlayerState) {
        playerStateLiveData.postValue(state)
    }

    private fun pauseTimer() {
        handler.removeCallbacks(timerRunnable)
    }

    private fun resetTimer() {
        handler.removeCallbacks(timerRunnable)
        renderState(PlayerState(PlayerStateType.STATE_PREPARED, START_TIME))
        if (mediaPlayer.currentPosition > 0) {
            mediaPlayer.seekTo(0)
        }
    }

    companion object {
        private const val PLAY_TIME_RENDER_DELAY_MILLIS = 200L
        private const val START_TIME = "00:00"
    }
}
