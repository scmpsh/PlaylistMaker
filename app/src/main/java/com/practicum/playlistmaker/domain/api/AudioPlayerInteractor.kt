package com.practicum.playlistmaker.domain.api

interface AudioPlayerInteractor {
    fun preparePlayer(url: String, onComplete: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun seekTo(msec: Int)
}

enum class PlayerState {
    DEFAULT, PREPARED, PLAYING, PAUSED
}