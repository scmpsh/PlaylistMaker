package com.practicum.playlistmaker.search.ui.view_model

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface SearchUiItem {
    data class TrackItem(val track: Track) : SearchUiItem
    object ClearHistoryItem : SearchUiItem
}