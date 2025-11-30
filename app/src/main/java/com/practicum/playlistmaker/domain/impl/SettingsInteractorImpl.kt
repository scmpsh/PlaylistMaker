package com.practicum.playlistmaker.domain.impl

import com.practicum.playlistmaker.domain.api.SettingsInteractor
import com.practicum.playlistmaker.domain.api.SettingsRepository

class SettingsInteractorImpl(
    private val settingsRepository: SettingsRepository
) : SettingsInteractor {

    override fun isDarkThemeEnabled(): Boolean =
        settingsRepository.isDarkThemeEnabled()


    override fun updateTheme(darkThemeEnabled: Boolean) =
        settingsRepository.updateTheme(darkThemeEnabled)

}