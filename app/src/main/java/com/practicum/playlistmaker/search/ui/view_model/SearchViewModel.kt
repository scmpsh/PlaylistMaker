package com.practicum.playlistmaker.search.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
) : ViewModel() {

    private var latestSearchText: String? = null
    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeSearchState(): LiveData<SearchState> = stateLiveData

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            searchRequest(changedText)
        }

    private fun searchRequest(inputText: String) {
        if (latestSearchText.isNullOrBlank()) {
            return
        }

        renderState(SearchState.Loading)

        viewModelScope.launch {
            searchInteractor.searchTracks(inputText).collect { pair ->
                val (foundTracks, errorMessage) = pair

                val tracks = mutableListOf<Track>()
                if (foundTracks != null) {
                    tracks.addAll(foundTracks)
                }

                when {
                    errorMessage != null -> {
                        renderState(SearchState.Error)
                    }

                    tracks.isEmpty() -> {
                        renderState(SearchState.Empty)
                    }

                    else -> {
                        renderState(
                            SearchState.Content(
                                items = tracks.map { SearchUiItem.TrackItem(it) },
                            )
                        )
                    }
                }
            }
        }
    }

    fun onSearchTextChanged(inputText: String, force: Boolean = false) {
        if (inputText.isBlank()) {
            latestSearchText = ""
            showHistory()
            return
        }

        if (force || inputText != latestSearchText) {
            latestSearchText = inputText
            trackSearchDebounce(inputText)
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    private fun showHistory() {
        val history = searchHistoryInteractor.getHistory()
            ?.map { SearchUiItem.TrackItem(it) }
            ?.plus(SearchUiItem.ClearHistoryItem)
            .orEmpty()

        if (history.isNotEmpty()) {
            stateLiveData.postValue(SearchState.Content(history))
        }
    }

    fun onTrackClicked(track: Track) {
        searchHistoryInteractor.saveToHistory(track)
        if (latestSearchText.isNullOrBlank()) {
            showHistory()
        }
    }

    fun onClearHistoryClicked() {
        searchHistoryInteractor.cleanHistory()
        stateLiveData.postValue(SearchState.Content(emptyList()))
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}