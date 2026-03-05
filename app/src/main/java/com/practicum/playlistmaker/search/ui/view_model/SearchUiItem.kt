package com.practicum.playlistmaker.search.ui.view_model

import com.practicum.playlistmaker.sharing.domain.model.Track

sealed interface SearchUiItem {
    data class TrackItem(val track: Track) : SearchUiItem
    object ClearHistoryItem : SearchUiItem
}