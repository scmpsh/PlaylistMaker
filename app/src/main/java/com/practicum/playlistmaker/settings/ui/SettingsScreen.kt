package com.practicum.playlistmaker.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.ui.LabeledIconRow
import com.practicum.playlistmaker.ui.PlaylistMakerTheme
import com.practicum.playlistmaker.ui.ScreenTitle

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val settingsState by viewModel.observeSettingsState().observeAsState()
    SettingsContent(
        isDarkEnabled = settingsState?.isDarkEnabled ?: false,
        onThemeSwitch = viewModel::onThemeSwitchClicked,
        onShare = viewModel::shareApp,
        onSupport = viewModel::openSupport,
        onLicense = viewModel::openLicense
    )
}

@Composable
fun SettingsContent(
    isDarkEnabled: Boolean,
    onThemeSwitch: (Boolean) -> Unit,
    onShare: () -> Unit,
    onSupport: () -> Unit,
    onLicense: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenTitle(text = stringResource(R.string.settings))
        Spacer(modifier = Modifier.height(24.dp))

        LabeledIconRow(
            text = stringResource(R.string.theme_option),
            iconRes = R.drawable.ic_track,
            onClick = { onThemeSwitch(!isDarkEnabled) },
            trailing = {
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                    Switch(
                        modifier = Modifier.scale(0.7f),
                        checked = isDarkEnabled,
                        onCheckedChange = onThemeSwitch,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                            uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                            uncheckedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                            checkedBorderColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        )
        LabeledIconRow(
            text = stringResource(R.string.share_app_option),
            iconRes = R.drawable.ic_share,
            onClick = onShare
        )
        LabeledIconRow(
            text = stringResource(R.string.call_support_option),
            iconRes = R.drawable.ic_support,
            onClick = onSupport
        )
        LabeledIconRow(
            text = stringResource(R.string.license_option),
            iconRes = R.drawable.ic_arrow_forward_24,
            onClick = onLicense
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    PlaylistMakerTheme(isDarkEnabled = false) {
        SettingsContent(
            isDarkEnabled = true,
            onThemeSwitch = {},
            onShare = {},
            onSupport = {},
            onLicense = {}
        )
    }
}
