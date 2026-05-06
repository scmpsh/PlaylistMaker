package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    viewModel { (trackId: Int) ->
        PlayerViewModel(
            trackId = trackId,
            favoriteTrackInteractor = get(),
            playlistInteractor = get(),
        )
    }
}