package com.practicum.playlistmaker.util

import android.app.Application
import com.practicum.playlistmaker.creator.Creator

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Creator.init(this)

        val settingsInteractor = Creator.provideSettingsInteractor()
        val themeSettings = settingsInteractor.getThemeSettings()
        settingsInteractor.updateThemeSetting(themeSettings)
    }
}