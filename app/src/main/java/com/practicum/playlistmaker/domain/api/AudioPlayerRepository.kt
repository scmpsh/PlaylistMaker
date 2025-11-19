package com.practicum.playlistmaker.domain.api

interface AudioPlayerRepository {
    fun preparePlayer(url: String, onComplete: () -> Unit)
    fun startPlayer()
    fun pausePlayer()
    fun releasePlayer()
    fun getCurrentPosition(): Int
    fun seekTo(msec: Int)
}