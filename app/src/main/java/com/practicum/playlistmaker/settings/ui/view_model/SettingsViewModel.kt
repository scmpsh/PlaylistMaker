package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val settingsStateLiveData: LiveData<ThemeSettings> = 
        settingsInteractor.getThemeSettingsFlow().asLiveData(viewModelScope.coroutineContext)

    fun observeSettingsState(): LiveData<ThemeSettings> = settingsStateLiveData

    fun onThemeSwitchClicked(isDarkEnabled: Boolean) {
        settingsInteractor.updateThemeSetting(ThemeSettings(isDarkEnabled))
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openLicense() {
        sharingInteractor.openTerms()
    }
}
