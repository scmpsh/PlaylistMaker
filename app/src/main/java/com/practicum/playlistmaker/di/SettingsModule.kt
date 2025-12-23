package com.practicum.playlistmaker.di

import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.api.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.api.SettingsRepository
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.domain.model.ThemeSettings
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.util.storage.PrefsStorageClient
import com.practicum.playlistmaker.util.storage.StorageClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

private const val DARK_THEME_KEY = "DARK_THEME_KEY"
private const val THEME_STORAGE = "THEME_STORAGE"

val settingsModule = module {

    single<StorageClient<ThemeSettings>>(named(THEME_STORAGE)) {
        PrefsStorageClient(
            get(),
            DARK_THEME_KEY,
            object : TypeToken<ThemeSettings>() {}.type,
            get()
        )
    }

    single<SettingsRepository> { SettingsRepositoryImpl(get(named(THEME_STORAGE)), androidContext()) }

    single<SettingsInteractor> { SettingsInteractorImpl(get()) }

    viewModel { SettingsViewModel(get(), get()) }
}