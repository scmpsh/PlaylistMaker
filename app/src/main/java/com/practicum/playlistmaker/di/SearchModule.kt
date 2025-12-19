package com.practicum.playlistmaker.di

import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.SearchRepositoryImpl
import com.practicum.playlistmaker.search.data.dto.TrackDto
import com.practicum.playlistmaker.search.data.network.ItunesApi
import com.practicum.playlistmaker.search.data.network.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchHistoryRepository
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.api.SearchRepository
import com.practicum.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.util.storage.PrefsStorageClient
import com.practicum.playlistmaker.util.storage.StorageClient
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

private const val ITUNES_URL = "https://itunes.apple.com"
private const val TRACK_LIST_KEY = "TRACK_LIST_KEY"
private const val HISTORY_STORAGE = "HISTORY_STORAGE"

val searchModule = module {

//    Data

    single {
        Retrofit.Builder()
            .baseUrl(ITUNES_URL)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }

    single<ItunesApi> { get<Retrofit>().create(ItunesApi::class.java) }

    single<NetworkClient> { RetrofitNetworkClient(get()) }

    single<StorageClient<ArrayList<TrackDto>>>(named(HISTORY_STORAGE)) {
        PrefsStorageClient(
            get(),
            TRACK_LIST_KEY,
            object : TypeToken<ArrayList<TrackDto>>() {}.type,
            get(),
        )
    }

    single<SearchRepository> { SearchRepositoryImpl(get()) }

    single<SearchHistoryRepository> { SearchHistoryRepositoryImpl(get(named(HISTORY_STORAGE))) }

//    Domain

    single<Executor> { Executors.newCachedThreadPool() }

    single<SearchInteractor> { SearchInteractorImpl(get(), get()) }

    single<SearchHistoryInteractor> { SearchHistoryInteractorImpl(get()) }

//    ViewModel

    viewModel { SearchViewModel(get(), get(), get()) }
}