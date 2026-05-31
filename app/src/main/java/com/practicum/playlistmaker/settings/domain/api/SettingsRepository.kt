package com.practicum.playlistmaker.settings.domain.api

import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getThemeSettings(): ThemeSettings

    fun updateThemeSetting(settings: ThemeSettings)

    fun getThemeSettingsFlow(): Flow<ThemeSettings>

}
