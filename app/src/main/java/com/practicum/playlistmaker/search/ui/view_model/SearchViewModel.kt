package com.practicum.playlistmaker.search.ui.view_model

import android.os.Handler
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.practicum.playlistmaker.search.domain.api.SearchInteractor
import com.practicum.playlistmaker.search.domain.models.Track

class SearchViewModel(
    private val searchInteractor: SearchInteractor,
    private val searchHistoryInteractor: SearchHistoryInteractor,
    private val handler: Handler,
) : ViewModel() {
    private var latestSearchText: String? = null

    private var isClickAllowed = true

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeSearchState(): LiveData<SearchState> = stateLiveData


    private fun searchRequest(inputText: String) {
        if (inputText.isBlank()) {
            return
        }

        renderState(SearchState.Loading)

        searchInteractor.searchTracks(
            inputText, object : SearchInteractor.SearchConsumer {
                override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                    handler.post {

                        if (latestSearchText != inputText) {
                            return@post
                        }

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
        )
    }

    private fun debounceSearch(inputText: String, force: Boolean = false) {
        if (!force && latestSearchText == inputText) {
            return
        }

        latestSearchText = inputText
        cancelSearch()

        val searchRunnable = Runnable {
            searchRequest(inputText)
        }
        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(searchRunnable, SEARCH_REQUEST_TOKEN, postTime)
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun onSearchTextChanged(inputText: String) {
        if (inputText.isBlank()) {
            latestSearchText = inputText
            cancelSearch()
            showHistory()
            return
        }

        debounceSearch(inputText)
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

    private fun cancelSearch() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun onRetryClicked() {
        latestSearchText?.let { debounceSearch(it, force = true) }
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

    fun clickDebounce(): Boolean {
        val current = isClickAllowed

        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed(
                { isClickAllowed = true },
                CLICK_DEBOUNCE_DELAY
            )
        }
        return current
    }


    override fun onCleared() {
        super.onCleared()
        cancelSearch()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L

        private val SEARCH_REQUEST_TOKEN = Any()

        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}