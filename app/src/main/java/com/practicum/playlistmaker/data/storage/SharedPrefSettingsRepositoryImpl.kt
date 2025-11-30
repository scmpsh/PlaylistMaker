package com.practicum.playlistmaker.data.storage

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import com.practicum.playlistmaker.domain.api.SettingsRepository

const val DARK_THEME_KEY = "DARK_THEME_KEY"
const val APP_PREFERENCES = "APP_PREFERENCES"

class SettingsRepositoryImpl(
    private val context: Context,
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {

    override fun isDarkThemeEnabled(): Boolean {
        val systemIsInDarkMode =
            (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        return sharedPreferences.getBoolean(DARK_THEME_KEY, systemIsInDarkMode)
    }

    override fun updateTheme(darkThemeEnabled: Boolean) {
        sharedPreferences.edit {
            putBoolean(DARK_THEME_KEY, darkThemeEnabled)
        }
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}