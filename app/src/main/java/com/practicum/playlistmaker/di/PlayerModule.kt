package com.practicum.playlistmaker.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    factory {
        MediaPlayer()
    }

    viewModel { (url: String, trackId: Int) ->
        PlayerViewModel(
            url = url,
            trackId = trackId,
            mediaPlayer = get(),
            favoriteTrackInteractor = get(),
        )
    }
}