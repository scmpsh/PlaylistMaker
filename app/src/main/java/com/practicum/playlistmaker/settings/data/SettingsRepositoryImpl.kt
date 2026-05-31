package com.practicum.playlistmaker.settings.data

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.utils.storage.StorageClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepositoryImpl(
    private val storageClient: StorageClient<ThemeSettings>,
    private val context: Context,
) : SettingsRepository {

    private val themeSettingsFlow = MutableStateFlow(getThemeSettings())

    override fun getThemeSettings(): ThemeSettings {
        val systemIsInDarkMode =
            (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        return storageClient.getData() ?: ThemeSettings(systemIsInDarkMode)
    }

    override fun getThemeSettingsFlow(): Flow<ThemeSettings> = themeSettingsFlow.asStateFlow()

    override fun updateThemeSetting(settings: ThemeSettings) {
        storageClient.storeData(settings)
        themeSettingsFlow.value = settings
        AppCompatDelegate.setDefaultNightMode(
            if (settings.isDarkEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
