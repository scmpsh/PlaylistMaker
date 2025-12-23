package com.practicum.playlistmaker.di

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val PLAYLIST_PREFS_NAME = "PLAYLIST_PREFS"

val commonModule = module {

    factory { Gson() }

    single {
        Handler(Looper.getMainLooper())
    }

    single {
        androidContext()
            .getSharedPreferences(PLAYLIST_PREFS_NAME, Context.MODE_PRIVATE)
    }
}