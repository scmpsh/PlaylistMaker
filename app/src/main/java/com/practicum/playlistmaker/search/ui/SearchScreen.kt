package com.practicum.playlistmaker.search.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.ui.view_model.SearchState
import com.practicum.playlistmaker.search.ui.view_model.SearchUiItem
import com.practicum.playlistmaker.search.ui.view_model.SearchViewModel
import com.practicum.playlistmaker.sharing.domain.model.Track
import com.practicum.playlistmaker.ui.BlackRoundedButton
import com.practicum.playlistmaker.ui.PlaceholderMessage
import com.practicum.playlistmaker.ui.ScreenTitle
import com.practicum.playlistmaker.ui.TrackRow
import com.practicum.playlistmaker.ui.YandexSansMedium
import com.practicum.playlistmaker.ui.YandexSansRegular

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onTrackClick: (Track) -> Unit
) {
    val searchState by viewModel.observeSearchState()
        .observeAsState(SearchState.Content(emptyList()))
    var text by rememberSaveable { mutableStateOf("") }
    var hasTyped by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onSearchTextChanged(text)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenTitle(text = stringResource(R.string.search_title))
        SearchInput(
            value = text,
            onValueChange = { value ->
                text = value
                hasTyped = true
                viewModel.onSearchTextChanged(value)
            },
            onClearClick = {
                text = ""
                viewModel.onSearchTextChanged("")
            },
            onSearch = { viewModel.onSearchTextChanged(text, force = true) }
        )

        when (val state = searchState) {
            is SearchState.Loading -> SearchProgress()
            is SearchState.Content -> SearchContent(
                items = state.items,
                showHistoryTitle = text.isBlank() && state.items.isNotEmpty(),
                onTrackClick = { track ->
                    viewModel.onTrackClicked(track)
                    onTrackClick(track)
                },
                onClearHistoryClick = viewModel::onClearHistoryClicked
            )

            is SearchState.Empty -> if (hasTyped && text.isNotBlank()) {
                PlaceholderMessage(
                    imageRes = R.drawable.ic_nothing_found,
                    text = stringResource(R.string.nothing_found),
                    modifier = Modifier.padding(top = 102.dp)
                )
            }

            is SearchState.Error -> if (hasTyped && text.isNotBlank()) {
                SearchError(onRetryClick = { viewModel.onSearchTextChanged(text, force = true) })
            }
        }
    }
}

@Composable
private fun SearchInput(
    value: String,
    onValueChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontFamily = YandexSansRegular,
                fontSize = 16.sp
            ),
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSearch() }),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 13.dp, end = if (value.isEmpty()) 13.dp else 40.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_search_input_16),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            Text(
                                text = stringResource(R.string.search_hint),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontFamily = YandexSansRegular,
                                fontSize = 16.sp
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
        if (value.isNotEmpty()) {
            Image(
                painter = painterResource(R.drawable.ic_clear_button_16),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp)
                    .size(24.dp)
                    .clickable { onClearClick() }
                    .padding(4.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }
    }
}

@Composable
private fun SearchProgress() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 140.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(44.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 4.dp
        )
    }
}

@Composable
private fun SearchContent(
    items: List<SearchUiItem>,
    showHistoryTitle: Boolean,
    onTrackClick: (Track) -> Unit,
    onClearHistoryClick: () -> Unit
) {
    if (showHistoryTitle) {
        Text(
            text = stringResource(R.string.you_are_search),
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 42.dp, bottom = 4.dp),
            color = MaterialTheme.colorScheme.onBackground,
            fontFamily = YandexSansMedium,
            fontSize = 19.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
    LazyColumn(modifier = Modifier.padding(top = if (showHistoryTitle) 0.dp else 16.dp)) {
        items(
            items = items,
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
            when (item) {
                is SearchUiItem.TrackItem -> TrackRow(item.track, onTrackClick)
                is SearchUiItem.ClearHistoryItem -> ClearHistoryButton(onClearHistoryClick)
            }
        }
    }
}

@Composable
private fun ClearHistoryButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        BlackRoundedButton(text = stringResource(R.string.clean_history), onClick = onClick)
    }
}

@Composable
private fun SearchError(onRetryClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 102.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlaceholderMessage(
            imageRes = R.drawable.ic_no_internet,
            text = stringResource(R.string.something_wrong)
        )
        Spacer(modifier = Modifier.height(24.dp))
        BlackRoundedButton(text = stringResource(R.string.update), onClick = onRetryClick)
    }
}
