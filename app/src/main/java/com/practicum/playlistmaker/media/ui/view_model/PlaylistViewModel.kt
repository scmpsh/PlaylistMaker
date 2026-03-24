package com.practicum.playlistmaker.media.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.api.CoverStorageInteractor
import com.practicum.playlistmaker.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.dto.Playlist
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val coverStorageInteractor: CoverStorageInteractor
) : ViewModel() {
    val state: LiveData<PlaylistState> = playlistInteractor.findAllPlaylists()
        .map { playlists ->
            if (playlists.isEmpty()) PlaylistState.Empty
            else PlaylistState.Content(playlists)
        }
        .asLiveData(viewModelScope.coroutineContext)

    fun addPlaylist(
        playlistName: String,
        playlistDescription: String,
        uri: Uri?
    ) {
        viewModelScope.launch {
            val imagePath = uri?.let {
                coverStorageInteractor.saveCover(it)
            }.orEmpty()

            playlistInteractor.addPlaylist(
                Playlist(
                    id = 0,
                    name = playlistName,
                    description = playlistDescription,
                    coverImagePath = imagePath,
                    tracks = emptyList(),
                    tracksCount = 0
                )
            )
        }
    }
}
