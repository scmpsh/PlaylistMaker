package com.practicum.playlistmaker.media.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.api.CoverStorageInteractor
import com.practicum.playlistmaker.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.dto.Playlist
import kotlinx.coroutines.launch

open class PlaylistViewModel(
    protected val playlistInteractor: PlaylistInteractor,
    protected val coverStorageInteractor: CoverStorageInteractor
) : ViewModel() {

    protected val _state = MutableLiveData<PlaylistState>()
    open val state: LiveData<PlaylistState> = _state

    init {
        fillData()
    }

    protected open fun fillData() {
        viewModelScope.launch {
            playlistInteractor.findAllPlaylists().collect { playlists ->
                if (playlists.isEmpty()) {
                    _state.postValue(PlaylistState.Empty)
                } else {
                    _state.postValue(PlaylistState.Content(playlists))
                }
            }
        }
    }

    open fun addPlaylist(
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
