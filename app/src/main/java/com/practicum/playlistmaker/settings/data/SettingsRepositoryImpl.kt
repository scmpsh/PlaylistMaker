package com.practicum.playlistmaker.settings.data

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.util.storage.StorageClient

class SettingsRepositoryImpl(
    private val storageClient: StorageClient<ThemeSettings>,
    private val context: Context,
) : SettingsRepository {

    override fun getThemeSettings(): ThemeSettings {
        val systemIsInDarkMode =
            (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        return storageClient.getData() ?: ThemeSettings(systemIsInDarkMode)
    }

    override fun updateThemeSetting(settings: ThemeSettings) {
        storageClient.storeData(settings)
        AppCompatDelegate.setDefaultNightMode(
            if (settings.isDarkEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}