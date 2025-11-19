package com.practicum.playlistmaker.domain.api

interface SettingsRepository {

    fun isDarkThemeEnabled(): Boolean

    fun updateTheme(darkThemeEnabled: Boolean)

}