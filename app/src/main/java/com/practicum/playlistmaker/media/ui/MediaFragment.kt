package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.player.ui.AudioPlayerFragment
import com.practicum.playlistmaker.player.ui.mapper.toUi
import com.practicum.playlistmaker.settings.ui.view_model.SettingsViewModel
import com.practicum.playlistmaker.ui.PlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MediaFragment : Fragment() {
    private val settingsViewModel by viewModel<SettingsViewModel>()
    private val favoritesViewModel by viewModel<FavoriteTracksViewModel>()
    private val playlistsViewModel by viewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            val settings by settingsViewModel.observeSettingsState().observeAsState()
            PlaylistMakerTheme(isDarkEnabled = settings?.isDarkEnabled == true) {
                MediaScreen(
                    favoritesViewModel = favoritesViewModel,
                    playlistsViewModel = playlistsViewModel,
                    onTrackClick = { track ->
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_audioPlayerFragment,
                            AudioPlayerFragment.createArgs(track.toUi())
                        )
                    },
                    onPlaylistClick = { playlistId ->
                        findNavController().navigate(
                            R.id.action_mediaFragment_to_playlistDetailsFragment,
                            PlaylistDetailsFragment.createArgs(playlistId)
                        )
                    },
                    onNewPlaylistClick = {
                        findNavController().navigate(R.id.action_mediaFragment_to_createPlaylistFragment)
                    }
                )
            }
        }
    }
}
