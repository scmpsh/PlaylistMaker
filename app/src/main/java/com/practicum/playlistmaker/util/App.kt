package com.practicum.playlistmaker.util

import android.app.Application
import com.practicum.playlistmaker.di.commonModule
import com.practicum.playlistmaker.di.playerModule
import com.practicum.playlistmaker.di.searchModule
import com.practicum.playlistmaker.di.settingsModule
import com.practicum.playlistmaker.di.sharingModule
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                commonModule,
                playerModule,
                searchModule,
                settingsModule,
                sharingModule
            )
        }

        onCreateTheme()
    }

    private fun onCreateTheme() {
        val settingsInteractor by inject<SettingsInteractor>()
        val themeSettings = settingsInteractor.getThemeSettings()
        settingsInteractor.updateThemeSetting(themeSettings)
    }
}