package com.practicum.playlistmaker.media.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.api.FavoriteTrackInteractor
import com.practicum.playlistmaker.search.ui.view_model.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchUiItem
import kotlinx.coroutines.flow.map

class FavoriteTracksViewModel(
    private val interactor: FavoriteTrackInteractor
) : ViewModel() {
    val stateLiveData: LiveData<SearchState> = interactor.findAllFavoriteTracks()
        .map { tracks ->
            if (tracks.isEmpty()) SearchState.Empty
            else SearchState.Content(tracks.map { SearchUiItem.TrackItem(it) })
        }
        .asLiveData(viewModelScope.coroutineContext)
}