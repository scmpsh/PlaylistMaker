package com.practicum.playlistmaker.di

import androidx.room.Room
import com.practicum.playlistmaker.media.data.CoverStorageRepositoryImpl
import com.practicum.playlistmaker.media.data.FavoriteTrackRepositoryImpl
import com.practicum.playlistmaker.media.data.PlaylistRepositoryImpl
import com.practicum.playlistmaker.media.data.db.AppDatabase
import com.practicum.playlistmaker.media.data.db.PlaylistDbConvertor
import com.practicum.playlistmaker.media.data.db.TrackDbConvertor
import com.practicum.playlistmaker.media.domain.api.CoverStorageInteractor
import com.practicum.playlistmaker.media.domain.api.CoverStorageRepository
import com.practicum.playlistmaker.media.domain.api.FavoriteTrackInteractor
import com.practicum.playlistmaker.media.domain.api.FavoriteTrackRepository
import com.practicum.playlistmaker.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.api.PlaylistRepository
import com.practicum.playlistmaker.media.domain.impl.CoverStorageInteractorImpl
import com.practicum.playlistmaker.media.domain.impl.FavoriteTrackInteractorImpl
import com.practicum.playlistmaker.media.domain.impl.PlaylistInteractorImpl
import com.practicum.playlistmaker.media.ui.view_model.EditPlaylistViewModel
import com.practicum.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.media.ui.view_model.PlaylistDetailsViewModel
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mediaModule = module {
    viewModel { PlaylistViewModel(get(), get()) }
    viewModel { (playlistId: Int) -> EditPlaylistViewModel(playlistId, get(), get()) }
    viewModel { FavoriteTracksViewModel(get()) }
    viewModel { (playlistId: Int) -> PlaylistDetailsViewModel(playlistId, get(), get()) }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

    single { get<AppDatabase>().favoriteTrackDao() }

    single { get<AppDatabase>().playlistDao() }

    single { get<AppDatabase>().tracksInPlaylistsDao() }

    factory {
        TrackDbConvertor()
    }

    factory {
        PlaylistDbConvertor()
    }

    single<FavoriteTrackRepository> {
        FavoriteTrackRepositoryImpl(get(), get())
    }

    single<FavoriteTrackInteractor> { FavoriteTrackInteractorImpl(get()) }

    single<PlaylistRepository> { PlaylistRepositoryImpl(get(), get(), get()) }

    single<PlaylistInteractor> { PlaylistInteractorImpl(get()) }

    single<CoverStorageRepository> { CoverStorageRepositoryImpl(androidContext()) }

    single<CoverStorageInteractor> { CoverStorageInteractorImpl(get()) }
}
