package com.practicum.playlistmaker.media.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.media.ui.model.PlaylistUi
import com.practicum.playlistmaker.media.ui.view_model.PlaylistState
import com.practicum.playlistmaker.media.ui.view_model.PlaylistViewModel
import com.practicum.playlistmaker.ui.BlackRoundedButton
import com.practicum.playlistmaker.ui.PlaceholderMessage
import com.practicum.playlistmaker.ui.PlaylistGray
import com.practicum.playlistmaker.ui.YandexSansRegular

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistViewModel,
    onPlaylistClick: (Int) -> Unit,
    onNewPlaylistClick: () -> Unit
) {
    val state by viewModel.state.observeAsState(PlaylistState.Empty)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BlackRoundedButton(
            text = stringResource(R.string.create_playlist),
            onClick = onNewPlaylistClick,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 24.dp)
        )

        when (val s = state) {
            is PlaylistState.Content -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(
                        items = s.playlists,
                        key = { it.id },
                        contentType = { "playlist" }
                    ) { playlist ->
                        PlaylistItem(playlist = playlist, onClick = { onPlaylistClick(playlist.id) })
                    }
                }
            }

            is PlaylistState.Empty -> {
                PlaceholderMessage(
                    imageRes = R.drawable.ic_nothing_found,
                    text = stringResource(R.string.playlist_placeholder),
                    modifier = Modifier.padding(top = 106.dp)
                )
            }

            else -> {}
        }
    }
}

@Composable
fun PlaylistItem(
    playlist: PlaylistUi,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(bottom = 8.dp)
    ) {
        AsyncImage(
            model = playlist.coverUri,
            contentDescription = null,
            placeholder = painterResource(R.drawable.ic_track_placeholder_312),
            error = painterResource(R.drawable.ic_track_placeholder_312),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Text(
            text = playlist.name,
            modifier = Modifier.padding(top = 4.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = YandexSansRegular,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = context.resources.getQuantityString(
                R.plurals.tracks_count,
                playlist.tracksCount,
                playlist.tracksCount
            ),
            color = PlaylistGray,
            fontFamily = YandexSansRegular,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
