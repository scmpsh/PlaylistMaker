package com.practicum.playlistmaker.settings.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor,
) : ViewModel() {

    private val settingsStateLiveData = MutableLiveData<ThemeSettings>()
    fun observeSettingsState(): LiveData<ThemeSettings> = settingsStateLiveData

    init {
        settingsStateLiveData.value = settingsInteractor.getThemeSettings()
    }

    fun onThemeSwitchClicked(isDarkEnabled: Boolean) {
        val settings = ThemeSettings(isDarkEnabled)
        settingsInteractor.updateThemeSetting(settings)
        settingsStateLiveData.value = settings
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

    companion object {
        fun getFactory(
            sharingInteractor: SharingInteractor,
            settingsInteractor: SettingsInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(
                    sharingInteractor = sharingInteractor,
                    settingsInteractor = settingsInteractor,
                )
            }
        }
    }
}
