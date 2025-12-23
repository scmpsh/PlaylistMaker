package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        settingsViewModel.observeSettingsState().observe(this) {
            binding.switchTheme.isChecked = it.isDarkEnabled
        }

        onSwitchThemeClick()
        onShareButtonClick()
        onSupportMessageButtonClick()
        onLicenseButtonClick()

        binding.leaveSettings.setOnClickListener { finish() }
    }

    private fun onSwitchThemeClick() {
        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            settingsViewModel.onThemeSwitchClicked(isChecked)
        }
    }

    private fun onLicenseButtonClick() {
        binding.licenseButton.setOnClickListener {
            settingsViewModel.openLicense()
        }
    }

    private fun onSupportMessageButtonClick() {
        binding.supportButton.setOnClickListener {
            settingsViewModel.openSupport()
        }
    }

    private fun onShareButtonClick() {
        binding.shareButton.setOnClickListener {
            settingsViewModel.shareApp()
        }
    }
}