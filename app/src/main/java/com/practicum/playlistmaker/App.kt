package com.practicum.playlistmaker

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)

        val settingsInteractor = Creator.provideSettingsInteractor()
        val darkTheme = settingsInteractor.isDarkThemeEnabled()

        settingsInteractor.updateTheme(darkTheme)
    }
}