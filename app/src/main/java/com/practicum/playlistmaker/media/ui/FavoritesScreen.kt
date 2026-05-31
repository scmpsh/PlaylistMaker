package com.practicum.playlistmaker.media.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.ui.view_model.FavoriteTracksViewModel
import com.practicum.playlistmaker.search.ui.view_model.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchUiItem
import com.practicum.playlistmaker.sharing.domain.model.Track
import com.practicum.playlistmaker.ui.PlaceholderMessage
import com.practicum.playlistmaker.ui.TrackRow

@Composable
fun FavoritesScreen(
    viewModel: FavoriteTracksViewModel,
    onTrackClick: (Track) -> Unit
) {
    val state by viewModel.stateLiveData.observeAsState(SearchState.Loading)

    when (val s = state) {
        is SearchState.Content -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(top = 16.dp)
            ) {
                items(
                    items = s.items,
                    key = { item ->
                        when (item) {
                            is SearchUiItem.TrackItem -> item.track.trackId
                            is SearchUiItem.ClearHistoryItem -> "clear_history"
                        }
                    },
                    contentType = { item ->
                        when (item) {
                            is SearchUiItem.TrackItem -> "track"
                            is SearchUiItem.ClearHistoryItem -> "button"
                        }
                    }
                ) { item ->
                    if (item is SearchUiItem.TrackItem) {
                        TrackRow(track = item.track, onClick = onTrackClick)
                    }
                }
            }
        }

        is SearchState.Empty -> {
            PlaceholderMessage(
                imageRes = R.drawable.ic_nothing_found,
                text = stringResource(R.string.favorites_placeholder),
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(top = 106.dp)
            )
        }

        else -> {}
    }
}
