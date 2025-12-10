package com.practicum.playlistmaker.settings.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private var viewModel: SettingsViewModel? = null

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

        viewModel = ViewModelProvider(
            this, SettingsViewModel.getFactory(
                Creator.provideSharingInteractor(),
                Creator.provideSettingsInteractor()
            )
        )[SettingsViewModel::class.java]

        viewModel?.observeSettingsState()?.observe(this) {
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
            viewModel?.onThemeSwitchClicked(isChecked)
        }
    }

    private fun onLicenseButtonClick() {
        binding.licenseButton.setOnClickListener {
            viewModel?.openLicense()
        }
    }

    private fun onSupportMessageButtonClick() {
        binding.supportButton.setOnClickListener {
            viewModel?.openSupport()
        }
    }

    private fun onShareButtonClick() {
        binding.shareButton.setOnClickListener {
            viewModel?.shareApp()
        }
    }
}