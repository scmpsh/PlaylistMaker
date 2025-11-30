package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.api.AudioPlayerRepository
import com.practicum.playlistmaker.domain.api.PlayerState

class AudioPlayerInteractorImpl(
    private val audioPlayerRepository: AudioPlayerRepository
) : AudioPlayerInteractor {

    override fun preparePlayer(
        url: String,
        onComplete: () -> Unit
    ) {
        audioPlayerRepository.preparePlayer(url, onComplete)
    }

    override fun startPlayer() {
        audioPlayerRepository.startPlayer()
    }

    override fun pausePlayer() {
        audioPlayerRepository.pausePlayer()
    }

    override fun releasePlayer() {
        audioPlayerRepository.releasePlayer()
    }

    override fun getCurrentPosition(): Int {
        return audioPlayerRepository.getCurrentPosition()
    }

    override fun seekTo(msec: Int) {
        audioPlayerRepository.seekTo(msec)
    }
}