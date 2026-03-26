package com.practicum.playlistmaker.media.ui.view_model

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.api.CoverStorageInteractor
import com.practicum.playlistmaker.media.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.dto.Playlist
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    private val playlistId: Int,
    playlistInteractor: PlaylistInteractor,
    coverStorageInteractor: CoverStorageInteractor
) : PlaylistViewModel(playlistInteractor, coverStorageInteractor) {

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    init {
        postPlaylistById(playlistId)
    }

    private fun postPlaylistById(playlistId: Int) {
        viewModelScope.launch {
            _playlist.postValue(playlistInteractor.getPlaylistById(playlistId))
        }
    }

    fun updatePlaylist(
        playlistName: String,
        playlistDescription: String,
        uri: Uri?
    ) {
        viewModelScope.launch {
            val currentPlaylist = _playlist.value ?: playlistInteractor.getPlaylistById(playlistId)

            val imagePath = if (uri != null) {
                coverStorageInteractor.saveCover(uri)
            } else {
                currentPlaylist.coverImagePath
            }

            playlistInteractor.updatePlaylist(
                currentPlaylist.copy(
                    name = playlistName,
                    description = playlistDescription,
                    coverImagePath = imagePath
                )
            )

            postPlaylistById(currentPlaylist.id)
        }
    }
}
