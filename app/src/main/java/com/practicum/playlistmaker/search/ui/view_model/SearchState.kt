package com.practicum.playlistmaker.search.ui.view_model

sealed interface SearchState {
    object Loading : SearchState
    data class Content(val items: List<SearchUiItem>) : SearchState
    object Error : SearchState
    object Empty : SearchState
}
