package com.practicum.playlistmaker.di

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.ui.view_model.PlayerViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {

    factory {
        MediaPlayer()
    }

    viewModel { (url: String) ->
        PlayerViewModel(
            url = url,
            mediaPlayer = get(),
            handler = get()
        )
    }
}