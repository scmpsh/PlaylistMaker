package com.practicum.playlistmaker.di

import androidx.room.Room
import com.practicum.playlistmaker.media.data.FavoriteTrackRepositoryImpl
import com.practicum.playlistmaker.media.data.db.AppDatabase
import com.practicum.playlistmaker.media.data.db.TrackDbConvertor
import com.practicum.playlistmaker.media.domain.api.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.api.FavoriteTrackRepository
import com.practicum.playlistmaker.media.domain.impl.FavoriteTrackInteractorImpl
import com.practicum.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    viewModel { PlaylistViewModel() }
    viewModel { FavoriteTracksViewModel(get()) }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    single { get<AppDatabase>().favoriteTrackDao() }

    factory {
        TrackDbConvertor()
    }

    single<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(get(), get())
    }

    single<FavoriteTrackInteractor> { FavoriteTrackInteractorImpl(get()) }
}