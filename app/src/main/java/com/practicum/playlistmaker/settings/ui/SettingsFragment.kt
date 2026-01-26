package com.practicum.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.observeSettingsState().observe(viewLifecycleOwner) {
            binding.switchTheme.isChecked = it.isDarkEnabled
        }

        onSwitchThemeClick()
        onShareButtonClick()
        onSupportMessageButtonClick()
        onLicenseButtonClick()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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