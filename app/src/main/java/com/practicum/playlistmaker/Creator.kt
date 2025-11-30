package com.practicum.playlistmaker

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.data.network.ItunesApi
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.player.AudioPlayerRepositoryImpl
import com.practicum.playlistmaker.data.storage.APP_PREFERENCES
import com.practicum.playlistmaker.data.storage.SettingsRepositoryImpl
import com.practicum.playlistmaker.data.storage.SharedPrefHistoryStorage
import com.practicum.playlistmaker.data.storage.SharedPrefHistoryStorageImpl
import com.practicum.playlistmaker.data.tracks.TrackListRepositoryImpl
import com.practicum.playlistmaker.domain.api.AudioPlayerInteractor
import com.practicum.playlistmaker.domain.api.AudioPlayerRepository
import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SettingsRepository
import com.practicum.playlistmaker.domain.api.TrackListInteractor
import com.practicum.playlistmaker.domain.api.TrackListRepository
import com.practicum.playlistmaker.domain.impl.AudioPlayerInteractorImpl
import com.practicum.playlistmaker.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.domain.impl.TrackListInteractorImpl
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {

    private const val ITUNES_URL = "https://itunes.apple.com"
    // Поле для хранения ApplicationContext
    private lateinit var context: Application

    // Метод инициализации. Вызовите его в Application классе.
    fun init(application: Application) {
        this.context = application
    }

    fun getItunesApi(): ItunesApi {
        return Retrofit.Builder()
            .baseUrl(ITUNES_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ItunesApi::class.java)
    }

    fun getMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }

    fun getHistoryStorage(): SharedPrefHistoryStorage {
        return SharedPrefHistoryStorageImpl(
            context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE),
            Gson()
        )
    }

    fun getSharedPreferences(): SharedPreferences =
        context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)

    private fun getTrackListRepository(
        sharedPrefHistoryStorage: SharedPrefHistoryStorage
    ): TrackListRepository {
        return TrackListRepositoryImpl(
            RetrofitNetworkClient(getItunesApi()),
            sharedPrefHistoryStorage
        )
    }

    fun provideTrackListInteractor(): TrackListInteractor {
        return TrackListInteractorImpl(getTrackListRepository(getHistoryStorage()))
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(context, getSharedPreferences())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    private fun getAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl(getMediaPlayer())
    }

    fun provideAudioPlayerInteractor(): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(getAudioPlayerRepository())
    }
}